package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApi;
import org.opendatamesh.dpds.datastoreapi.v1.parser.DataStoreApiParser;
import org.opendatamesh.dpds.datastoreapi.v1.parser.DataStoreApiParserFactory;
import org.opendatamesh.dpds.datastoreapi.v1.visitor.DataStoreApiVisitor;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionQualityExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

@Component
public class PortDatastoreApiEntitiesExtractor implements PortStandardDefinitionEntitiesExtractor, PortStandardDefinitionQualityExtractor {

    private final SemanticLinkManager semanticLinkManager;
    private final BdDataProductConfig bdDataProductConfig;

    private final String SPECIFICATION = "datastoreapi";
    private final String VERSION = "1.*.*";

    @Autowired
    public PortDatastoreApiEntitiesExtractor(SemanticLinkManager semanticLinkManager, BdDataProductConfig bdDataProductConfig) {
        this.semanticLinkManager = semanticLinkManager;
        this.bdDataProductConfig = bdDataProductConfig;
    }

    @Override
    public boolean supports(StandardDefinition portStandardDefinition) {
        return SPECIFICATION.equalsIgnoreCase(portStandardDefinition.getSpecification()) &&
                portStandardDefinition.getSpecificationVersion().matches(VERSION);
    }

    @Override
    public List<QualityCheck> extractQualityChecks(StandardDefinition portStandardDefinition) {
        try {
            DataStoreApiParser parser = DataStoreApiParserFactory.getParser()
                    .register(new DataStoreApiBlindataDefinitionConverter());
            DataStoreApi dataStoreApi = parser.deserialize(new ObjectMapper().valueToTree(portStandardDefinition.getDefinition()));

            List<QualityCheck> qualityChecks = new ArrayList<>();
            if (dataStoreApi.getSchema() == null) {
                getUseCaseLogger().warn("Data product port has empty schema, skipping quality checks extraction");
                return qualityChecks;
            }
            DataStoreApiVisitor visitor = new DataStoreApiVisitorImpl(semanticLinkManager, NO_OP_ENTITIES(), qualityChecks::add, bdDataProductConfig);
            dataStoreApi.getSchema().accept(visitor);
            return qualityChecks;
        } catch (MismatchedInputException e) {
            getUseCaseLogger().warn("Malformed port schema definition: " + e.getMessage(), e);
            return Collections.emptyList();
        } catch (IOException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<BDPhysicalEntityRes> extractEntities(StandardDefinition portStandardDefinition) {
        try {
            DataStoreApiParser parser = DataStoreApiParserFactory.getParser()
                    .register(new DataStoreApiBlindataDefinitionConverter());
            DataStoreApi dataStoreApi = parser.deserialize(new ObjectMapper().valueToTree(portStandardDefinition.getDefinition()));
            List<BDPhysicalEntityRes> physicalEntities = new ArrayList<>();
            if (dataStoreApi.getSchema() == null) {
                getUseCaseLogger().warn("Data product port has empty schema, skipping entities extraction");
                return physicalEntities;
            }
            DataStoreApiVisitor visitor = new DataStoreApiVisitorImpl(semanticLinkManager, physicalEntities::add, NO_OP_QUALITY_DEFINITIONS(), bdDataProductConfig);
            dataStoreApi.getSchema().accept(visitor);
            return physicalEntities;
        } catch (MismatchedInputException e) {
            getUseCaseLogger().warn("Malformed port schema definition: " + e.getMessage(), e);
            return Collections.emptyList();
        } catch (IOException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private DataStoreApiVisitorQualityDefinitionsPresenter NO_OP_QUALITY_DEFINITIONS() {
        return s -> {
        };
    }

    private DataStoreApiVisitorEntitiesPresenter NO_OP_ENTITIES() {
        return s -> {
        };
    }
}
