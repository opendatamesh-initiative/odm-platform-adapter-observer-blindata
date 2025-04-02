package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2;

import java.util.Map;

class AsyncApi {
    private String asyncapi;
    private String id;
    private AsyncApiInfo info;
    private Map<String, Object> servers;
    private String defaultContentType;
    private Map<String, AsyncApiChannelItem> channels;
    /*
    private Components components;
    private List<Tag> tags;
    private ExternalDocumentation externalDocs;
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

    public AsyncApiInfo getInfo() {
        return info;
    }

    public void setInfo(AsyncApiInfo info) {
        this.info = info;
    }

    public Map<String, Object> getServers() {
        return servers;
    }

    public void setServers(Map<String, Object> servers) {
        this.servers = servers;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public Map<String, AsyncApiChannelItem> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, AsyncApiChannelItem> channels) {
        this.channels = channels;
    }
}
