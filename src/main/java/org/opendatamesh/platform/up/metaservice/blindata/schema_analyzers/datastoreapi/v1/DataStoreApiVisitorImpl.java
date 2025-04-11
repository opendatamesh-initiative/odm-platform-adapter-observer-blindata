package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import org.opendatamesh.dpds.datastoreapi.v1.extensions.DataStoreApiStandardDefinitionVisitor;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiDatabaseService;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiInfo;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiSchema;
import org.opendatamesh.dpds.datastoreapi.v1.model.DataStoreApiStandardDefinitionObject;
import org.opendatamesh.dpds.datastoreapi.v1.visitor.DataStoreApiVisitor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import java.util.ArrayList;
import java.util.List;

class DataStoreApiVisitorImpl implements DataStoreApiVisitor {

    private final SemanticLinkManager semanticLinkManager;
    private final DataStoreApiVisitorEntitiesPresenter entitiesPresenter;
    private final DataStoreApiVisitorQualityDefinitionsPresenter qualityPresenter;

    DataStoreApiVisitorImpl(SemanticLinkManager semanticLinkManager, DataStoreApiVisitorEntitiesPresenter entitiesPresenter) {
        this.semanticLinkManager = semanticLinkManager;
        this.entitiesPresenter = entitiesPresenter;
        this.qualityPresenter = null;
    }

    DataStoreApiVisitorImpl(SemanticLinkManager semanticLinkManager, DataStoreApiVisitorQualityDefinitionsPresenter qualityPresenter) {
        this.semanticLinkManager = semanticLinkManager;
        this.entitiesPresenter = null;
        this.qualityPresenter = qualityPresenter;
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
        List<BDPhysicalEntityRes> physicalEntities = new ArrayList<>();
        List<QualityCheck> qualityChecks = new ArrayList<>();
        String databaseSchemaName = dataStoreApiSchema.getDatabaseSchemaName();

        if (dataStoreApiSchema.getTables() != null) {
            for (DataStoreApiStandardDefinitionObject table : dataStoreApiSchema.getTables()) {
                DataStoreApiStandardDefinitionVisitor<DataStoreApiBlindataDefinition> visitor =
                        new DataStoreApiStandardDefinitionVisitorImpl(physicalEntities::addAll, qualityChecks::addAll, semanticLinkManager, databaseSchemaName);
                visitor.visit(table.getDefinition());
            }
        }
        presentResults(physicalEntities, qualityChecks);
    }

    private void presentResults(List<BDPhysicalEntityRes> physicalEntities, List<QualityCheck> qualityChecks) {
        if (entitiesPresenter != null) {
            entitiesPresenter.presentPhysicalEntities(physicalEntities);
        }
        if (qualityPresenter != null) {
            qualityPresenter.presentQualityChecks(qualityChecks);
        }
    }
}
