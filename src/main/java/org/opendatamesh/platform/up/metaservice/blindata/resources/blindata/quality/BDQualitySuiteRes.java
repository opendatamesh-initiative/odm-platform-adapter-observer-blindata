package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;

import java.util.Date;


public class BDQualitySuiteRes {
    private String uuid;
    private String code;
    private BDSystemRes system;
    private String name;
    private String description;
    private Boolean published = Boolean.TRUE;
    private String teamCode;
    private Date createdAt;
    private Date updatedAt;

    public BDQualitySuiteRes() {
        //DO NOTHING
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BDSystemRes getSystem() {
        return system;
    }

    public void setSystem(BDSystemRes system) {
        this.system = system;
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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BDQualitySuiteRes{" +
                "uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                ", system=" + system +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", published=" + published +
                ", teamCode='" + teamCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
