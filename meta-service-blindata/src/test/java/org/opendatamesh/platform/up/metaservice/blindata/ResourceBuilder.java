package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceBuilder {

    ObjectMapper mapper;

    public ResourceBuilder() {
        mapper = new ObjectMapper();
    }

    public <T> T readResourceFromFile(String filePath, Class<T> resourceType) throws IOException {
        String fileContent = readResourceFromFile(filePath);
        return mapper.readValue(fileContent, resourceType);
    }

    public String readResourceFromFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

}
