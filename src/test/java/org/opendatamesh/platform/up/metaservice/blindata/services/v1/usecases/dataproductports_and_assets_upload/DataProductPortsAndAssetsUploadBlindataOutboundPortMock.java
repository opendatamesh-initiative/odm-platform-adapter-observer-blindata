package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

import java.util.Optional;

public class DataProductPortsAndAssetsUploadBlindataOutboundPortMock implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortMock(DataProductVersionUploadInitialState initialState) {
        this.initialState = initialState;
    }


    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.ofNullable(initialState.getExistentDataProduct());
    }

    @Override
    public void updateDataProduct(BDDataProductRes bdDataProductRes) {
        //DO NOTHING
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        //DO NOTHING
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        //DO NOTHING
    }

    @Override
    public Optional<BDSystemRes> findExistingSystem(String systemName) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findSystemName(String portDependency) {
        // Use the same regex pattern as in the real implementation
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("blindata:systems:(.+)");
        java.util.regex.Matcher matcher = pattern.matcher(portDependency);
        if (!matcher.find()) return Optional.empty();
        return Optional.ofNullable(
                matcher.groupCount() > 0 ? matcher.group(1) : matcher.group()
        );
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return "\\bx-([\\S]+)";
    }
}