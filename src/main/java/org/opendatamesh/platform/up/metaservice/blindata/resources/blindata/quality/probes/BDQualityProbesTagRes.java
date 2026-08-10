package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

public class BDQualityProbesTagRes {
    private String uuid;
    private String name;
    private String description;
    private BDQualityProbesProjectRes project;

    public BDQualityProbesTagRes() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BDQualityProbesProjectRes getProject() {
        return project;
    }

    public void setProject(BDQualityProbesProjectRes project) {
        this.project = project;
    }
}
