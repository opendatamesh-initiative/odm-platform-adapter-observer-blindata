package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

import java.util.ArrayList;
import java.util.List;

public class BDQualityProbesDefinitionRes {
    private String rootUuid;
    private String name;
    private String type;
    private String checkCode;
    private String checkName;
    private BDQualityProbesProjectRes project;
    private List<BDQualityProbesQueryRes> queries = new ArrayList<>();

    public BDQualityProbesDefinitionRes() {
        //DO NOTHING
    }

    public String getRootUuid() {
        return rootUuid;
    }

    public void setRootUuid(String rootUuid) {
        this.rootUuid = rootUuid;
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

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public BDQualityProbesProjectRes getProject() {
        return project;
    }

    public void setProject(BDQualityProbesProjectRes project) {
        this.project = project;
    }

    public List<BDQualityProbesQueryRes> getQueries() {
        return queries;
    }

    public void setQueries(List<BDQualityProbesQueryRes> queries) {
        this.queries = queries;
    }
}
