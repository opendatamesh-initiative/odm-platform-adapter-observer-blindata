package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseIncorrectInputException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload.PoliciesUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload.StagesUploadFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
public class UseCasesExecutionTemplate implements NotificationEventHandler {

    private final Optional<DataProductUploadFactory> dataProductCreation;
    private final Optional<DataProductPortsAndAssetsUploadFactory> dataProductVersionCreation;
    private final Optional<StagesUploadFactory> stagesUploadFactory;
    private final Optional<PoliciesUploadFactory> policiesCreation;
    private final Optional<DataProductRemovalFactory> dataProductDeletion;

    private final String eventType;
    private final String filter;

    public UseCasesExecutionTemplate(
            DataProductUploadFactory dataProductCreation,
            DataProductPortsAndAssetsUploadFactory dataProductVersionCreation,
            StagesUploadFactory stagesUploadFactory,
            PoliciesUploadFactory policiesCreation,
            DataProductRemovalFactory dataProductDeletion,
            String eventType,
            String filter) {
        this.dataProductCreation = Optional.ofNullable(dataProductCreation);
        this.dataProductVersionCreation = Optional.ofNullable(dataProductVersionCreation);
        this.stagesUploadFactory = Optional.ofNullable(stagesUploadFactory);
        this.policiesCreation = Optional.ofNullable(policiesCreation);
        this.dataProductDeletion = Optional.ofNullable(dataProductDeletion);
        this.eventType = eventType;
        this.filter = filter;
    }

    @Override
    public OBEventNotificationResource handle(OBEventNotificationResource event) {
        try {
            if (dataProductCreation.isPresent()) {
                dataProductCreation.get().getUseCase(event).execute();
            }
            if (dataProductVersionCreation.isPresent()) {
                dataProductVersionCreation.get().getUseCase(event).execute();
            }
            if (stagesUploadFactory.isPresent()) {
                stagesUploadFactory.get().getUseCase(event).execute();
            }
            if (policiesCreation.isPresent()) {
                policiesCreation.get().getUseCase(event).execute();
            }
            if (dataProductDeletion.isPresent()) {
                dataProductDeletion.get().getUseCase(event).execute();
            }
            event.setStatus(OBEventNotificationStatus.PROCESSED);
            return event;
        } catch (UseCaseExecutionException | UseCaseIncorrectInputException e) {
            log.warn(e.getMessage(), e);
            event.setStatus(OBEventNotificationStatus.PROCESS_ERROR);
            event.setProcessingOutput(e.getMessage());
            return event;
        } catch (UseCaseInitException e) {
            log.warn(e.getMessage(), e);
            event.setStatus(OBEventNotificationStatus.UNPROCESSABLE);
            event.setProcessingOutput(e.getMessage());
            return event;
        }
    }

    @Override
    public boolean isSubscribedTo(OBEventNotificationResource event) {
        if (!eventType.equalsIgnoreCase(event.getEvent().getType())) {
            return false;
        }
        if (!StringUtils.hasText(filter)) {
            return true;
        }
        try {
            ExpressionParser parser = new SpelExpressionParser();
            String stringifyEvent = new ObjectMapper().writeValueAsString(event.getEvent());
            Object objectNode = new ObjectMapper().readValue(stringifyEvent, Object.class);
            StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(objectNode);
            return Boolean.TRUE.equals(parser.parseExpression(filter).getValue(standardEvaluationContext, Boolean.class));
        } catch (Exception e) {
            log.error("Failed to check event subscription. {}", e.getMessage(), e);
            return false;
        }
    }
}
