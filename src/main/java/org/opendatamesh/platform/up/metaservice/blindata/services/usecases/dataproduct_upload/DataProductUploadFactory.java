package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductUploadFactory implements UseCaseFactory, UseCaseDryRunFactory {

    @Autowired
    private BDUserClient bdUserClient;

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private BDStewardshipClient bdStewardshipClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${blindata.roleUuid}")
    private String roleUuid;

    private final Set<String> supportedEventTypes = Set.of(
            OdmEventType.DATA_PRODUCT_CREATED.name(),
            OdmEventType.DATA_PRODUCT_VERSION_CREATED.name(),
            OdmEventType.DATA_PRODUCT_ACTIVITY_COMPLETED.name()
    );


    @Override
    public UseCase getUseCase(OdmEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductUploadBlindataOutboundPort blindataOutboundPort = new DataProductUploadBlindataOutboundPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    roleUuid
            );
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new DataProductUpload(
                    odmOutboundPort,
                    blindataOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }

    private DataProductUploadOdmOutboundPort initOdmOutboundPort(OdmEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        switch (OdmEventType.valueOf(event.getEvent().getType())) {
            case DATA_PRODUCT_CREATED: {
                DataProductEventState dataProductEventState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductEventState.class);
                return new DataProductUploadOdmOutboundPortImpl(dataProductEventState.getDataProduct());
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState dataProductVersionEventState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductVersionEventState.class);
                return new DataProductUploadOdmOutboundPortImpl(dataProductVersionEventState.getDataProductVersion().getInfo());
            }
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                DataProductActivityEventState activityEventState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductActivityEventState.class);
                return new DataProductUploadOdmOutboundPortImpl(activityEventState.getDataProductVersion().getInfo());
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductUpload use case.");
        }
    }

    @Override
    public UseCase getUseCaseDryRun(OdmEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductUploadBlindataOutboundPort blindataOutboundPort = new DataProductUploadBlindataOutboundPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    roleUuid
            );
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new DataProductUpload(
                    odmOutboundPort,
                    new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }
}
