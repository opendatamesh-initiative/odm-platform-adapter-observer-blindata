package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class DataProductVersionUploadFactory implements UseCaseFactory {

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
            DataProductVersionUploadBlindataOutboundPort bdOutboundPort = new DataProductVersionUploadBlindataOutboundPortImpl(bdDataProductClient);
            DataProductVersionUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);

            return new DataProductVersionUpload(
                    bdOutboundPort,
                    odmOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case.", e);
        }
    }

    private DataProductVersionUploadOdmOutboundPort initOdmOutboundPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name())) {
            ActivityResource activityResource = objectMapper.readValue(event.getEvent().getAfterState().toString(), ActivityResource.class);
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(activityResource.getDataProductVersion(), DataProductVersionDPDS.class);
            return new DataProductVersionUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, odmDataProduct, activityResource);
        } else if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_VERSION_CREATED.name())) {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductVersionDPDS.class);
            return new DataProductVersionUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, odmDataProduct, null);
        } else {
            throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductVersionUpload use case.");
        }
    }
}
