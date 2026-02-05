package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_align;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.PolicyEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicyClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicyImplementationClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicySuiteClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyImplementationRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PoliciesAlignTest {

    @Mock
    private BdGovernancePolicyClient bdPolicyClient;

    @Mock
    private BdGovernancePolicySuiteClient bdSuiteClient;

    @Mock
    private BdGovernancePolicyImplementationClient bdImplementationClient;

    @InjectMocks
    private PoliciesAlignFactory policiesAlignFactory;

    @Test
    void testPolicyCreatedAlign() throws UseCaseExecutionException, UseCaseInitException, IOException {
        // Load ODM policy JSON from classpath
        byte[] jsonBytes = Resources.toByteArray(
                Resources.getResource(PoliciesAlignTest.class, "odm_policy.json")
        );
        OdmPolicyResource odmPolicyResource = new ObjectMapper()
                .readValue(jsonBytes, OdmPolicyResource.class);

        // Prepare event
        PolicyEventState policyEventState = new PolicyEventState();
        policyEventState.setPolicy(odmPolicyResource);

        Event policyEvent = new Event();
        policyEvent.setEventType(EventType.POLICY_CREATED);
        policyEvent.setBeforeState(null);
        policyEvent.setAfterState(policyEventState);

        // Mock clients to return empty pages
        when(bdPolicyClient.getPolicies(any(), any()))
                .thenReturn(Page.<BDPolicyRes>empty());
        when(bdSuiteClient.getPolicySuites(any(), any()))
                .thenReturn(Page.<BDPolicySuiteRes>empty());
        when(bdImplementationClient.getPolicyImplementations(any(), any()))
                .thenReturn(Page.<BDPolicyImplementationRes>empty());

        // Capture create calls
        ArgumentCaptor<BDPolicyRes> policyCaptor = ArgumentCaptor.forClass(BDPolicyRes.class);
        when(bdPolicyClient.createPolicy(policyCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<BDPolicySuiteRes> suiteCaptor = ArgumentCaptor.forClass(BDPolicySuiteRes.class);
        when(bdSuiteClient.createPolicySuite(suiteCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<BDPolicyImplementationRes> implCaptor = ArgumentCaptor.forClass(BDPolicyImplementationRes.class);
        when(bdImplementationClient.createPolicyImplementation(implCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        // Execute use case
        policiesAlignFactory.getUseCase(policyEvent).execute();

        // Ensure no update or delete operations were invoked
        verify(bdPolicyClient, never()).putPolicyRes(any());
        verify(bdPolicyClient, never()).deletePolicy(any());
        verify(bdSuiteClient, never()).putPolicySuite(any());
        verify(bdSuiteClient, never()).deletePolicySuite(any());
        verify(bdImplementationClient, never()).putPolicyImplementation(any());
        verify(bdImplementationClient, never()).deletePolicyImplementation(any());


        // Load expected BD resources from classpath
        byte[] policyJson = Resources.toByteArray(
                Resources.getResource(PoliciesAlignTest.class, "bd_policy.json")
        );
        BDPolicyRes expectedPolicy = new ObjectMapper()
                .readValue(policyJson, BDPolicyRes.class);

        byte[] suiteJson = Resources.toByteArray(
                Resources.getResource(PoliciesAlignTest.class, "bd_policy_suite.json")
        );
        BDPolicySuiteRes expectedSuite = new ObjectMapper()
                .readValue(suiteJson, BDPolicySuiteRes.class);

        byte[] implJson = Resources.toByteArray(
                Resources.getResource(PoliciesAlignTest.class, "bd_policy_implementation.json")
        );
        BDPolicyImplementationRes expectedImpl = new ObjectMapper()
                .readValue(implJson, BDPolicyImplementationRes.class);

        // Assert that captured arguments match expected resources, ignoring collection order
        assertThat(policyCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedPolicy);

        assertThat(suiteCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedSuite);

        assertThat(implCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedImpl);
    }

    @Test
    void testPolicyUpdatedAlign() throws UseCaseExecutionException, UseCaseInitException, IOException {
        OdmPolicyResource afterOdm = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "odm_policy.json")),
                OdmPolicyResource.class
        );

        // Prepare event with before and after state
        PolicyEventState afterState = new PolicyEventState();
        afterState.setPolicy(afterOdm);

        Event updateEvent = new Event();
        updateEvent.setEventType(EventType.POLICY_UPDATED);
        updateEvent.setAfterState(afterState);

        // Mock clients to return existing BD resources
        BDPolicyRes existingPolicy = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy.json")),
                BDPolicyRes.class
        );
        when(bdPolicyClient.getPolicies(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingPolicy)));

        BDPolicySuiteRes existingSuite = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_suite.json")),
                BDPolicySuiteRes.class
        );
        when(bdSuiteClient.getPolicySuites(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingSuite)));

        BDPolicyImplementationRes existingImpl = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_implementation.json")),
                BDPolicyImplementationRes.class
        );
        when(bdImplementationClient.getPolicyImplementations(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingImpl)));

        // Capture update calls
        ArgumentCaptor<BDPolicyRes> policyPutCaptor = ArgumentCaptor.forClass(BDPolicyRes.class);
        when(bdPolicyClient.putPolicyRes(policyPutCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<BDPolicySuiteRes> suitePutCaptor = ArgumentCaptor.forClass(BDPolicySuiteRes.class);
        when(bdSuiteClient.putPolicySuite(suitePutCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<BDPolicyImplementationRes> implPutCaptor = ArgumentCaptor.forClass(BDPolicyImplementationRes.class);
        when(bdImplementationClient.putPolicyImplementation(implPutCaptor.capture()))
                .thenAnswer(i -> i.getArgument(0));

        // Execute use case
        policiesAlignFactory.getUseCase(updateEvent).execute();

        // Verify create/delete not invoked
        verify(bdPolicyClient, never()).createPolicy(any());
        verify(bdPolicyClient, never()).deletePolicy(any());
        verify(bdSuiteClient, never()).createPolicySuite(any());
        verify(bdSuiteClient, never()).deletePolicySuite(any());
        verify(bdImplementationClient, never()).createPolicyImplementation(any());
        verify(bdImplementationClient, never()).deletePolicyImplementation(any());

        // Load expected updated BD resources
        BDPolicyRes expectedPolicy = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy.json")),
                BDPolicyRes.class
        );
        BDPolicySuiteRes expectedSuite = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_suite.json")),
                BDPolicySuiteRes.class
        );
        BDPolicyImplementationRes expectedImpl = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_implementation.json")),
                BDPolicyImplementationRes.class
        );

        // Assert that PUT payloads match expected, ignoring collection order
        assertThat(policyPutCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedPolicy);
        assertThat(suitePutCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedSuite);
        assertThat(implPutCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedImpl);
    }


    @Test
    void testPolicyDeletedAlign() throws UseCaseExecutionException, UseCaseInitException, IOException {
        // Load ODM policy JSON to delete
        OdmPolicyResource odmPolicy = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "odm_policy.json")),
                OdmPolicyResource.class
        );

        // Prepare event for deletion
        PolicyEventState beforeState = new PolicyEventState();
        beforeState.setPolicy(odmPolicy);
        Event deleteEvent = new Event();
        deleteEvent.setEventType(EventType.POLICY_DELETED);
        deleteEvent.setBeforeState(beforeState);
        deleteEvent.setAfterState(null);

        // Mock clients to return existing BD resources matching the policy
        BDPolicyRes existingPolicy = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy.json")),
                BDPolicyRes.class
        );
        existingPolicy.setUuid("ExistingPolicyUuid");
        when(bdPolicyClient.getPolicies(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingPolicy)));

        BDPolicySuiteRes existingSuite = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_suite.json")),
                BDPolicySuiteRes.class
        );
        existingSuite.setUuid("ExistingSuiteUuid");
        when(bdSuiteClient.getPolicySuites(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingSuite)));

        BDPolicyImplementationRes existingImpl = new ObjectMapper().readValue(
                Resources.toByteArray(Resources.getResource(PoliciesAlignTest.class, "bd_policy_implementation.json")),
                BDPolicyImplementationRes.class
        );
        existingImpl.setUuid("ExistingImplUuid");
        when(bdImplementationClient.getPolicyImplementations(any(), any()))
                .thenReturn(new PageImpl<>(List.of(existingImpl)));

        // Capture delete calls
        ArgumentCaptor<String> implDeleteCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(bdImplementationClient).deletePolicyImplementation(implDeleteCaptor.capture());

        // Execute use case
        policiesAlignFactory.getUseCase(deleteEvent).execute();

        // Verify delete called and other operations not invoked
        verify(bdPolicyClient, never()).deletePolicy(any());
        verify(bdSuiteClient, never()).deletePolicySuite(any());
        verify(bdImplementationClient, times(1)).deletePolicyImplementation(any());
        verify(bdPolicyClient, never()).createPolicy(any());
        verify(bdPolicyClient, never()).putPolicyRes(any());
        verify(bdSuiteClient, never()).createPolicySuite(any());
        verify(bdSuiteClient, never()).putPolicySuite(any());
        verify(bdImplementationClient, never()).createPolicyImplementation(any());
        verify(bdImplementationClient, never()).putPolicyImplementation(any());

        // Assert that the correct IDs were deleted
        assertThat(implDeleteCaptor.getValue())
                .isEqualTo(existingImpl.getUuid());
    }
}
