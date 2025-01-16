package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.eventstates.DataProductActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.eventstates.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents.BlindataProperties;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.slf4j.helpers.NOPLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductPortsAndAssetsUploadFactory implements UseCaseFactory, UseCaseDryRunFactory {

    @Autowired
    private BDDataProductClient bdDataProductClient;
    @Autowired
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BlindataProperties blindataProperties;

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
            DataProductPortsAndAssetsUploadBlindataOutboundPort bdOutboundPort = new DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(bdDataProductClient, blindataProperties.getDependsOnSystemNameRegex());
            DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);

            return new DataProductPortsAndAssetsUpload(
                    bdOutboundPort,
                    odmOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case.", e);
        }
    }

    @Override
    public UseCase getUseCaseDryRun(OBEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductPortsAndAssetsUploadBlindataOutboundPort bdOutboundPort = new DataProductPortsAndAssetsUploadBlindataOutboundPortDryRunImpl(new DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(bdDataProductClient, blindataProperties.getDependsOnSystemNameRegex()));
            DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);

            return new DataProductPortsAndAssetsUpload(
                    bdOutboundPort,
                    odmOutboundPort,
                    NOPLogger.NOP_LOGGER
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductVersionUpload use case.", e);
        }
    }

    private DataProductPortsAndAssetsUploadOdmOutboundPort initOdmOutboundPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        switch (EventType.valueOf(event.getEvent().getType())) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                DataProductActivityEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductActivityEventState.class);
                return new DataProductPortsAndAssetsUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, afterState.getDataProductVersion(), afterState.getActivity());
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState afterState = objectMapper.treeToValue(event.getEvent().getAfterState(), DataProductVersionEventState.class);
                return new DataProductPortsAndAssetsUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, afterState.getDataProductVersion(), null);
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductVersionUpload use case.");
        }
    }
}
