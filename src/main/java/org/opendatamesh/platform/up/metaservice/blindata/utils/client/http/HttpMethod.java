package org.opendatamesh.platform.up.metaservice.blindata.utils.client.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> MAPPINGS = new HashMap<>(16);

    static {
        for (HttpMethod httpMethod : values()) {
            MAPPINGS.put(httpMethod.name(), httpMethod);
        }
    }

    public static HttpMethod resolve(String method) {
        return method != null ? MAPPINGS.get(method) : null;
    }

    public boolean matches(String method) {
        return name().equals(method);
    }
}
