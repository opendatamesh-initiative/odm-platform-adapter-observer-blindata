package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSystemClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataProductPortsAndAssetsUploadBlindataOutboundPortImpl implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final BdDataProductClient bdDataProductClient;
    private final String systemDependencyRegex;
    private final BdDataProductConfig dataProductConfig;
    private final BdSystemClient bdSystemClient;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(BdDataProductClient bdDataProductClient, String systemDependencyRegex, BdDataProductConfig dataProductConfig, BdSystemClient bdSystemClient) {
        this.bdDataProductClient = bdDataProductClient;
        this.systemDependencyRegex = systemDependencyRegex;
        this.dataProductConfig = dataProductConfig;
        this.bdSystemClient = bdSystemClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProduct(BDDataProductRes bdDataProductRes) {
        bdDataProductClient.patchDataProduct(bdDataProductRes);
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
    public Optional<BDSystemRes> findExistingSystem(String systemName) {
        BDSystemSearchOptions searchOptions = new BDSystemSearchOptions(systemName);
        return bdSystemClient.getSystem(searchOptions);
    }

    @Override
    public Optional<String> findSystemName(String portDependency) {
        Pattern pattern = Pattern.compile(systemDependencyRegex);
        Matcher matcher = pattern.matcher(portDependency);
        if (!matcher.find()) return Optional.empty();
        return Optional.ofNullable(
                matcher.groupCount() > 0 ? matcher.group(1) : matcher.group()
        );
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return dataProductConfig.getAdditionalPropertiesRegex();
    }

}
