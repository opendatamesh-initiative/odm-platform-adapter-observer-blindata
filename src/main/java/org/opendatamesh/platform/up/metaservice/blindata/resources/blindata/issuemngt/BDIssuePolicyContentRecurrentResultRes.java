package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

import java.util.List;

public class BDIssuePolicyContentRecurrentResultRes {

    private List<BDQualitySemaphoreRes> semaphores;

    private Integer semaphoresNumber;

    private Boolean autoClose;

    public BDIssuePolicyContentRecurrentResultRes() {
        //DO NOTHING
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public Integer getSemaphoresNumber() {
        return semaphoresNumber;
    }

    public void setSemaphoresNumber(Integer semaphoresNumber) {
        this.semaphoresNumber = semaphoresNumber;
    }

    public List<BDQualitySemaphoreRes> getSemaphores() {
        return semaphores;
    }

    public void setSemaphores(List<BDQualitySemaphoreRes> semaphores) {
        this.semaphores = semaphores;
    }

}
