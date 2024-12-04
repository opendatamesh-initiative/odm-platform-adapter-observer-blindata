package org.opendatamesh.platform.up.metaservice.blindata.services.schema_analyzers.semanticLinkManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SemanticLinkManagerTest {

    @Mock
    private BDSemanticLinkingClient client;

    @InjectMocks
    private SemanticLinkManager semanticLinkManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnrichPhysicalPropertiesWithSemanticLinks() {
        // Mock input data
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        Set<BDPhysicalFieldRes> physicalFieldResList = new HashSet<>();
        BDPhysicalFieldRes fieldRes = new BDPhysicalFieldRes();
        fieldRes.setName("testField");
        physicalFieldResList.add(fieldRes);
        physicalEntity.setPhysicalFields(physicalFieldResList);

        Map<String, Object> sContext = new HashMap<>();
        sContext.put("s-base", "http://example.com");
        sContext.put("s-type", "[DataCategoryTest]");
        sContext.put("testField", "relation[OtherDataCategory].logicalField");

        SemanticLinkingMetaserviceRes mockResponse = new SemanticLinkingMetaserviceRes();
        LogicalFieldSemanticLinkRes logicalFieldSemanticLinkRes = new LogicalFieldSemanticLinkRes();
        BDLogicalNamespaceRes namespaceRes = new BDLogicalNamespaceRes();
        namespaceRes.setName("logicalName");
        namespaceRes.setDisplayName("Namespace Name");
        BDDataCategoryRes dataCategoryRes = new BDDataCategoryRes();
        dataCategoryRes.setUuid("DCUuid");
        dataCategoryRes.setName("OtherDataCategory");
        BDDataCategoryRes dataCategoryRoot = new BDDataCategoryRes();
        dataCategoryRoot.setUuid("DCUuid2");
        dataCategoryRes.setName("DataCategoryTest");
        logicalFieldSemanticLinkRes.setDataCategory(dataCategoryRes);
        logicalFieldSemanticLinkRes.setNamespace(namespaceRes);

        mockResponse.setSemanticLinkElementResList(logicalFieldSemanticLinkRes);
        mockResponse.setRootDataCategory(dataCategoryRoot);
        doReturn(mockResponse)
                .when(client)
                .getSemanticLinkElements("[DataCategoryTest].relation[OtherDataCategory].logicalField", "http://example.com");

        semanticLinkManager.enrichPhysicalPropertiesWithSemanticLinks(sContext, physicalEntity);

        verify(client, times(1))
                .getSemanticLinkElements("[DataCategoryTest].relation[OtherDataCategory].logicalField", "http://example.com");

        // Assertions
        assertNotNull(fieldRes.getLogicalFields());
        assertEquals(1, fieldRes.getLogicalFields().size());
        assertEquals(1, physicalEntity.getDataCategories().size());
    }
}
