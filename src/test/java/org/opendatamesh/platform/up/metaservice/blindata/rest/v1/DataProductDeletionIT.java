package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class DataProductDeletionIT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient);
    }

    /**
     * Feature: Handle deletion of data products
     * In order to preserve user-entered information in Blindata
     * As the Blindata observer
     * I want to delete the Blindata data product only when the whole data product is deleted in ODM.
     * <p>
     * Background:
     *   Given a data product "dp1" with fully qualified name "odm://domain.dp1"
     *   And the data product "dp1" is registered in Blindata
     *   And the Blindata observer is configured with the "DATA_PRODUCT_REMOVAL" use case enabled
     * <p>
     * Scenario: DATA_PRODUCT_DELETED event removes the Blindata data product
     *   Given an ODM event of type "DATA_PRODUCT_DELETED"
     *   And the event state contains only "dataProduct" for "dp1"
     *   And the event state does not contain any "dataProductVersion"
     *   When the Blindata observer processes the event
     *   Then the DataProductEventState must expose only the "dataProduct" object
     *   And the DataProductRemoval use case is selected by the factory
     *   And the factory uses the fullyQualifiedName from "dataProduct"
     *   And the observer calls Blindata to delete the data product with fullyQualifiedName "odm://domain.dp1"
     *   And the data product "dp1" is no longer present in Blindata
     */
    @Test
    public void testDataProductDeletedRemovesBlindataDataProduct() throws IOException {
        // Arrange Blindata mock – data product exists and can be deleted
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setUuid("test-uuid");
        dataProduct.setIdentifier("odm://domain.dp1");
        Mockito.when(bdDataProductClient.getDataProduct("odm://domain.dp1"))
                .thenReturn(Optional.of(dataProduct));

        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductDeletion_eventNotification.json"))),
                OdmEventNotificationResource.class
        );

        // Act
        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class
        );

        // Assert HTTP response and notification status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // Verify that Blindata data product was looked up and then deleted
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .getDataProduct("odm://domain.dp1");
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .deleteDataProduct("test-uuid");
    }

    /**
     * Feature: Handle deletion of data products
     * <p>
     * Scenario: DATA_PRODUCT_DELETED event with unexpected dataProductVersion logs a warning and ignores the version
     *   Given an ODM event of type "DATA_PRODUCT_DELETED"
     *   And the raw event payload contains both "dataProduct" and "dataProductVersion"
     *   When the DataProductEventStateConverter converts the event state
     *   Then it must ignore "dataProductVersion" and not map it into DataProductEventState
     *   And it logs a WARN message indicating that "dataProductVersion" is present in a DATA_PRODUCT_DELETED event and will be ignored
     *   And the resulting DataProductEventState exposes only "dataProduct"
     *   When the Blindata observer processes the converted event
     *   Then the DataProductRemoval use case is selected by the factory
     *   And the factory uses the fullyQualifiedName from "dataProduct"
     *   And the observer calls Blindata to delete the data product with fullyQualifiedName "odm://domain.dp1"
     */
    @Test
    public void testDataProductDeletedWithVersionStillDeletesBlindataDataProduct() throws IOException {
        // Arrange Blindata mock – same behavior as normal deletion, version is ignored
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setUuid("test-uuid");
        dataProduct.setIdentifier("odm://domain.dp1");
        Mockito.when(bdDataProductClient.getDataProduct("odm://domain.dp1"))
                .thenReturn(Optional.of(dataProduct));

        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductDeletion_withVersion_eventNotification.json"))),
                OdmEventNotificationResource.class
        );

        // Act
        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class
        );

        // Assert HTTP response and notification status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // Even if dataProductVersion is present in the payload, we still look up and delete by dataProduct FQN
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .getDataProduct("odm://domain.dp1");
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .deleteDataProduct("test-uuid");
    }

    /**
     * Feature: Handle deletion of data products
     * <p>
     * Scenario: DataProductEventState model does not contain dataProductVersion field
     *   When the application is built and the DataProductEventState class is inspected
     *   Then the DataProductEventState type does not define any "dataProductVersion" field
     *   And the DataProductEventStateConverter does not attempt to map "dataProductVersion" into the state for DATA_PRODUCT_DELETED events
     */
    @Test
    public void testDataProductEventStateDoesNotExposeDataProductVersionField() {
        Field[] fields = DataProductEventState.class.getDeclaredFields();
        boolean hasDataProductVersionField = false;
        for (Field field : fields) {
            if ("dataProductVersion".equals(field.getName())) {
                hasDataProductVersionField = true;
                break;
            }
        }
        Assertions.assertThat(hasDataProductVersionField)
                .as("DataProductEventState should not expose a dataProductVersion field")
                .isFalse();
    }
}

