package org.opendatamesh.platform.up.metaservice.blindata.validator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventAdapter;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions.OdmPlatformBadRequestException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions.OdmPlatformInternalServerException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.RunWithUseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.ValidatorUseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationResultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.DATA_PRODUCT_VERSION_CREATED;

@Service
public class BlindataValidatorService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DataProductUploadFactory dataProductUploadFactory;
    private final DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory;
    private final EventAdapter eventAdapter;

    @Autowired
    public BlindataValidatorService(
            DataProductUploadFactory dataProductUploadFactory,
            DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory,
            EventAdapter eventAdapter
    ) {
        this.dataProductUploadFactory = dataProductUploadFactory;
        this.dataProductPortsAndAssetsUploadFactory = dataProductPortsAndAssetsUploadFactory;
        this.eventAdapter = eventAdapter;
    }

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public OdmValidatorPolicyEvaluationResultRes validateDataProduct(OdmValidatorPolicyEvaluationRequestRes evaluationRequest) {

        OdmValidatorPolicyEvaluationResultRes evaluationResult = new OdmValidatorPolicyEvaluationResultRes();
        evaluationResult.setEvaluationResult(true);
        evaluationResult.setPolicyEvaluationId(evaluationRequest.getPolicyEvaluationId());
        OdmValidatorPolicyEvaluationResultRes.OutputObject resultOutput = new OdmValidatorPolicyEvaluationResultRes.OutputObject();
        evaluationResult.setOutputObject(resultOutput);

        ValidatorUseCaseLogger validatorUseCaseLogger = new ValidatorUseCaseLogger();
        new RunWithUseCaseLogger(validatorUseCaseLogger, () -> {
            try {
                PolicyEvaluationInputObject policyEvaluationInputObject = objectMapper.treeToValue(evaluationRequest.getObjectToEvaluate(), PolicyEvaluationInputObject.class);
                OdmEventNotificationResource odmEventNotification = buildFakeNotificationEvent(policyEvaluationInputObject);

                Event eventNotification = eventAdapter.odmToInternalEvent(odmEventNotification)
                        .orElseThrow(() -> new IllegalStateException("Impossible to convert Odm Event to Internal Event."));

                dataProductUploadFactory.getUseCaseDryRun(eventNotification).execute();
                if (policyEvaluationInputObject.getAfterState().has("interfaceComponents")) {
                    dataProductPortsAndAssetsUploadFactory.getUseCaseDryRun(eventNotification).execute();
                }
            } catch (UseCaseExecutionException e) {
                resultOutput.setMessage(String.format("Use case failed due an internal use case error: %s", e.getMessage()));
                resultOutput.setRawError(objectMapper.valueToTree(e));
                log.warn("[Blindata Policy Validator]: Blindata policy failed to validate data product due an internal use case error: {} .", e.getMessage());
            } catch (UseCaseInitException e) {
                throw new OdmPlatformInternalServerException(e);
            } catch (JsonProcessingException e) {
                throw new OdmPlatformBadRequestException(e);
            }
        }).run();

        if (!validatorUseCaseLogger.getWarnings().isEmpty()) {
            evaluationResult.setEvaluationResult(false);
            resultOutput.setMessage("[Blindata Policy Validator]: Blindata policy failed to validate data product.");
            resultOutput.setRawError(objectMapper.valueToTree(validatorUseCaseLogger.getWarnings()));
            try {
                log.info("[Blindata Policy Validator]: Blindata policy failed to validate data product: {}", objectMapper.writeValueAsString(resultOutput.getRawError()));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }

        return evaluationResult;
    }

    private OdmEventNotificationResource buildFakeNotificationEvent(PolicyEvaluationInputObject policyEvaluationInputObject) {
        OdmEventNotificationResource eventNotification = new OdmEventNotificationResource();
        OdmEventResource event = new OdmEventResource();
        event.setType(DATA_PRODUCT_VERSION_CREATED.name());
        event.setAfterState(policyEvaluationInputObject.getAfterState());
        eventNotification.setEvent(event);
        return eventNotification;
    }

    private static class PolicyEvaluationInputObject {
        private JsonNode currentState;
        private JsonNode afterState;

        public JsonNode getCurrentState() {
            return currentState;
        }

        public void setCurrentState(JsonNode currentState) {
            this.currentState = currentState;
        }

        public JsonNode getAfterState() {
            return afterState;
        }

        public void setAfterState(JsonNode afterState) {
            this.afterState = afterState;
        }
    }

}
