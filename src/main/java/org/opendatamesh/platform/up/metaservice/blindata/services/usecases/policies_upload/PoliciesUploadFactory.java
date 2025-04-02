package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyEvaluationResultClient;
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
public class PoliciesUploadFactory implements UseCaseFactory {

    @Autowired
    private OdmPolicyEvaluationResultClient odmPolicyEvaluationResultClient;

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;

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
            PoliciesUploadBlindataOutboundPort bdOutboundPort = new PoliciesUploadBlindataOutboundPortImpl(
                    bdDataProductClient,
                    bdPolicyEvaluationResultClient
            );
            PoliciesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new PoliciesUpload(
                    bdOutboundPort,
                    odmOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case.", e);
        }
    }

    private PoliciesUploadOdmOutboundPort initOdmOutboundPort(OdmEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        switch (OdmEventType.valueOf(event.getEvent().getType())) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                DataProductActivityEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductActivityEventState.class);
                return new PoliciesUploadOdmOutboundPortImpl(
                        odmPolicyEvaluationResultClient,
                        afterState.getDataProductVersion().getInfo()
                );
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductVersionEventState.class);
                return new PoliciesUploadOdmOutboundPortImpl(
                        odmPolicyEvaluationResultClient,
                        afterState.getDataProductVersion().getInfo());
            }
            default:
                throw new UseCaseInitException("Failed to init OdmOutboundPort on PoliciesUpload use case.");
        }
    }
}
