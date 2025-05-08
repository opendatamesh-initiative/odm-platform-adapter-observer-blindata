package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy;


import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;
import java.util.List;

public class OdmPolicyResource {

    private Long id;
    private Long rootId;
    private String name;
    private String displayName;
    private String description;
    private Boolean blockingFlag;
    private String rawContent;
    private String suite;
    private List<OdmPolicyEvaluationEventResource> evaluationEvents;
    private String filteringExpression;
    private Boolean isLastVersion;
    private OdmPolicyEngineResource policyEngine;
    private Date createdAt;
    private Date updatedAt;

    private ObjectNode externalContext;

    public OdmPolicyResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlockingFlag() {
        return blockingFlag;
    }

    public void setBlockingFlag(Boolean blockingFlag) {
        this.blockingFlag = blockingFlag;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public List<OdmPolicyEvaluationEventResource> getEvaluationEvents() {
        return evaluationEvents;
    }

    public void setEvaluationEvents(List<OdmPolicyEvaluationEventResource> evaluationEvents) {
        this.evaluationEvents = evaluationEvents;
    }

    public String getFilteringExpression() {
        return filteringExpression;
    }

    public void setFilteringExpression(String filteringExpression) {
        this.filteringExpression = filteringExpression;
    }

    public Boolean getLastVersion() {
        return isLastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        isLastVersion = lastVersion;
    }

    public OdmPolicyEngineResource getPolicyEngine() {
        return policyEngine;
    }

    public void setPolicyEngine(OdmPolicyEngineResource policyEngine) {
        this.policyEngine = policyEngine;
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

    public ObjectNode getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ObjectNode externalContext) {
        this.externalContext = externalContext;
    }
}
