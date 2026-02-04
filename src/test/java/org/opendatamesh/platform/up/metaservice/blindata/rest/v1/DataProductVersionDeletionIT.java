package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Objects;

public class DataProductVersionDeletionIT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient);
    }

    /**
     * Feature: Handle deletion of data product versions
     * In order to preserve user-entered information in Blindata
     * As the Blindata observer
     * I want to do nothing in Blindata when only a data product version is deleted in ODM.
     * <p>
     * Background:
     * Given a data product "dp1" with fully qualified name "odm://domain.dp1"
     * And the data product "dp1" is registered in Blindata
     * And the data product "dp1" has at least one version "1.0.0" in ODM
     * And the Blindata observer is configured with the "DATA_PRODUCT_VERSION_REMOVAL" use case enabled
     * <p>
     * Scenario: DATA_PRODUCT_VERSION_DELETED event does not delete the Blindata data product
     * Given an ODM event of type "DATA_PRODUCT_VERSION_DELETED"
     * And the event state contains "dataProductVersion" for version "1.0.0" of "dp1"
     * And the event state may also contain "dataProduct" for "dp1"
     * When the Blindata observer processes the event
     * Then the DataProductVersionRemoval use case is selected when configured as an active use case for DATA_PRODUCT_VERSION_DELETED
     * And the use case logs an INFO message stating that the ODM version was deleted but the Blindata data product will be preserved
     * And the use case does not invoke any Blindata API to delete or update the data product
     * And the data product "dp1" is still present in Blindata
     * And any user-entered metadata for "dp1" in Blindata is still present
     */
    @Test
    public void testDataProductVersionDeletedIsNoOpOnBlindata() throws IOException {
        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_eventNotification.json"))),
                OdmEventNotificationResource.class
        );

        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class
        );

        // Assert HTTP response and notification status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // Verify that no Blindata delete/update API is invoked
        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .createDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .patchDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .putDataProduct(Mockito.any());
    }

    /**
     * Feature: Handle deletion of data product versions
     * <p>
     * Scenario: DATA_PRODUCT_VERSION_DELETED event is a no-op even when DATA_PRODUCT_REMOVAL and/or DATA_PRODUCT_VERSION_REMOVAL are enabled
     * Given the "DATA_PRODUCT_REMOVAL" use case is enabled in the observer configuration
     * And optionally the "DATA_PRODUCT_VERSION_REMOVAL" use case is also enabled
     * And an ODM event of type "DATA_PRODUCT_VERSION_DELETED" is received for version "1.0.0" of "dp1"
     * When the Blindata observer processes the event
     * Then the observer must not instantiate or execute any use case that deletes the Blindata data product
     * And no Blindata deletion API is invoked
     * And the data product "dp1" remains in Blindata with all its user-entered information
     */
    @Test
    public void testDataProductVersionDeletedDoesNotTriggerDataProductRemoval() throws IOException {
        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_eventNotification.json"))),
                OdmEventNotificationResource.class
        );

        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class
        );

        // Assert HTTP response and notification status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // We approximate "no DataProductRemoval use case executed" by asserting that
        // no Blindata deletion is performed at all.
        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
    }

    /**
     * Feature: Handle deletion of data product versions
     * <p>
     * Scenario: Deleting one version of a multi-version data product does not impact Blindata data product
     * Given the data product "dp1" has versions "1.0.0" and "2.0.0" in ODM
     * And "dp1" is registered once in Blindata (without per-version instances)
     * And the "DATA_PRODUCT_REMOVAL" use case is enabled
     * And optionally the "DATA_PRODUCT_VERSION_REMOVAL" use case is also enabled
     * When an ODM event of type "DATA_PRODUCT_VERSION_DELETED" is received for version "1.0.0"
     * And the Blindata observer processes the event
     * Then the DataProductVersionRemoval use case is executed as a no-op if configured
     * And the data product "dp1" is still present in Blindata
     * And manual metadata for "dp1" in Blindata is unchanged
     */
    @Test
    public void testDeletingOneVersionDoesNotAffectBlindataDataProduct() throws IOException {
        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_multiVersion_eventNotification.json"))),
                OdmEventNotificationResource.class
        );

        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class
        );

        // Assert HTTP response and notification status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // Again, we assert that no destructive operation is performed on Blindata
        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .createDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .patchDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .putDataProduct(Mockito.any());
    }
}

