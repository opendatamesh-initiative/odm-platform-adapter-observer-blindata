package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductRemovalFactory implements UseCaseFactory {

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<String> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_DELETED.name()
    );


    @Override
    public UseCase getUseCase(OBEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductRemoval use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductRemovalOdmOutputPort odmOutputPort = initOdmOutputPort(event);
            DataProductRemovalBlindataOutputPort blindataOutputPort = new DataProductRemovalBlindataOutputPortImpl(
                    bdDataProductClient
            );
            return new DataProductRemoval(
                    odmOutputPort,
                    blindataOutputPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductRemoval use case." + e.getMessage(), e);
        }
    }

    private DataProductRemovalOdmOutputPort initOdmOutputPort(OBEventNotificationResource event) throws JsonProcessingException {
        try {
            DataProductVersionDPDS dataProductVersion = objectMapper.readValue(event.getEvent().getBeforeState().toString(), DataProductVersionDPDS.class);
            String fullyQualifiedName = dataProductVersion.getInfo().getFullyQualifiedName();
            return new DataProductRemovalOdmOutputPortImpl(fullyQualifiedName);
        } catch (JacksonException e) {
            DataProductResource dataProductResource = objectMapper.readValue(event.getEvent().getBeforeState().toString(), DataProductResource.class);
            String fullyQualifiedName = dataProductResource.getFullyQualifiedName();
            return new DataProductRemovalOdmOutputPortImpl(fullyQualifiedName);
        }
    }
}

