package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataProductPortsAndAssetsUploadBlindataOutboundPortImpl implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final BDDataProductClient bdDataProductClient;
    private final String systemDependencyRegex;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient, String systemDependencyRegex) {
        this.bdDataProductClient = bdDataProductClient;
        this.systemDependencyRegex = systemDependencyRegex;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        bdDataProductClient.patchDataProduct(dataProduct);
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        bdDataProductClient.createDataProductAssets(dataProductPortsAssets);
    }

    @Override
    public Optional<BDSystemRes> getSystemDependency(String portDependency) {
        Pattern pattern = Pattern.compile(systemDependencyRegex);
        Matcher matcher = pattern.matcher(portDependency);

        if (matcher.find()) {
            //regex example blindata:systems:(.+)
            String systemName = matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
            BDSystemRes systemRes = new BDSystemRes();
            systemRes.setName(systemName);
            return Optional.of(systemRes);
        }

        return Optional.empty();
    }

}
