package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.google.common.collect.Lists;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.internals.InternalComponents;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmActivityResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmActivityStatus;

import java.util.*;

class StagesUploadOdmOutboundPortImpl implements StagesUploadOdmOutboundPort {
    private final DataProductVersion dataProductVersion;
    private final OdmActivityResource activityResource;

    StagesUploadOdmOutboundPortImpl(DataProductVersion dataProductVersion, OdmActivityResource activityResource) {
        this.dataProductVersion = dataProductVersion;
        this.activityResource = activityResource;
    }

    @Override
    public List<BDDataProductStageRes> extractDataProductStages() {
        if (activityResource != null) {
            if (isACompletedActivity()) {
                BDDataProductStageRes stage = new BDDataProductStageRes();
                stage.setName(activityResource.getStage());
                stage.setVersion(dataProductVersion.getInfo().getVersion());
                return Lists.newArrayList(stage);
            }
            return Collections.emptyList();
        } else {
            SortedSet<String> stages = extractStageNamesFromDescriptor();
            return buildBlindataStages(stages);
        }
    }

    @Override
    public DataProductVersion getDataProductVersion() {
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
                .map(InternalComponents::getLifecycleInfo) // This should be a LinkedHashMap (Jackson default)
                .map(Map::keySet)
                .map(TreeSet::new)
                .orElseGet(TreeSet::new);
    }


    private boolean isACompletedActivity() {
        return OdmActivityStatus.PROCESSED.equals(activityResource.getStatus());
    }
}


