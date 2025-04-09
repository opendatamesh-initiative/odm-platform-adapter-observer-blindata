package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2;

import java.util.List;

class AsyncApiChannelItem {

    private String description;
    private String ref;
    private List<String> servers;
    private AsyncApiOperation subscribe;
    private AsyncApiOperation publish;

   /*
    private Map<String, Object> parameters;
    private Map<String, Object> bindings;
    */

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public AsyncApiOperation getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(AsyncApiOperation subscribe) {
        this.subscribe = subscribe;
    }

    public AsyncApiOperation getPublish() {
        return publish;
    }

    public void setPublish(AsyncApiOperation publish) {
        this.publish = publish;
    }
}
