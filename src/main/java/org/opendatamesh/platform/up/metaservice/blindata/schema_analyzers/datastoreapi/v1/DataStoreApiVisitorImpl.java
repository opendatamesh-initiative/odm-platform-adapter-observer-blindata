package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiDatabaseService;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiInfo;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiSchema;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiStandardDefinitionObject;
import org.opendatamesh.dpds.datastoreapi.v1.visitor.DataStoreApiVisitor;
import org.opendatamesh.dpds.visitors.core.StandardDefinitionVisitor;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class DataStoreApiVisitorImpl implements DataStoreApiVisitor {

    private final SemanticLinkManager semanticLinkManager;
    private final DataStoreApiVisitorEntitiesPresenter entitiesPresenter;
    private final DataStoreApiVisitorQualityDefinitionsPresenter qualityPresenter;
    private final BdDataProductConfig bdDataProductConfig;

    DataStoreApiVisitorImpl(SemanticLinkManager semanticLinkManager, DataStoreApiVisitorEntitiesPresenter entitiesPresenter, DataStoreApiVisitorQualityDefinitionsPresenter qualityPresenter, BdDataProductConfig bdDataProductConfig) {
        this.semanticLinkManager = semanticLinkManager;
        this.entitiesPresenter = entitiesPresenter;
        this.qualityPresenter = qualityPresenter;
        this.bdDataProductConfig = bdDataProductConfig;
    }

    @Override
    public void visit(DataStoreApiInfo dataStoreApiInfo) {
        //DO NOTHING
    }

    @Override
    public void visit(DataStoreApiDatabaseService dataStoreApiDatabaseService) {
        //DO NOTHING
    }

    @Override
    public void visit(DataStoreApiSchema dataStoreApiSchema) {
        String databaseSchemaName = dataStoreApiSchema.getDatabaseSchemaName();

        if (dataStoreApiSchema.getTables() != null) {
            for (DataStoreApiStandardDefinitionObject table : dataStoreApiSchema.getTables()) {
                StandardDefinitionVisitor<DataStoreApiBlindataDefinition> visitor =
                        new DataStoreApiStandardDefinitionVisitorImpl(entitiesPresenter, qualityPresenter, semanticLinkManager, databaseSchemaName, bdDataProductConfig);
                if (table.getDefinition() == null) {
                    getUseCaseLogger().warn("Missing definition on datastore api table: " + table.getName());
                    continue;
                }
                if (table.getDefinition() instanceof DataStoreApiBlindataDefinition) {
                    table.getDefinition().accept(visitor);
                } else {
                    getUseCaseLogger().warn("Malformed definition on datastore api table: " + table.getName());
                }
            }
        }
    }

}
