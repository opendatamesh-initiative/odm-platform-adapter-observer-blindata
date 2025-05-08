package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_align;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.PolicyEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDGovernancePolicyClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDGovernancePolicyImplementationClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDGovernancePolicySuiteClient;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.*;

@Component
public class PoliciesAlignFactory implements UseCaseFactory {

    @Autowired
    private BDGovernancePolicyClient bdGovernancePolicyClient;
    @Autowired
    private BDGovernancePolicySuiteClient bdGovernancePolicySuiteClient;
    @Autowired
    private BDGovernancePolicyImplementationClient bdGovernancePolicyImplementationClient;

    private final Set<EventType> supportedEventTypes = Set.of(
            POLICY_CREATED,
            POLICY_UPDATED,
            POLICY_DELETED
    );

    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init PoliciesAlign use case, unsupported event type: " + event.getEventType());
        }
        PoliciesAlignOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
        PoliciesAlignBlindataOutboundPort blindataOutboundPort = new PoliciesAlignBlindataOutboundPortImpl(bdGovernancePolicySuiteClient, bdGovernancePolicyClient, bdGovernancePolicyImplementationClient);

        if (POLICY_DELETED.equals(event.getEventType())) {
            return new PoliciesAlignDelete(blindataOutboundPort, odmOutboundPort);
        }
        return new PoliciesAlign(blindataOutboundPort, odmOutboundPort);
    }


    private PoliciesAlignOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        switch (event.getEventType()) {
            case POLICY_CREATED:
            case POLICY_UPDATED:
                if (event.getAfterState() == null) {
                    throw new UseCaseInitException("AfterState is null for event type: " + event.getEventType());
                }
                return createOutboundPort(event.getAfterState(), PolicyEventState.class, state -> new PoliciesAlignOdmOutboundPortImpl(state.getPolicy()));
            case POLICY_DELETED:
                if (event.getBeforeState() == null) {
                    throw new UseCaseInitException("BeforeState is null for event type: " + event.getEventType());
                }
                return createOutboundPort(event.getBeforeState(), PolicyEventState.class, state -> new PoliciesAlignOdmOutboundPortImpl(state.getPolicy()));
            default:
                throw new UseCaseInitException("Unsupported event type: " + event.getEventType());
        }
    }

    private <T> PoliciesAlignOdmOutboundPort createOutboundPort(Object state, Class<T> expectedClass, Function<T, PoliciesAlignOdmOutboundPort> converter) throws UseCaseInitException {
        if (!expectedClass.isInstance(state)) {
            throw new UseCaseInitException("Invalid state type for " + expectedClass.getSimpleName() + " event: " +
                    state.getClass().getTypeName());
        }
        return converter.apply(expectedClass.cast(state));
    }
}
