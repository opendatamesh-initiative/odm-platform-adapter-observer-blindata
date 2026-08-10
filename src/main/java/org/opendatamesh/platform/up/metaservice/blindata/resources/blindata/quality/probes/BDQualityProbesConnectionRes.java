package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

public class BDQualityProbesConnectionRes {
    private String uuid;
    private String name;
    private String type;

    public BDQualityProbesConnectionRes() {
        //DO NOTHING
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
