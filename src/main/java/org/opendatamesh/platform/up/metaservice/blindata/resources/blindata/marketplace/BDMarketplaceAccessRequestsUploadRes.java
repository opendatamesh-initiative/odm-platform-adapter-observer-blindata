package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource class representing a collection of marketplace access request updates.
 * This class is used for uploading multiple access request updates in a single operation.
 * Each update can contain multiple port status changes for a specific access request.
 *
 * @see AccessRequestUpdate
 * @see AccessRequestPortUpdate
 */
@Schema(
        name = "MarketplaceAccessRequestsUploadRes",
        description = "Resource for uploading Access Requests updates.",
        example = "{\n" +
                "  \"accessRequestsUpdates\": [\n" +
                "    {\n" +
                "      \"accessRequestIdentifier\": \"req-123\",\n" +
                "      \"accessRequestPortsUpdates\": [\n" +
                "        {\n" +
                "          \"portIdentifier\": \"port-456\",\n" +
                "          \"grantStatus\": \"GRANTED\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
)
public class BDMarketplaceAccessRequestsUploadRes {
    @Schema(
            description = "List of updates for the marketplace access requests.",
            required = true
    )
    private List<AccessRequestUpdate> accessRequestsUpdates = new ArrayList<>();

    /**
     * Represents an update for a specific marketplace access request.
     * This class contains the identifier of the access request being updated
     * and a list of port status updates associated with it.
     */
    @Schema(
            name = "AccessRequestUpdate",
            description = "Represents an update for a specific marketplace access request.",
            example = "{\n" +
                    "  \"accessRequestIdentifier\": \"req-123\",\n" +
                    "  \"accessRequestPortsUpdates\": [\n" +
                    "    {\n" +
                    "      \"portIdentifier\": \"port-456\",\n" +
                    "      \"grantStatus\": \"GRANTED\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}"
    )
    public static class AccessRequestUpdate {

        @Schema(
                description = "The unique identifier of the marketplace access request being updated",
                example = "req-123",
                required = true
        )
        private String accessRequestIdentifier;

        @Schema(
                description = "List of port status updates for this access request",
                required = true
        )
        private List<AccessRequestPortUpdate> accessRequestPortsUpdates = new ArrayList<>();

        public AccessRequestUpdate() {
            //DO NOTHING
        }

        public String getAccessRequestIdentifier() {
            return accessRequestIdentifier;
        }

        public void setAccessRequestIdentifier(String accessRequestIdentifier) {
            this.accessRequestIdentifier = accessRequestIdentifier;
        }

        public List<AccessRequestPortUpdate> getAccessRequestPortsUpdates() {
            return accessRequestPortsUpdates;
        }

        public void setAccessRequestPortsUpdates(List<AccessRequestPortUpdate> accessRequestPortsUpdates) {
            this.accessRequestPortsUpdates = accessRequestPortsUpdates;
        }
    }

    /**
     * Represents an update for a specific port within an access request.
     * This class contains the port identifier and its new grant status.
     */
    @Schema(
            name = "AccessRequestPortUpdate",
            description = "Represents an update for a specific port within an access request.",
            example = "{\n" +
                    "  \"portIdentifier\": \"port-456\",\n" +
                    "  \"grantStatus\": \"GRANTED\"\n" +
                    "}"
    )
    public static class AccessRequestPortUpdate {
        @Schema(
                description = "The unique identifier of the port being updated",
                example = "port-456",
                required = true
        )
        private String portIdentifier;

        @Schema(
                description = "The new grant status for the port",
                example = "GRANTED",
                required = true
        )
        private GrantStatusRes grantStatus;

        public AccessRequestPortUpdate() {
        }

        public String getPortIdentifier() {
            return portIdentifier;
        }

        public void setPortIdentifier(String portIdentifier) {
            this.portIdentifier = portIdentifier;
        }

        public GrantStatusRes getGrantStatus() {
            return grantStatus;
        }

        public void setGrantStatus(GrantStatusRes grantStatus) {
            this.grantStatus = grantStatus;
        }

    }

    public BDMarketplaceAccessRequestsUploadRes() {
        //DO NOTHING
    }

    public List<AccessRequestUpdate> getAccessRequestsUpdates() {
        return accessRequestsUpdates;
    }

    public void setAccessRequestsUpdates(List<AccessRequestUpdate> accessRequestsUpdates) {
        this.accessRequestsUpdates = accessRequestsUpdates;
    }

    /**
     * Represents the possible statuses of a marketplace access request grant for each port, on the data ops platform.
     */
    public enum GrantStatusRes {
        /**
         * The request has not been submitted to the platform yet.
         */
        PLATFORM_NOT_SUBMITTED,

        /**
         * The grant request has been submitted to the platform.
         */
        PLATFORM_SUBSCRIBED,

        /**
         * The platform has granted access to the requested port.
         */
        PLATFORM_GRANTED,

        /**
         * The platform has denied access to the requested port.
         */
        PLATFORM_DENIED,

        /**
         * An error occurred while processing the request on the platform.
         */
        PLATFORM_ERROR,

        /**
         * The revoke request has been submitted from the platform.
         */
        PLATFORM_UNSUBSCRIBED,

        /**
         * The previously granted access has been revoked by the platform.
         */
        PLATFORM_REVOKED
    }
}
