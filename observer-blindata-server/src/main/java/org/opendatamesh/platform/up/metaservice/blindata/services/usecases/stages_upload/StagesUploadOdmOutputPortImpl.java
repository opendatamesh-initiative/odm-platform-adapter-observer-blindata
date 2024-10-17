package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.internals.InternalComponentsDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.*;

@Slf4j
class StagesUploadOdmOutputPortImpl implements StagesUploadOdmOutputPort {

    private final DataProductVersionDPDS dataProductVersion;
    private final ActivityResource activityResource;

    StagesUploadOdmOutputPortImpl(DataProductVersionDPDS dataProductVersion, ActivityResource activityResource) {
        this.dataProductVersion = dataProductVersion;
        this.activityResource = activityResource;
    }

    @Override
    public List<BDDataProductStageRes> extractDataProductStages() {
        if (isACompletedActivity()) {
            BDDataProductStageRes stage = new BDDataProductStageRes();
            stage.setName(activityResource.getStage());
            stage.setValue(dataProductVersion.getInfo().getVersionNumber());
            return Lists.newArrayList(stage);
        } else {
            SortedSet<String> stages = extractStageNamesFromDescriptor();
            return buildBlindataStages(stages);
        }
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    private List<BDDataProductStageRes> buildBlindataStages(SortedSet<String> stages) {
        List<BDDataProductStageRes> bdStages = new ArrayList<>();
        int i = 0;
        for (String stageName : stages) {
            BDDataProductStageRes bdStage = new BDDataProductStageRes();
            bdStage.setName(stageName);
            bdStage.setOrder(i);
            i++;
            bdStages.add(bdStage);
        }
        return bdStages;
    }

    private SortedSet<String> extractStageNamesFromDescriptor() {
        return Optional.ofNullable(dataProductVersion.getInternalComponents())
                .map(InternalComponentsDPDS::getLifecycleInfo)
                .map(LifecycleInfoDPDS::getStageNames).orElse(Collections.emptySortedSet());
    }

    private boolean isACompletedActivity() {
        return activityResource != null && ActivityStatus.PROCESSED.equals(activityResource.getStatus());
    }
}


