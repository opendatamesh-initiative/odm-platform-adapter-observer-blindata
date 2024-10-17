package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StagesUploadFactory implements UseCaseFactory {

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<String> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_VERSION_CREATED.name(),
            EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name()
    );


    @Override
    public UseCase getUseCase(OBEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            StagesUploadBlindataOutputPort bdOutputPort = new StagesUploadBlindataOutputPortImpl(bdDataProductClient);
            StagesUploadOdmOutputPort odmOutputPort = initOdmOutputPort(event);
            return new StagesUpload(bdOutputPort, odmOutputPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case.", e);
        }
    }

    private StagesUploadOdmOutputPort initOdmOutputPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name())) {
            ActivityResource activityResource = objectMapper.readValue(event.getEvent().getAfterState().toString(), ActivityResource.class);
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(activityResource.getDataProductVersion(), DataProductVersionDPDS.class);
            return new StagesUploadOdmOutputPortImpl(odmDataProduct, activityResource);
        } else if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_VERSION_CREATED.name())) {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductVersionDPDS.class);
            return new StagesUploadOdmOutputPortImpl(odmDataProduct, null);
        } else {
            throw new UseCaseInitException("Failed to init OdmOutputPort on DataProductVersionUpload use case.");
        }
    }
}
