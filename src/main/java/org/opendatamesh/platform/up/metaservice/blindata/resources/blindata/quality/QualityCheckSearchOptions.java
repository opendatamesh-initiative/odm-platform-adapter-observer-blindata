package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import java.util.List;

public class QualityCheckSearchOptions {
    private String search;
    private String code;
    private List<String> suiteUuid;
    private List<String> physicalEntityUuid;
    private List<String> physicalFieldUuid;
    private String checkGroupCode;
    private Boolean includeProperties;
    private Boolean enabled;
    private Boolean published;

    public QualityCheckSearchOptions() {
        //DO NOTHING
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getSuiteUuid() {
        return suiteUuid;
    }

    public void setSuiteUuid(List<String> suiteUuid) {
        this.suiteUuid = suiteUuid;
    }

    public List<String> getPhysicalEntityUuid() {
        return physicalEntityUuid;
    }

    public void setPhysicalEntityUuid(List<String> physicalEntityUuid) {
        this.physicalEntityUuid = physicalEntityUuid;
    }

    public List<String> getPhysicalFieldUuid() {
        return physicalFieldUuid;
    }

    public void setPhysicalFieldUuid(List<String> physicalFieldUuid) {
        this.physicalFieldUuid = physicalFieldUuid;
    }

    public String getCheckGroupCode() {
        return checkGroupCode;
    }

    public void setCheckGroupCode(String checkGroupCode) {
        this.checkGroupCode = checkGroupCode;
    }

    public Boolean getIncludeProperties() {
        return includeProperties;
    }

    public void setIncludeProperties(Boolean includeProperties) {
        this.includeProperties = includeProperties;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
