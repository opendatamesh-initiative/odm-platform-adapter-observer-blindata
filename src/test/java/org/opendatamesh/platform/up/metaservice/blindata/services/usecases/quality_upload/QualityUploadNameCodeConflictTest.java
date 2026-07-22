package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponents;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.ValidatorUseCaseLogger;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Feature: Quality check name/code conflict detection on QUALITY_UPLOAD
 * In order to avoid opaque Blindata create failures when renaming quality.name without aligning display identity
 * As the Blindata observer
 * I want to detect same-name / different-code collisions within a Quality Suite and emit clear warnings
 * So that the observer validator fails policy evaluation with remediable messages before publish
 * <p>
 * Background:
 * Given a data product in domain "sales" with name "orders"
 * And the Quality Suite code is "sales - orders"
 * And QUALITY_UPLOAD extracts quality checks from descriptor ports
 * And quality check codes are prefixed with the suite code before upload
 * And Blindata upsert identity for quality checks is by code within the suite
 * And Blindata enforces unique quality check name within the suite
 */
@ExtendWith(MockitoExtension.class)
public class QualityUploadNameCodeConflictTest {

    private static final String SUITE_CODE = "sales - orders";
    private static final String SUITE_UUID = "suite-uuid";

    @Mock
    private QualityUploadBlindataOutboundPort blindataOutboundPort;
    @Mock
    private QualityUploadOdmOutboundPort odmOutboundPort;
    @Mock
    private UseCaseLogger mockLogger;

    private UseCaseLogger originalLogger;

    @BeforeEach
    void setUpLogger() {
        originalLogger = UseCaseLoggerContext.getUseCaseLogger();
        UseCaseLoggerContext.setUseCaseLogger(mockLogger);
    }

    @AfterEach
    void restoreLogger() {
        UseCaseLoggerContext.setUseCaseLogger(originalLogger);
    }

    /**
     * Scenario: AC1-AC3 — renamed technical name with unchanged displayName is detected and warned
     * Given Blindata already has Quality Suite "sales - orders"
     * And the suite contains a Quality Check with code "sales - orders - old_rule" and name "Customer Completeness"
     * And the descriptor quality object has name "new_rule" without quality.id
     * And customProperties.displayName is "Customer Completeness"
     * When QUALITY_UPLOAD runs
     * Then the observer resolves the suite on Blindata by code "sales - orders"
     * And it detects a name/code conflict between incoming code "sales - orders - new_rule" / name "Customer Completeness"
     * and existing code "sales - orders - old_rule" / name "Customer Completeness"
     * And it emits a use-case warn identifying both checks
     * And the warn suggests updating customProperties.displayName or using a stable quality.id
     */
    @Test
    void testConflictWarnsWhenSameNameDifferentCode() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("new_rule", "Customer Completeness")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - old_rule", "Customer Completeness")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        ArgumentCaptor<String> warnCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger, atLeastOnce()).warn(warnCaptor.capture());
        assertThat(warnCaptor.getAllValues()).anySatisfy(message -> {
            assertThat(message).contains("[#121]");
            assertThat(message).contains("sales - orders - new_rule");
            assertThat(message).contains("sales - orders - old_rule");
            assertThat(message).contains("Customer Completeness");
            assertThat(message).contains("customProperties.displayName");
            assertThat(message).contains("quality.id");
        });
        verify(blindataOutboundPort).findQualitySuiteByCode(SUITE_CODE);
        verify(blindataOutboundPort).uploadQuality(any(), any());
    }

    /**
     * Scenario: AC2 — verify no Blindata check exists with same name and different code
     * Given Blindata already has Quality Suite "sales - orders"
     * And the suite contains Quality Check code "sales - orders - rule_a" name "Rule A"
     * And the incoming check has code "sales - orders - rule_a" and name "Rule A"
     * When QUALITY_UPLOAD runs the conflict guard
     * Then no name/code conflict warning is emitted for that check
     */
    @Test
    void testNoWarnWhenSameNameSameCode() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("rule_a", "Rule A")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - rule_a", "Rule A")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verifyNoConflictWarn();
        verify(blindataOutboundPort).uploadQuality(any(), any());
    }

    /**
     * Scenario: AC3 — collect all conflicts in one upload
     * Given Blindata already has Quality Suite "sales - orders" with two existing checks
     * And the descriptor produces two conflicting incoming checks
     * When QUALITY_UPLOAD runs the conflict guard
     * Then two distinct use-case warnings are emitted
     * And each warning identifies its conflicting pair
     */
    @Test
    void testCollectsAllConflicts() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Arrays.asList(
                qualityCheck("new_one", "Name One"),
                qualityCheck("new_two", "Name Two")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Arrays.asList(
                existingCheck("sales - orders - old_one", "Name One"),
                existingCheck("sales - orders - old_two", "Name Two")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        ArgumentCaptor<String> warnCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLogger, atLeastOnce()).warn(warnCaptor.capture());
        List<String> conflictWarns = warnCaptor.getAllValues().stream()
                .filter(message -> message.contains("[#121]"))
                .collect(Collectors.toList());
        assertThat(conflictWarns).hasSize(2);
        assertThat(conflictWarns.get(0)).contains("Name One").contains("old_one").contains("new_one");
        assertThat(conflictWarns.get(1)).contains("Name Two").contains("old_two").contains("new_two");
    }

    /**
     * Scenario: AC4 — happy path upload/update by code continues without conflict warn
     * Given Blindata already has Quality Suite "sales - orders"
     * And the suite contains Quality Check code "sales - orders - stable" name "Stable Name"
     * And the incoming check has code "sales - orders - stable" and name "Stable Name"
     * When QUALITY_UPLOAD completes
     * Then no name/code conflict warning is emitted
     * And uploadQuality is invoked with the prefixed checks
     */
    @Test
    void testHappyPathSameIdentityUploadsWithoutConflictWarn() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("stable", "Stable Name")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - stable", "Stable Name")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verifyNoConflictWarn();
        verify(blindataOutboundPort).uploadQuality(any(), any());
    }

    /**
     * Scenario: AC4b — intentional rename of both technical name and display name creates new check identity without this conflict
     * Given Blindata already has Quality Suite "sales - orders"
     * And the suite contains Quality Check code "sales - orders - old_rule" name "Old Display"
     * And the descriptor quality object has name "new_rule"
     * And customProperties.displayName is "New Display"
     * When QUALITY_UPLOAD runs the conflict guard
     * Then no name/code conflict warning is emitted for that check
     */
    @Test
    void testNoWarnWhenBothNameAndDisplayChange() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("new_rule", "New Display")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - old_rule", "Old Display")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verifyNoConflictWarn();
    }

    /**
     * Scenario: AC5 — first upload / suite absent is a no-op for the guard
     * Given Blindata does not contain Quality Suite "sales - orders"
     * And the descriptor defines one or more quality checks
     * When QUALITY_UPLOAD runs the conflict guard
     * Then findQualitySuiteByCode returns empty
     * And no name/code conflict warning is emitted
     * And upload proceeds as today
     */
    @Test
    void testSuiteAbsentIsNoOp() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("new_rule", "Customer Completeness")
        ));
        when(blindataOutboundPort.findQualitySuiteByCode(SUITE_CODE)).thenReturn(Optional.empty());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, never()).findQualityChecks(any());
        verifyNoConflictWarn();
        verify(blindataOutboundPort).uploadQuality(any(), any());
    }

    /**
     * Scenario: Fuzzy Blindata search must not false-positive without exact name match
     * Given Blindata suite-scoped search returns a check whose name only partially matches the incoming name
     * When the conflict guard post-filters by exact name equality
     * Then no conflict warning is emitted for that partial match
     */
    @Test
    void testExactNameMatchRequired() throws UseCaseExecutionException {
        stubDataProductAndUpload();
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("new_rule", "Customer Completeness")
        ));
        stubExistingSuite();
        when(blindataOutboundPort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - other", "Customer")
        ));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verifyNoConflictWarn();
    }

    /**
     * Scenario: AC6 — observer validator dry-run surfaces conflict and fails policy evaluation
     * Given QUALITY_UPLOAD runs under ValidatorUseCaseLogger (validator dry-run path)
     * And Blindata already has Quality Suite "sales - orders" with an existing same-name / different-code check
     * And the descriptor produces a conflicting incoming check
     * And dry-run stubs uploadQuality but still performs suite/check read lookups
     * When the use case executes
     * Then ValidatorUseCaseLogger collects the conflict warning(s)
     * And rawError-equivalent warning list contains [#121]
     */
    @Test
    void testValidatorLoggerCollectsConflictOnDryRun() throws UseCaseExecutionException {
        QualityUploadBlindataOutboundPort livePort = mock(QualityUploadBlindataOutboundPort.class);
        QualityUploadBlindataOutboundPort dryRunPort = new QualityUploadBlindataOutboundPortDryRunImpl(livePort);

        DataProductVersion dpv = dataProductVersion();
        when(odmOutboundPort.getDataProductVersion()).thenReturn(dpv);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(Collections.singletonList(
                qualityCheck("new_rule", "Customer Completeness")
        ));
        when(livePort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(livePort.findQualitySuiteByCode(SUITE_CODE)).thenReturn(Optional.of(existingSuite()));
        when(livePort.findQualityChecks(any())).thenReturn(Collections.singletonList(
                existingCheck("sales - orders - old_rule", "Customer Completeness")
        ));

        ValidatorUseCaseLogger validatorLogger = new ValidatorUseCaseLogger();
        UseCaseLoggerContext.setUseCaseLogger(validatorLogger);
        try {
            new QualityUpload(dryRunPort, odmOutboundPort).execute();
        } finally {
            UseCaseLoggerContext.setUseCaseLogger(mockLogger);
        }

        assertThat(validatorLogger.getWarnings()).anySatisfy(message ->
                assertThat(message).contains("[#121]").contains("Customer Completeness")
        );
        verify(livePort).findQualitySuiteByCode(SUITE_CODE);
        verify(livePort).findQualityChecks(any(QualityCheckSearchOptions.class));
        verify(livePort, never()).uploadQuality(any(), any());
    }

    private void stubDataProductAndUpload() {
        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion());
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.uploadQuality(any(), any())).thenReturn(new BDQualityUploadResultsRes());
    }

    private void stubExistingSuite() {
        when(blindataOutboundPort.findQualitySuiteByCode(SUITE_CODE)).thenReturn(Optional.of(existingSuite()));
    }

    private void verifyNoConflictWarn() {
        verify(mockLogger, never()).warn(argThat(message -> message != null && message.contains("[#121]")));
    }

    private DataProductVersion dataProductVersion() {
        DataProductVersion dataProductVersion = new DataProductVersion();
        Info info = new Info();
        info.setFullyQualifiedName("urn:dp:sales:orders");
        info.setDomain("sales");
        info.setName("orders");
        dataProductVersion.setInfo(info);
        InterfaceComponents interfaceComponents = new InterfaceComponents();
        dataProductVersion.setInterfaceComponents(interfaceComponents);
        return dataProductVersion;
    }

    private QualityCheck qualityCheck(String code, String name) {
        QualityCheck qualityCheck = new QualityCheck();
        qualityCheck.setCode(code);
        qualityCheck.setName(name);
        qualityCheck.setSuccessThreshold(BigDecimal.valueOf(100));
        qualityCheck.setWarningThreshold(BigDecimal.valueOf(80));
        return qualityCheck;
    }

    private BDQualitySuiteRes existingSuite() {
        BDQualitySuiteRes suite = new BDQualitySuiteRes();
        suite.setUuid(SUITE_UUID);
        suite.setCode(SUITE_CODE);
        suite.setName(SUITE_CODE);
        return suite;
    }

    private BDQualityCheckRes existingCheck(String code, String name) {
        BDQualityCheckRes check = new BDQualityCheckRes();
        check.setCode(code);
        check.setName(name);
        return check;
    }
}
