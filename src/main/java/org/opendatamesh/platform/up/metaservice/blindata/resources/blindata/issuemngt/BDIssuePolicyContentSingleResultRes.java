package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;


import java.util.List;

public class BDIssuePolicyContentSingleResultRes {

    private List<BDQualitySemaphoreRes> semaphores;

    public List<BDQualitySemaphoreRes> getSemaphores() {
        return semaphores;
    }

    public void setSemaphores(List<BDQualitySemaphoreRes> semaphores) {
        this.semaphores = semaphores;
    }
}
