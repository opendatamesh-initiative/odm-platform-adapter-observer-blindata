package org.opendatamesh.platform.up.metaservice.blindata.services.v2.devopsevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents.ActivityEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Turns a DevOps 2.0 activity event ({@code ACTIVITY_SUCCEEDED}, {@code ACTIVITY_FAILED}) into the inputs the
 * use-case factories need: the activity and the data product version descriptor.
 * <p>
 * <b>TEMPORARY BRIDGE (BDMD-5558):</b> DevOps 2.0 events carry only the identity of the data product version, not its
 * descriptor, so the descriptor is read from Registry V2 by {@code dataProductVersionUuid}. When the events are
 * enriched upstream (likely by Notification), {@link #dataProductVersionOf(EventV2)} reads the descriptor from the
 * event and the Registry read is deleted, together with {@link OdmRegistryClient#getDataProductVersionV2(String)}.
 * <p>
 * Factories and use cases must not depend on the Registry client: this class is the only place that reads Registry V2
 * for DevOps events.
 */
@Component
public class DevOpsActivityEventResolver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OdmRegistryClient odmRegistryClient;
    private final ObjectMapper objectMapper;

    // EventV2 has no equals/hashCode, so keys are compared by identity: one entry per event instance,
    // released when the event is garbage collected.
    private final Map<EventV2, DataProductVersion> resolvedVersions = Collections.synchronizedMap(new WeakHashMap<>());

    public DevOpsActivityEventResolver(OdmRegistryClient odmRegistryClient, ObjectMapper objectMapper) {
        this.odmRegistryClient = odmRegistryClient;
        this.objectMapper = objectMapper;
    }

    public OdmActivityResourceV2 activityOf(EventV2 event) {
        requireContent(event);
        try {
            ActivityEventContentResource content = objectMapper.treeToValue(event.getEventContent(), ActivityEventContentResource.class);
            return requireActivity(content, event);
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse " + event.getEventType() + " event content.", e);
        }
    }

    public DataProductVersion dataProductVersionOf(EventV2 event) {
        DataProductVersion cached = resolvedVersions.get(event);
        if (cached != null) {
            return cached;
        }
        OdmActivityResourceV2 activity = activityOf(event);
        log.info("[DevOpsActivityEventResolver] Reading data product version {} ({} {}) from ODM Registry V2 for {} of activity {}",
                activity.getDataProductVersionUuid(), activity.getDataProductFqn(), activity.getDataProductVersionNumber(),
                event.getEventType(), activity.getName());
        OdmDataProductVersionResourceV2 version = readFromRegistry(activity);
        DataProductVersion dataProductVersion = parseDescriptor(version, activity);
        resolvedVersions.put(event, dataProductVersion);
        return dataProductVersion;
    }

    private void requireContent(EventV2 event) {
        if (event.getEventContent() == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
    }

    private OdmActivityResourceV2 requireActivity(ActivityEventContentResource content, EventV2 event) {
        OdmActivityResourceV2 activity = content == null ? null : content.getActivity();
        if (activity == null || !StringUtils.hasText(activity.getDataProductVersionUuid())) {
            throw new UseCaseInitException("Missing activity.dataProductVersionUuid in " + event.getEventType() + " event " + event.getResourceIdentifier());
        }
        return activity;
    }

    private OdmDataProductVersionResourceV2 readFromRegistry(OdmActivityResourceV2 activity) {
        String uuid = activity.getDataProductVersionUuid();
        Optional<OdmDataProductVersionResourceV2> version;
        try {
            version = odmRegistryClient.getDataProductVersionV2(uuid);
        } catch (ClientException e) {
            throw registryCallFailed(uuid, e);
        } catch (RuntimeException e) {
            throw new UseCaseInitException("Failed to read data product version " + uuid + " from ODM Registry V2.", e);
        }
        return version.orElseThrow(() -> new UseCaseInitException(
                "Data product version " + uuid + " (" + activity.getDataProductFqn() + " " + activity.getDataProductVersionNumber() + ") not found in ODM Registry V2."));
    }

    /**
     * The Registry response body is kept out of the message, because the message is written back to Notification
     * as the notification's {@code errorMessage}. Only the HTTP status is reported.
     */
    private UseCaseInitException registryCallFailed(String uuid, ClientException e) {
        log.warn("[DevOpsActivityEventResolver] ODM Registry V2 answered HTTP {} for data product version {}.", e.getCode(), uuid);
        return new UseCaseInitException(
                "Failed to read data product version " + uuid + " from ODM Registry V2: HTTP " + e.getCode() + ".");
    }

    private DataProductVersion parseDescriptor(OdmDataProductVersionResourceV2 version, OdmActivityResourceV2 activity) {
        String uuid = activity.getDataProductVersionUuid();
        if (version.getContent() == null) {
            throw new UseCaseInitException("ODM Registry V2 returned no content for data product version " + uuid);
        }
        try {
            return objectMapper.readValue(version.getContent().toString(), DataProductVersion.class);
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse descriptor of data product version " + uuid + ".", e);
        }
    }
}
