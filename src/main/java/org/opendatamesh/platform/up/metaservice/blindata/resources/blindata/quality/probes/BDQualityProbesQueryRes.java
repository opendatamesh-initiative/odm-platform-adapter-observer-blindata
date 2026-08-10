package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

public class BDQualityProbesQueryRes {
    private String connectionName;
    private String connectionType;
    private Object queryBody;

    public BDQualityProbesQueryRes() {
        //DO NOTHING
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public Object getQueryBody() {
        return queryBody;
    }

    public void setQueryBody(Object queryBody) {
        this.queryBody = queryBody;
    }
}
