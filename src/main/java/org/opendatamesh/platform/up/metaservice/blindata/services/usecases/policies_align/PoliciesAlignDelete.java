package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_align;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class PoliciesAlignDelete implements UseCase {

    private final String USE_CASE_PREFIX = "[PoliciesAlign-Delete]";

    private final PoliciesAlignBlindataOutboundPort blindataOutboundPort;
    private final PoliciesAlignOdmOutboundPort odmOutboundPort;

    PoliciesAlignDelete(PoliciesAlignBlindataOutboundPort blindataOutboundPort, PoliciesAlignOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            OdmPolicyResource odmPolicy = odmOutboundPort.getPolicy();
            ObjectNode externalContext = odmPolicy.getExternalContext();

            if (externalContextIsNotValid(externalContext, odmPolicy)) return;

            BDPolicyRes extractedPolicy = extractBdPolicyFromExternalContext(externalContext);
            if (extractedPolicyIsNotValid(extractedPolicy, odmPolicy)) return;

            BDPolicySuiteRes extractedSuite = extractedPolicy.getGovernancePolicySuite();
            if (extractedPolicySuiteIsNotValid(extractedSuite, odmPolicy)) return;

            BDPolicyImplementationRes extractedPolicyImplementation = buildPolicyImplementationFromOdmPolicy(odmPolicy);
            if (extractedPolicyImplementationIsNotValid(extractedPolicyImplementation, odmPolicy)) return;

            // Handle Policy Suite
            Optional<BDPolicySuiteRes> suite = blindataOutboundPort
                    .findPolicySuite(extractedSuite.getName());
            if (suite.isEmpty()) {
                getUseCaseLogger().info(String.format("%s Policy Suite: %s not found on Blindata", USE_CASE_PREFIX, extractedSuite.getName()));
                return;
            }

            // Handle Policy
            Optional<BDPolicyRes> policy = blindataOutboundPort.findPolicy(suite.get().getUuid(), extractedPolicy.getName());
            if (policy.isEmpty()) {
                getUseCaseLogger().info(String.format("%s Policy: %s not found on Blindata", USE_CASE_PREFIX, extractedPolicy.getName()));
                return;
            }

            // Handle Policy Implementation
            Optional<BDPolicyImplementationRes> implementation = blindataOutboundPort.findPolicyImplementation(extractedPolicyImplementation.getName());
            if (implementation.isEmpty()) {
                getUseCaseLogger().info(String.format("%s Policy Implementation: %s not found on Blindata", USE_CASE_PREFIX, extractedPolicyImplementation.getName()));
            } else {
                blindataOutboundPort.deletePolicyImplementation(implementation.get().getUuid());
                getUseCaseLogger().info(String.format("%s Deleted Policy Implementation: %s on Blindata", USE_CASE_PREFIX, implementation.get().getName()));
            }
            blindataOutboundPort.deletePolicy(policy.get().getUuid());
            getUseCaseLogger().info(String.format("%s Deleted Policy: %s on Blindata", USE_CASE_PREFIX, policy.get().getName()));

        });
    }

    private boolean extractedPolicyImplementationIsNotValid(BDPolicyImplementationRes extractedPolicyImplementation, OdmPolicyResource odmPolicy) {
        if (!StringUtils.hasText(extractedPolicyImplementation.getName())) {
            getUseCaseLogger().warn(String.format("%s Invalid Policy Implementation built from Odm Policy: %s", USE_CASE_PREFIX, odmPolicy.getName()));
            return true;
        }
        return false;
    }

    private boolean extractedPolicySuiteIsNotValid(BDPolicySuiteRes extractedSuite, OdmPolicyResource odmPolicy) {
        if (extractedSuite == null || !StringUtils.hasText(extractedSuite.getName())) {
            getUseCaseLogger().warn(String.format("%s External context policy is missing suite for Odm Policy: %s", USE_CASE_PREFIX, odmPolicy.getName()));
            return true;
        }
        return false;
    }

    private boolean extractedPolicyIsNotValid(BDPolicyRes extractedPolicy, OdmPolicyResource odmPolicy) {
        if (extractedPolicy == null || !StringUtils.hasText(extractedPolicy.getName())) {
            getUseCaseLogger().warn(String.format("%s External context policy is not valid for Odm Policy: %s", USE_CASE_PREFIX, odmPolicy.getName()));
            return true;
        }
        return false;
    }

    private BDPolicyRes extractBdPolicyFromExternalContext(ObjectNode externalContext) {
        try {
            return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .treeToValue(externalContext, BDPolicyRes.class);
        } catch (JacksonException e) {
            getUseCaseLogger().warn(String.format("%s Error mapping external context to Blindata Policy: %s", USE_CASE_PREFIX, e.getMessage()), e);
            return null;
        }
    }

    private boolean externalContextIsNotValid(ObjectNode externalContext, OdmPolicyResource odmPolicy) {
        if (externalContext == null || externalContext.isEmpty()) {
            getUseCaseLogger().warn(String.format("%s Empty external context for Odm Policy: %s", USE_CASE_PREFIX, odmPolicy.getName()));
            return true;
        }
        return false;
    }

    private BDPolicyImplementationRes buildPolicyImplementationFromOdmPolicy(OdmPolicyResource odmPolicy) {

        BDPolicyImplementationRes policyImplementation = new BDPolicyImplementationRes();
        policyImplementation.setName(odmPolicy.getName());
        policyImplementation.setDisplayName(odmPolicy.getDisplayName());
        policyImplementation.setDescription(odmPolicy.getDescription());
        policyImplementation.setBlocking(odmPolicy.getBlockingFlag());
        policyImplementation.setPolicyBody(odmPolicy.getRawContent());
        odmPolicy.getEvaluationEvents().forEach(odmEvalEvent ->
                policyImplementation.getEvaluationEvents().add(
                        new BDPolicyImplementationEvaluationEventRes(BDPolicyEvaluationEvent.valueOf(odmEvalEvent.getEvent()))
                )
        );
        policyImplementation.setEvaluationCondition(odmPolicy.getFilteringExpression());
        if (odmPolicy.getPolicyEngine() != null) {
            policyImplementation.setPolicyEngineName(odmPolicy.getPolicyEngine().getName());
        }
        return policyImplementation;
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }

}
