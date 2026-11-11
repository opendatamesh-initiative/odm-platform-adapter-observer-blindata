package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2;

import java.util.Date;

/**
 * DevOps 2.0 activity carried by the {@code ACTIVITY_SUCCEEDED} and {@code ACTIVITY_FAILED} events.
 * Only the fields the observer needs are modelled; any other DevOps field is ignored on deserialization.
 */
public class OdmActivityResourceV2 {
    private String uuid;
    private String dataProductVersionUuid;
    private String dataProductFqn;
    /**
     * The descriptor {@code info.version} (Registry V2 {@code versionNumber}), not the Git tag.
     */
    private String dataProductVersionNumber;
    /**
     * The lifecycle stage of the activity.
     */
    private String name;
    private Integer sortOrder;
    private OdmActivityStatusV2 status;
    private Date startedAt;
    private Date finishedAt;

    public OdmActivityResourceV2() {
        //DO NOTHING
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDataProductVersionUuid() {
        return dataProductVersionUuid;
    }

    public void setDataProductVersionUuid(String dataProductVersionUuid) {
        this.dataProductVersionUuid = dataProductVersionUuid;
    }

    public String getDataProductFqn() {
        return dataProductFqn;
    }

    public void setDataProductFqn(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }

    public String getDataProductVersionNumber() {
        return dataProductVersionNumber;
    }

    public void setDataProductVersionNumber(String dataProductVersionNumber) {
        this.dataProductVersionNumber = dataProductVersionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public OdmActivityStatusV2 getStatus() {
        return status;
    }

    public void setStatus(OdmActivityStatusV2 status) {
        this.status = status;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }
}
