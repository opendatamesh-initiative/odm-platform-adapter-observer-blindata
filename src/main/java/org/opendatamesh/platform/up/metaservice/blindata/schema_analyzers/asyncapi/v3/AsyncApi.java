package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3;

import java.util.Map;

class AsyncApi {
    private String asyncapi;
    private String id;
    private String defaultContentType;
    private AsyncApiInfo info;
    private Map<String, AsyncApiChannel> channels;
    /*
    private Map<String, Object> servers;
    private Map<String, Object> operations;
    private Components components;
    */

    public String getAsyncapi() {
        return asyncapi;
    }

    public void setAsyncapi(String asyncapi) {
        this.asyncapi = asyncapi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public AsyncApiInfo getInfo() {
        return info;
    }

    public void setInfo(AsyncApiInfo info) {
        this.info = info;
    }

    public Map<String, AsyncApiChannel> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, AsyncApiChannel> channels) {
        this.channels = channels;
    }
}
