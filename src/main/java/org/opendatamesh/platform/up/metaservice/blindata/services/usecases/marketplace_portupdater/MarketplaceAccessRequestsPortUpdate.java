package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace.BDMarketplaceAccessRequestsUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDMarketplaceAccessRequestPortStatusUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class MarketplaceAccessRequestsPortUpdate implements UseCase {

    private final String USE_CASE_PREFIX = "[MarketplaceAccessRequestsPortUpdate]";

    private final MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort;
    private final MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort;

    public MarketplaceAccessRequestsPortUpdate(MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort, MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            OdmExecutorResultReceivedEventExecutorResponse odmMarketplaceAccessRequestPortUploadResult = odmOutboundPort.getOdmMarketplaceAccessRequestPortUploadResult();
            if (!isValidMarketplaceAccessRequestPortUpdateResult(odmMarketplaceAccessRequestPortUploadResult)) {
                return;
            }
            List<String> providerDataProductPortsFqn = odmMarketplaceAccessRequestPortUploadResult.getProvider().getDataProductPortsFqn();
            getUseCaseLogger().info(String.format("%s Marketplace Access Request: %s found %s port updates.", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier(), providerDataProductPortsFqn.size()));
            BDMarketplaceAccessRequestsUploadRes uploadRes =
                    odmExecutorResponseToBDAccessRequestsUploadRes(odmMarketplaceAccessRequestPortUploadResult);

            BDMarketplaceAccessRequestPortStatusUploadResultsRes uploadResultsMessage = blindataOutboundPort.uploadPortStatuses(uploadRes);
            getUseCaseLogger().info(String.format("%s Access Request: %s updated %s access requests on Blindata, discarded %s.", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier(), uploadResultsMessage.getRowUpdated(), uploadResultsMessage.getRowDiscarded()));
            logMarketplaceAccessRequestsUploadErrors(uploadResultsMessage);
        });
    }

    private boolean isValidMarketplaceAccessRequestPortUpdateResult(OdmExecutorResultReceivedEventExecutorResponse odmMarketplaceAccessRequestPortUploadResult) {
        if (!StringUtils.hasText(odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier())) {
            getUseCaseLogger().warn(String.format("%s Missing Access Request identifier.", USE_CASE_PREFIX));
            return false;
        }
        if (odmMarketplaceAccessRequestPortUploadResult.getStatus() == null) {
            getUseCaseLogger().info(String.format("%s Marketplace Access Request: %s missing Port status update", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier()));
            return false;
        }
        if (odmMarketplaceAccessRequestPortUploadResult.getStatus() == OdmExecutorResultReceivedEventExecutorResponse.ExecutorResultReceivedEventExecutorResponseStatus.PENDING) {
            getUseCaseLogger().info(String.format("%s Marketplace Access Request: %s Port status update PENDING, nothing to do", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier()));
            return false;
        }
        if (odmMarketplaceAccessRequestPortUploadResult.getProvider() == null) {
            getUseCaseLogger().warn(String.format("%s Marketplace Access Request: %s Missing Provider.", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier()));
            return false;
        }
        List<String> providerDataProductPortsFqn = odmMarketplaceAccessRequestPortUploadResult.getProvider().getDataProductPortsFqn();
        if (providerDataProductPortsFqn == null || providerDataProductPortsFqn.isEmpty()) {
            getUseCaseLogger().warn(String.format("%s Marketplace Access Request: %s Missing Provider ports.", USE_CASE_PREFIX, odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier()));
            return false;
        }
        return true;
    }

    private BDMarketplaceAccessRequestsUploadRes odmExecutorResponseToBDAccessRequestsUploadRes(OdmExecutorResultReceivedEventExecutorResponse odmMarketplaceAccessRequestPortUploadResult) {
        BDMarketplaceAccessRequestsUploadRes uploadRes = new BDMarketplaceAccessRequestsUploadRes();
        List<String> providerDataProductPortsFqn = odmMarketplaceAccessRequestPortUploadResult.getProvider().getDataProductPortsFqn();
        BDMarketplaceAccessRequestsUploadRes.AccessRequestUpdate accessRequestUpdate = new BDMarketplaceAccessRequestsUploadRes.AccessRequestUpdate();
        accessRequestUpdate.setAccessRequestIdentifier(odmMarketplaceAccessRequestPortUploadResult.getAccessRequestIdentifier());
        List<BDMarketplaceAccessRequestsUploadRes.AccessRequestPortUpdate> marketplaceAccessRequestsPortUpdates = new ArrayList<>();

        for (String providerDataProductPortFqn : providerDataProductPortsFqn) {
            BDMarketplaceAccessRequestsUploadRes.AccessRequestPortUpdate accessRequestPortUpdate = new BDMarketplaceAccessRequestsUploadRes.AccessRequestPortUpdate();
            accessRequestPortUpdate.setPortIdentifier(providerDataProductPortFqn);
            accessRequestPortUpdate.setGrantStatus(odmMarketplaceAccessRequestPortUploadStatusToBDGrantStatus(odmMarketplaceAccessRequestPortUploadResult.getStatus()));
            marketplaceAccessRequestsPortUpdates.add(accessRequestPortUpdate);
        }
        accessRequestUpdate.setAccessRequestPortsUpdates(marketplaceAccessRequestsPortUpdates);
        uploadRes.setAccessRequestsUpdates(List.of(accessRequestUpdate));

        return uploadRes;
    }

    private BDMarketplaceAccessRequestsUploadRes.GrantStatusRes odmMarketplaceAccessRequestPortUploadStatusToBDGrantStatus(OdmExecutorResultReceivedEventExecutorResponse.ExecutorResultReceivedEventExecutorResponseStatus status) {
        switch (status) {
            case DENIED:
                return BDMarketplaceAccessRequestsUploadRes.GrantStatusRes.PLATFORM_DENIED;
            case REVOKED:
                return BDMarketplaceAccessRequestsUploadRes.GrantStatusRes.PLATFORM_REVOKED;
            case ERROR:
                return BDMarketplaceAccessRequestsUploadRes.GrantStatusRes.PLATFORM_ERROR;
            case GRANTED:
                return BDMarketplaceAccessRequestsUploadRes.GrantStatusRes.PLATFORM_GRANTED;
            default:
                return null;
        }
    }

    private void logMarketplaceAccessRequestsUploadErrors(BDMarketplaceAccessRequestPortStatusUploadResultsRes uploadResultsMessage) {
        if (!CollectionUtils.isEmpty(uploadResultsMessage.getErrors())) {
            uploadResultsMessage.getErrors().stream().filter(error -> StringUtils.hasText(error.getMessage()))
                    .forEach(error ->
                            getUseCaseLogger().warn(
                                    String.format("%s Access Request: %s error on port update result upload: %s.", USE_CASE_PREFIX, odmOutboundPort.getOdmMarketplaceAccessRequestPortUploadResult().getAccessRequestIdentifier(), error.getMessage())
                            )
                    );
        }
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
