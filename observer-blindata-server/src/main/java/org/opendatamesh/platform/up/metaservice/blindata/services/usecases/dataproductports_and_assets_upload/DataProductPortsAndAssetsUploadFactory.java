package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductPortsAndAssetsUploadFactory implements UseCaseFactory {

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<String> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_VERSION_CREATED.name(),
            EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name()
    );

    @Override
    public UseCase getUseCase(OBEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductPortsAndAssetsUploadBlindataOutboundPort bdOutboundPort = new DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(bdDataProductClient);
            DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);

            return new DataProductPortsAndAssetsUpload(
                    bdOutboundPort,
                    odmOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case.", e);
        }
    }

    private DataProductPortsAndAssetsUploadOdmOutboundPort initOdmOutboundPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name())) {
            JsonNode afterStateNode = objectMapper.readTree(event.getEvent().getAfterState().toString());
            JsonNode activityNode = afterStateNode.get("activity");
            JsonNode dataProductVersionNode = afterStateNode.get("dataProductVersion");
            ActivityResource activityResource = objectMapper.treeToValue(activityNode, ActivityResource.class);
            DataProductVersionDPDS odmDataProduct = objectMapper.treeToValue(dataProductVersionNode, DataProductVersionDPDS.class);
            return new DataProductPortsAndAssetsUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, odmDataProduct, activityResource);
        } else if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_VERSION_CREATED.name())) {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductVersionDPDS.class);
            return new DataProductPortsAndAssetsUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, odmDataProduct, null);
        } else {
            throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductVersionUpload use case.");
        }
    }
}
