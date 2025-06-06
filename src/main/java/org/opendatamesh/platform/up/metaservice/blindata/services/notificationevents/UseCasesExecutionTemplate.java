package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.marketplace_portupdater.MarketplaceAccessRequestsPortUpdateFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_align.PoliciesAlignFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload.PoliciesUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload.QualityUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload.StagesUploadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Optional;


public class UseCasesExecutionTemplate implements NotificationEventHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Optional<DataProductUploadFactory> dataProductCreation;
    private final Optional<DataProductPortsAndAssetsUploadFactory> dataProductVersionCreation;
    private final Optional<QualityUploadFactory> qualityUpload;
    private final Optional<StagesUploadFactory> stagesUploadFactory;
    private final Optional<PoliciesAlignFactory> policiesAlignFactory;
    private final Optional<PoliciesUploadFactory> policiesUploadFactory;
    private final Optional<DataProductRemovalFactory> dataProductDeletion;
    private final Optional<MarketplaceAccessRequestsPortUpdateFactory> marketplaceAccessRequestResultUploadFactory;

    private final String eventType;
    private final String filter;

    public UseCasesExecutionTemplate(
            DataProductUploadFactory dataProductCreation,
            DataProductPortsAndAssetsUploadFactory dataProductVersionCreation,
            QualityUploadFactory qualityUpload,
            StagesUploadFactory stagesUploadFactory,
            PoliciesAlignFactory policiesAlignFactory,
            PoliciesUploadFactory policiesUploadFactory,
            DataProductRemovalFactory dataProductDeletion,
            MarketplaceAccessRequestsPortUpdateFactory marketplaceAccessRequestResultUploadFactory,

            String eventType,
            String filter) {
        this.dataProductCreation = Optional.ofNullable(dataProductCreation);
        this.dataProductVersionCreation = Optional.ofNullable(dataProductVersionCreation);
        this.qualityUpload = Optional.ofNullable(qualityUpload);
        this.stagesUploadFactory = Optional.ofNullable(stagesUploadFactory);
        this.policiesAlignFactory = Optional.ofNullable(policiesAlignFactory);
        this.policiesUploadFactory = Optional.ofNullable(policiesUploadFactory);
        this.dataProductDeletion = Optional.ofNullable(dataProductDeletion);
        this.marketplaceAccessRequestResultUploadFactory = Optional.ofNullable(marketplaceAccessRequestResultUploadFactory);
        this.eventType = eventType;
        this.filter = filter;
    }

    @Override
    public Event handle(Event event) {
        try {
            if (dataProductCreation.isPresent()) {
                dataProductCreation.get().getUseCase(event).execute();
            }
            if (dataProductVersionCreation.isPresent()) {
                dataProductVersionCreation.get().getUseCase(event).execute();
            }
            if (qualityUpload.isPresent()) {
                qualityUpload.get().getUseCase(event).execute();
            }
            if (stagesUploadFactory.isPresent()) {
                stagesUploadFactory.get().getUseCase(event).execute();
            }
            if (policiesAlignFactory.isPresent()) {
                policiesAlignFactory.get().getUseCase(event).execute();
            }
            if (policiesUploadFactory.isPresent()) {
                policiesUploadFactory.get().getUseCase(event).execute();
            }
            if (dataProductDeletion.isPresent()) {
                dataProductDeletion.get().getUseCase(event).execute();
            }
            if (marketplaceAccessRequestResultUploadFactory.isPresent()) {
                marketplaceAccessRequestResultUploadFactory.get().getUseCase(event).execute();
            }
            event.setEventStatus(EventStatus.PROCESSED);
            return event;
        } catch (UseCaseExecutionException e) {
            log.warn(e.getMessage(), e);
            event.setEventStatus(EventStatus.PROCESS_ERROR);
            event.setProcessingOutput(new ObjectMapper().valueToTree(e));
            return event;
        } catch (UseCaseInitException e) {
            log.warn(e.getMessage(), e);
            event.setEventStatus(EventStatus.UNPROCESSABLE);
            event.setProcessingOutput(new ObjectMapper().valueToTree(e));
            return event;
        }
    }

    @Override
    public boolean isSubscribedTo(Event event) {
        if (!eventType.equalsIgnoreCase(event.getEventType().toString())) {
            return false;
        }
        if (!StringUtils.hasText(filter)) {
            return true;
        }
        try {
            ExpressionParser parser = new SpelExpressionParser();
            String stringifyEvent = new ObjectMapper().writeValueAsString(event);
            Object objectNode = new ObjectMapper().readValue(stringifyEvent, Object.class);
            StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(objectNode);
            return Boolean.TRUE.equals(parser.parseExpression(filter).getValue(standardEvaluationContext, Boolean.class));
        } catch (Exception e) {
            log.error("Failed to check event subscription. {}", e.getMessage(), e);
            return false;
        }
    }
}
