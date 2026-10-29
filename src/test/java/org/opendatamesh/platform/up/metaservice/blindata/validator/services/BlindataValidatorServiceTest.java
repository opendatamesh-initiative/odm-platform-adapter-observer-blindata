package org.opendatamesh.platform.up.metaservice.blindata.validator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventAdapter;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload.ProbesUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload.QualityUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents.BlindataProperties;
import org.opendatamesh.platform.up.metaservice.blindata.utils.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationResultRes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Feature: optional use cases in the Blindata policy validator
 * PROBES_UPLOAD is optional: when no event handler activates it, the validator must not evaluate probe requirements,
 * otherwise it would block the publish of data products for a use case the deployment never opted into.
 */
@ExtendWith(MockitoExtension.class)
class BlindataValidatorServiceTest {

    @Mock
    private DataProductUploadFactory dataProductUploadFactory;
    @Mock
    private DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory;
    @Mock
    private QualityUploadFactory qualityUploadFactory;
    @Mock
    private ProbesUploadFactory probesUploadFactory;
    @Mock
    private EventAdapter eventAdapter;
    @Mock
    private UseCase dryRunUseCase;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Scenario: PROBES_UPLOAD is not active
     * Given no event handler lists PROBES_UPLOAD among its active use cases
     * When the validator evaluates a data product version with interface components
     * Then the probes dry run is never initialised
     * And the evaluation passes
     */
    @Test
    void probesAreNotValidatedWhenProbesUploadIsNotActive() throws IOException {
        BlindataValidatorService service = serviceWithActiveUseCases("DATA_PRODUCT_UPLOAD", "DATA_PRODUCT_VERSION_UPLOAD", "QUALITY_UPLOAD");
        stubDryRuns();

        OdmValidatorPolicyEvaluationResultRes result = service.validateDataProduct(dataProductVersionWithProbes());

        verifyNoInteractions(probesUploadFactory);
        assertThat(result.getEvaluationResult()).isTrue();
    }

    /**
     * Scenario: PROBES_UPLOAD is active
     * Given an event handler lists PROBES_UPLOAD among its active use cases
     * When the validator evaluates a data product version with interface components
     * Then the probes dry run is executed
     */
    @Test
    void probesAreValidatedWhenProbesUploadIsActive() throws IOException {
        BlindataValidatorService service = serviceWithActiveUseCases("DATA_PRODUCT_UPLOAD", "DATA_PRODUCT_VERSION_UPLOAD", "QUALITY_UPLOAD", "PROBES_UPLOAD");
        stubDryRuns();
        when(probesUploadFactory.getUseCaseDryRun(any())).thenReturn(dryRunUseCase);

        service.validateDataProduct(dataProductVersionWithProbes());

        verify(probesUploadFactory).getUseCaseDryRun(any());
        verify(dryRunUseCase, times(4)).execute();
    }

    private BlindataValidatorService serviceWithActiveUseCases(String... activeUseCases) {
        BlindataProperties.EventHandler eventHandler = new BlindataProperties.EventHandler();
        eventHandler.setEventType("DATA_PRODUCT_VERSION_CREATED");
        eventHandler.setActiveUseCases(List.of(activeUseCases));
        BlindataProperties blindataProperties = new BlindataProperties();
        blindataProperties.setEventHandlers(List.of(eventHandler));
        return new BlindataValidatorService(
                dataProductUploadFactory,
                dataProductPortsAndAssetsUploadFactory,
                qualityUploadFactory,
                probesUploadFactory,
                eventAdapter,
                blindataProperties
        );
    }

    private void stubDryRuns() {
        when(eventAdapter.odmToInternalEvent(any())).thenReturn(Optional.of(new Event()));
        when(dataProductUploadFactory.getUseCaseDryRun(any())).thenReturn(dryRunUseCase);
        when(dataProductPortsAndAssetsUploadFactory.getUseCaseDryRun(any())).thenReturn(dryRunUseCase);
        when(qualityUploadFactory.getUseCaseDryRun(any())).thenReturn(dryRunUseCase);
    }

    private OdmValidatorPolicyEvaluationRequestRes dataProductVersionWithProbes() throws IOException {
        return mapper.readValue(
                Resources.toByteArray(getClass().getResource("/org/opendatamesh/platform/up/metaservice/blindata/validator/controllers/valid_data_product_version_with_probes.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );
    }
}
