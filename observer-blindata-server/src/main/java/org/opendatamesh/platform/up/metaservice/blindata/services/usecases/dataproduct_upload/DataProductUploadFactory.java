package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductUploadFactory implements UseCaseFactory {

    @Autowired
    private BDUserClient bdUserClient;

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private BDStewardshipClient bdStewardshipClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${blindata.roleUuid}")
    private String roleUuid;

    private final Set<String> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_CREATED.name(),
            EventType.DATA_PRODUCT_VERSION_CREATED.name()
    );


    @Override
    public UseCase getUseCase(OBEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductUploadBlindataOutputPort blindataOutputPort = new DataProductUploadBlindataOutputPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    roleUuid
            );
            DataProductUploadOdmOutputPort odmOutputPort = initOdmOutputPort(event);
            return new DataProductUpload(
                    odmOutputPort,
                    blindataOutputPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }

    private DataProductUploadOdmOutputPort initOdmOutputPort(OBEventNotificationResource event) throws JsonProcessingException, UseCaseInitException {
        if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_CREATED.name())) {
            DataProductResource odmDataProduct = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductResource.class);
            return new DataProductUploadOdmOutputPortImpl(odmDataProduct);
        } else if (event.getEvent().getType().equalsIgnoreCase(EventType.DATA_PRODUCT_VERSION_CREATED.name())) {
            DataProductVersionDPDS odmDataProductVersion = objectMapper.readValue(event.getEvent().getAfterState().toString(), DataProductVersionDPDS.class);
            return new DataProductUploadOdmOutputPortImpl(odmDataProductVersion.getInfo());
        } else {
            throw new UseCaseInitException("Failed to init OdmOutputPort on DataProductUpload use case.");
        }
    }
}
