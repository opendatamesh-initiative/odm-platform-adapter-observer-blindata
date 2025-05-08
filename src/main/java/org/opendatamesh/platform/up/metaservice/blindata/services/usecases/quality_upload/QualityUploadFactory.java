package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BDIssueManagementConfig;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckMapper;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class QualityUploadFactory implements UseCaseFactory, UseCaseDryRunFactory {

    @Autowired
    private BDQualityClient bdQualityClient;
    @Autowired
    private BDIssueCampaignClient bdIssueCampaignClient;
    @Autowired
    private BDUserClient bdUserClient;
    @Autowired
    private BDIssueManagementConfig issuePolicyConfig;
    @Autowired
    private QualityCheckMapper qualityCheckMapper;
    @Autowired
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;

    private final Set<EventType> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_VERSION_CREATED,
            EventType.DATA_PRODUCT_ACTIVITY_COMPLETED
    );


    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init QualityUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            QualityUploadBlindataOutboundPort blindataOutboundPort = new QualityUploadBlindataOutboundPortImpl(bdQualityClient, bdIssueCampaignClient, bdUserClient, issuePolicyConfig, qualityCheckMapper);
            QualityUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new QualityUpload(blindataOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init QualityUpload use case: " + e.getMessage(), e);
        }
    }

    @Override
    public UseCase getUseCaseDryRun(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init QualityUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            QualityUploadBlindataOutboundPort blindataOutboundPort = new QualityUploadBlindataOutboundPortDryRunImpl(new QualityUploadBlindataOutboundPortImpl(bdQualityClient, bdIssueCampaignClient, bdUserClient, issuePolicyConfig, qualityCheckMapper));
            QualityUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new QualityUpload(blindataOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init QualityUpload use case: " + e.getMessage(), e);
        }
    }

    private QualityUploadOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        switch (event.getEventType()) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                ActivityEventState state = castState(event.getAfterState(), ActivityEventState.class, event.getEventType(), "afterState");
                return new QualityUploadOdmOutboundPortImpl(
                        dataProductPortAssetAnalyzer,
                        state.getDataProductVersion()
                );
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState state = castState(event.getAfterState(), DataProductVersionEventState.class, event.getEventType(), "afterState");
                return new QualityUploadOdmOutboundPortImpl(
                        dataProductPortAssetAnalyzer,
                        state.getDataProductVersion());
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on QualityUpload use case.");
        }
    }

    private <T> T castState(Object state, Class<T> expectedClass, EventType eventType, String stateName) throws UseCaseInitException {
        if (state == null) {
            throw new UseCaseInitException("The " + stateName + " is null for event: " + eventType);
        }
        if (!expectedClass.isInstance(state)) {
            throw new UseCaseInitException("The event: " + eventType + " does not have " +
                    expectedClass.getSimpleName() + " as " + stateName + ", but got: " + state.getClass().getTypeName());
        }
        return expectedClass.cast(state);
    }
}
