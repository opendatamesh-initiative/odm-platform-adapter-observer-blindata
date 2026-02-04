package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_align;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyImplementationRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

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

            BDPolicyImplementationRes extractedPolicyImplementation = extractBdPolicyImplementationFromExternalContext(externalContext);
            if (extractedPolicyImplementationIsNotValid(extractedPolicyImplementation, odmPolicy)) return;

            BDPolicyRes extractedPolicy = extractedPolicyImplementation.getGovernancePolicy();
            if (extractedPolicyIsNotValid(extractedPolicy, odmPolicy)) return;

            BDPolicySuiteRes extractedSuite = extractedPolicy.getGovernancePolicySuite();
            if (extractedPolicySuiteIsNotValid(extractedSuite, odmPolicy)) return;

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
        });
    }

    private boolean extractedPolicyImplementationIsNotValid(BDPolicyImplementationRes extractedPolicyImplementation, OdmPolicyResource odmPolicy) {
        if (extractedPolicyImplementation == null || !StringUtils.hasText(extractedPolicyImplementation.getName())) {
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

    private BDPolicyImplementationRes extractBdPolicyImplementationFromExternalContext(ObjectNode externalContext) {
        try {
            return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .treeToValue(externalContext, BDPolicyImplementationRes.class);
        } catch (JacksonException e) {
            getUseCaseLogger().warn(String.format("%s Error mapping external context to Blindata Policy Implementation: %s", USE_CASE_PREFIX, e.getMessage()), e);
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
