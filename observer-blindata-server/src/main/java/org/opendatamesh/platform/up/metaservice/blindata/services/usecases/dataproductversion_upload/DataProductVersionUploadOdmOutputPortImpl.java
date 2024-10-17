package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.internals.InternalComponentsDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class DataProductVersionUploadOdmOutputPortImpl implements DataProductVersionUploadOdmOutputPort {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersionDPDS dataProductVersion;
    private final ActivityResource activityResource;

    public DataProductVersionUploadOdmOutputPortImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersionDPDS dataProductVersion, ActivityResource activityResource) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.dataProductVersion = dataProductVersion;
        this.activityResource = activityResource;
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        if (activityResource != null) {
            try {
                return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(activityResource.getDataProductVersion(), DataProductVersionDPDS.class);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        return dataProductVersion;
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports) {
        return dataProductPortAssetAnalyzer.extractPhysicalResourcesFromPorts(ports);
    }

    @Override
    public List<BDDataProductStageRes> extractDataProductStages() {
        if (isACompletedActivity()) {
            BDDataProductStageRes stage = new BDDataProductStageRes();
            stage.setName(activityResource.getStage());
            stage.setValue(getDataProductVersion().getInfo().getVersionNumber());
            return Lists.newArrayList(stage);
        } else {
            SortedSet<String> stages = extractStageNamesFromDescriptor();
            return buildBlindataStages(stages);
        }
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
