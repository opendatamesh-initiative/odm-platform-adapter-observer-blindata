package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductVersionEventState;
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
            OdmEventType.DATA_PRODUCT_VERSION_CREATED.name(),
            OdmEventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name()
    );


    @Override
    public UseCase getUseCase(OdmEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            StagesUploadBlindataOutboundPort bdOutboundPort = new StagesUploadBlindataOutboundPortImpl(bdDataProductClient);
            StagesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new StagesUpload(bdOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case.", e);
        }
    }

    private StagesUploadOdmOutboundPort initOdmOutboundPort(OdmEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        switch (OdmEventType.valueOf(event.getEvent().getType())) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                DataProductActivityEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductActivityEventState.class);
                return new StagesUploadOdmOutboundPortImpl(afterState.getDataProductVersion(), afterState.getActivity());
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductVersionEventState.class);
                return new StagesUploadOdmOutboundPortImpl(afterState.getDataProductVersion(), null);
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductVersionUpload use case.");
        }
    }
}
