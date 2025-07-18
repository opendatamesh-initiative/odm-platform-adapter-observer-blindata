package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy;

import java.util.Date;

public class OdmPolicyEvaluationResultResource {

    private Long id;
    private String dataProductId;
    private Boolean result;
    private OdmPolicyResource policy;
    private Date createdAt;
    private Date updatedAt;

    public OdmPolicyEvaluationResultResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public OdmPolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(OdmPolicyResource policy) {
        this.policy = policy;
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
}
