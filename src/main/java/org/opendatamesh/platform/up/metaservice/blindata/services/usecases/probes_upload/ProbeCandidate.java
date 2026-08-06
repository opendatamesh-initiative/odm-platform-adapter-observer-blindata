package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import java.util.Map;

public class ProbeCandidate {
    private String probeName;
    private String checkCode;
    private String checkName;
    private String connectionName;
    private String connectionType;
    private String portFullyQualifiedName;
    private Map<String, Object> contractRule;
    private PhysicalBinding physicalBinding;

    public ProbeCandidate() {
        //DO NOTHING
    }

    public String getProbeName() {
        return probeName;
    }

    public void setProbeName(String probeName) {
        this.probeName = probeName;
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

    public String getPortFullyQualifiedName() {
        return portFullyQualifiedName;
    }

    public void setPortFullyQualifiedName(String portFullyQualifiedName) {
        this.portFullyQualifiedName = portFullyQualifiedName;
    }

    public Map<String, Object> getContractRule() {
        return contractRule;
    }

    public void setContractRule(Map<String, Object> contractRule) {
        this.contractRule = contractRule;
    }

    public PhysicalBinding getPhysicalBinding() {
        return physicalBinding;
    }

    public void setPhysicalBinding(PhysicalBinding physicalBinding) {
        this.physicalBinding = physicalBinding;
    }
}
