package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdProbesUploadConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityShortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProbesUploadOdmOutboundPortImplTest {

    private static final String DATA_PRODUCT_VERSION = "{"
            + "\"info\":{\"fullyQualifiedName\":\"urn:dpds:demo:dataproducts:probeSim:1\",\"domain\":\"demo\",\"name\":\"probeSim\",\"version\":\"1.0.0\"},"
            + "\"interfaceComponents\":{\"outputPorts\":[{"
            + "\"fullyQualifiedName\":\"urn:dpds:demo:dataproducts:probeSim:1:outputPorts:customers\","
            + "\"name\":\"customers\",\"x-blindataConnectionName\":\"Postgres\"}]}}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    @Mock
    private BdProbesUploadConfig config;
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

    @Test
    void entityLevelRuleWithReferenceStub_probeRunsOnTheTable() throws IOException {
        BDPhysicalEntityRes entity = physicalEntity();

        QualityCheck rowCount = libraryCheck("customers_row_count_positive");
        rowCount.setPhysicalEntities(Lists.newArrayList(entity));
        QualityCheck referenceStub = new QualityCheck();
        referenceStub.setCode("customers_row_count_positive");
        referenceStub.setReference(true);
        referenceStub.setPhysicalFields(Lists.newArrayList(physicalField(entity, "customer_id")));

        List<ProbeCandidate> candidates = extractCandidates(rowCount, referenceStub);

        assertThat(candidates).hasSize(1);
        ProbeCandidate candidate = candidates.get(0);
        assertThat(candidate.getProbeName()).isEqualTo("demo - probeSim - customers_row_count_positive");
        assertThat(candidate.getConnectionName()).isEqualTo("Postgres");
        assertThat(candidate.getPhysicalBinding().getSchema()).isEqualTo("public");
        assertThat(candidate.getPhysicalBinding().getObject()).isEqualTo("customers");
        assertThat(candidate.getPhysicalBinding().getProperty()).isNull();
    }

    @Test
    void fieldLevelRule_probeRunsOnTheDeclaredColumn() throws IOException {
        BDPhysicalEntityRes entity = physicalEntity();

        QualityCheck noNulls = libraryCheck("customer_id_no_nulls");
        noNulls.setPhysicalFields(Lists.newArrayList(physicalField(entity, "customer_id")));
        QualityCheck referenceStub = new QualityCheck();
        referenceStub.setCode("customer_id_no_nulls");
        referenceStub.setReference(true);
        referenceStub.setPhysicalFields(Lists.newArrayList(physicalField(entity, "email")));

        List<ProbeCandidate> candidates = extractCandidates(noNulls, referenceStub);

        assertThat(candidates).hasSize(1);
        assertThat(candidates.get(0).getPhysicalBinding().getObject()).isEqualTo("customers");
        assertThat(candidates.get(0).getPhysicalBinding().getProperty()).isEqualTo("customer_id");
    }

    @Test
    void ruleDeclaredTwiceWithTheSameCode_producesASingleProbe() throws IOException {
        BDPhysicalEntityRes entity = physicalEntity();

        QualityCheck first = libraryCheck("customers_row_count_positive");
        first.setPhysicalEntities(Lists.newArrayList(entity));
        QualityCheck second = libraryCheck("customers_row_count_positive");
        second.setPhysicalFields(Lists.newArrayList(physicalField(entity, "email")));

        List<ProbeCandidate> candidates = extractCandidates(first, second);

        assertThat(candidates).hasSize(1);
        assertThat(candidates.get(0).getPhysicalBinding().getProperty()).isNull();
    }

    @Test
    void usesTheChecksAsDeclared_withoutTheReferenceMerge() throws IOException {
        BDPhysicalEntityRes entity = physicalEntity();
        QualityCheck rowCount = libraryCheck("customers_row_count_positive");
        rowCount.setPhysicalEntities(Lists.newArrayList(entity));

        extractCandidates(rowCount);

        verify(dataProductPortAssetAnalyzer).extractDeclaredQualityChecksFromPorts(any());
        verify(dataProductPortAssetAnalyzer, never()).extractQualityChecksFromPorts(any());
    }

    private List<ProbeCandidate> extractCandidates(QualityCheck... qualityChecks) throws IOException {
        DataProductVersion dataProductVersion = objectMapper.readValue(DATA_PRODUCT_VERSION, DataProductVersion.class);

        when(config.getConnectionNamePropertyKey()).thenReturn("x-blindataConnectionName");
        when(dataProductPortAssetAnalyzer.extractDeclaredQualityChecksFromPorts(any()))
                .thenReturn(Arrays.asList(qualityChecks));

        return new ProbesUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, dataProductVersion, config)
                .extractProbeCandidates();
    }

    private QualityCheck libraryCheck(String code) {
        QualityCheck qualityCheck = new QualityCheck();
        qualityCheck.setCode(code);
        qualityCheck.setName(code);
        qualityCheck.setAdditionalProperties(Lists.newArrayList(
                additionalProperty("_contract.ruleType", "library"),
                additionalProperty("_contract.metric", "rowCount")
        ));
        return qualityCheck;
    }

    private BDAdditionalPropertiesRes additionalProperty(String name, String value) {
        BDAdditionalPropertiesRes additionalProperty = new BDAdditionalPropertiesRes();
        additionalProperty.setName(name);
        additionalProperty.setValue(value);
        return additionalProperty;
    }

    private BDPhysicalEntityRes physicalEntity() {
        BDPhysicalEntityRes entity = new BDPhysicalEntityRes();
        entity.setSchema("public");
        entity.setName("customers");
        return entity;
    }

    private BDPhysicalFieldRes physicalField(BDPhysicalEntityRes entity, String name) {
        BDPhysicalFieldRes field = new BDPhysicalFieldRes();
        field.setName(name);
        field.setPhysicalEntity(new BDPhysicalEntityShortRes(entity));
        return field;
    }
}
