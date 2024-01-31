package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SystemRes {

    @Schema(description = "The name of the system")
    private String name;

    @Schema(description = "the description of the system")
    private String description;


    @Schema(description = "List of user defined properties")
    private List<AdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    @Schema(description = "The system's technology")
    private String technology;

}
