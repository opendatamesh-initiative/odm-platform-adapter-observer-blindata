package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PoliciesUploadFactory implements UseCaseFactory {

    @Autowired
    private PolicyEvaluationResultClient odmPolicyEvaluationResultClient;

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;

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

    private PoliciesUploadOdmOutboundPort initOdmOutboundPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name())) {
            JsonNode afterStateNode = objectMapper.readTree(event.getEvent().getAfterState().toString());
            JsonNode dataProductVersionNode = afterStateNode.get("dataProductVersion");
            DataProductVersionDPDS odmDataProduct = objectMapper.treeToValue(dataProductVersionNode, DataProductVersionDPDS.class);
            return new PoliciesUploadOdmOutboundPortImpl(
                    odmPolicyEvaluationResultClient,
                    odmDataProduct.getInfo());
        }
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_VERSION_CREATED.name())) {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductVersionDPDS.class);
            return new PoliciesUploadOdmOutboundPortImpl(
                    odmPolicyEvaluationResultClient,
                    odmDataProduct.getInfo());
        } else {
            throw new UseCaseInitException("Failed to init OdmOutboundPort on PoliciesUpload use case.");
        }
    }
}
