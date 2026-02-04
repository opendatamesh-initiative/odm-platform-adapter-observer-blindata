package org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_version_removal.DataProductVersionRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Optional;


public class UseCasesExecutionTemplateV2 implements NotificationEventHandlerV2 {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Optional<DataProductUploadFactory> dataProductCreation;
    private final Optional<DataProductPortsAndAssetsUploadFactory> dataProductVersionCreation;
    private final Optional<DataProductRemovalFactory> dataProductDeletion;
    private final Optional<DataProductVersionRemovalFactory> dataProductVersionDeletion;

    private final String eventType;
    private final String filter;

    public UseCasesExecutionTemplateV2(
            DataProductUploadFactory dataProductCreation,
            DataProductPortsAndAssetsUploadFactory dataProductVersionCreation,
            DataProductRemovalFactory dataProductDeletion,
            DataProductVersionRemovalFactory dataProductVersionDeletion,

            String eventType,
            String filter) {
        this.dataProductCreation = Optional.ofNullable(dataProductCreation);
        this.dataProductVersionCreation = Optional.ofNullable(dataProductVersionCreation);
        this.dataProductDeletion = Optional.ofNullable(dataProductDeletion);
        this.dataProductVersionDeletion = Optional.ofNullable(dataProductVersionDeletion);
        this.eventType = eventType;
        this.filter = filter;
    }

    @Override
    public EventV2 handle(EventV2 event) {
        try {
            if (dataProductCreation.isPresent()) {
                dataProductCreation.get().getUseCaseV2(event).execute();
            }
            if (dataProductVersionCreation.isPresent()) {
                dataProductVersionCreation.get().getUseCaseV2(event).execute();
            }
            if (dataProductVersionDeletion.isPresent()) {
                dataProductVersionDeletion.get().getUseCaseV2(event).execute();
            }
            if (dataProductDeletion.isPresent()) {
                dataProductDeletion.get().getUseCaseV2(event).execute();
            }
            event.setEventStatus(EventStatusV2.PROCESSED);
            return event;
        } catch (UseCaseExecutionException e) {
            log.warn(e.getMessage(), e);
            event.setEventStatus(EventStatusV2.FAILED_TO_PROCESS);
            event.setErrorMessage(e.getMessage());
            return event;
        } catch (UseCaseInitException e) {
            log.warn(e.getMessage(), e);
            event.setEventStatus(EventStatusV2.FAILED_TO_PROCESS);
            event.setErrorMessage(e.getMessage());
            return event;
        }
    }

    @Override
    public boolean isSubscribedTo(EventV2 event) {
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
