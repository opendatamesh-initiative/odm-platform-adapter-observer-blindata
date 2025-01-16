package org.opendatamesh.platform.up.metaservice.blindata.validator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseIncorrectInputException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.PolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.PolicyEvaluationResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlindataValidatorService {

    private final DataProductUploadFactory dataProductUploadFactory;
    private final DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory;

    @Autowired
    public BlindataValidatorService(DataProductUploadFactory dataProductUploadFactory, DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory) {
        this.dataProductUploadFactory = dataProductUploadFactory;
        this.dataProductPortsAndAssetsUploadFactory = dataProductPortsAndAssetsUploadFactory;
    }

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public PolicyEvaluationResultRes validateDataProduct(PolicyEvaluationRequestRes evaluationRequest) {
        PolicyEvaluationResultRes evaluationResult = new PolicyEvaluationResultRes();
        evaluationResult.setEvaluationResult(true);
        evaluationResult.setPolicyEvaluationId(evaluationRequest.getPolicyEvaluationId());
        PolicyEvaluationResultRes.OutputObject resultOutput = new PolicyEvaluationResultRes.OutputObject();
        evaluationResult.setOutputObject(resultOutput);

        try {
            PolicyEvaluationInputObject policyEvaluationInputObject = objectMapper.treeToValue(evaluationRequest.getObjectToEvaluate(), PolicyEvaluationInputObject.class);
            DataProductVersionDPDS descriptorToValidate = objectMapper.treeToValue(policyEvaluationInputObject.getAfterState(), DataProductVersionDPDS.class);

            OBEventNotificationResource eventNotification = new OBEventNotificationResource();
            OBEventResource event = new OBEventResource();
            event.setType("DATA_PRODUCT_VERSION_CREATED");
            event.setAfterState(policyEvaluationInputObject.getAfterState());
            eventNotification.setEvent(event);
            dataProductUploadFactory.getUseCaseDryRun(eventNotification).execute();
            if (descriptorToValidate.getInterfaceComponents() != null) {
                dataProductPortsAndAssetsUploadFactory.getUseCaseDryRun(eventNotification).execute();
            }
        } catch (UseCaseIncorrectInputException e) {
            evaluationResult.setEvaluationResult(false);
            resultOutput.setMessage(String.format("Use case failed due to incorrect data on input: %s", e.getMessage()));
            resultOutput.setRawError(objectMapper.valueToTree(e));
            log.warn("[Blindata Policy Validator]: Blindata policy failed to validate data product, reason: {} .", e.getMessage());
            return evaluationResult;
        } catch (UseCaseExecutionException e) {
            resultOutput.setMessage(String.format("Use case failed due an error while processing the input: %s", e.getMessage()));
            resultOutput.setRawError(objectMapper.valueToTree(e));
            log.warn("[Blindata Policy Validator]: Blindata policy failed to validate data product due an error while processing the input: {} .", e.getMessage());
        } catch (UseCaseInitException e) {
            throw new InternalServerException(e);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e);
        }

        return evaluationResult;
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
