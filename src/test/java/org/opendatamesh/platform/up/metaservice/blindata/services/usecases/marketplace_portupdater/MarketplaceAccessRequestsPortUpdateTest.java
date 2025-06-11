package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.marketplace_portupdater;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace.BDMarketplaceAccessRequestsUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class MarketplaceAccessRequestsPortUpdateTest {

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testMarketplaceAccessRequestsPortUpdate() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, times(1)).uploadPortStatuses(argThat(uploadRes -> {
            assert uploadRes.getAccessRequestsUpdates().size() == 1;
            assert uploadRes.getAccessRequestsUpdates().get(0).getAccessRequestIdentifier().equals("test-access-request-1");
            assert uploadRes.getAccessRequestsUpdates().get(0).getAccessRequestPortsUpdates().size() == 2;
            assert uploadRes.getAccessRequestsUpdates().get(0).getAccessRequestPortsUpdates().get(0).getGrantStatus() == BDMarketplaceAccessRequestsUploadRes.GrantStatusRes.PLATFORM_GRANTED;
            assert uploadRes.getAccessRequestsUpdates().get(0).getAccessRequestPortsUpdates().get(0).getGrantMessage().equals("Access request has been granted successfully");
            return true;
        }));
    }

    @Test
    public void testMarketplaceAccessRequestsPortUpdateWithMissingStatus() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );
        initialState.getOdmMarketplaceAccessRequestPortUploadResult().setStatus(null);

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, never()).uploadPortStatuses(any(BDMarketplaceAccessRequestsUploadRes.class));
    }

    @Test
    public void testMarketplaceAccessRequestsPortUpdateWithPendingStatus() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );
        initialState.getOdmMarketplaceAccessRequestPortUploadResult().setStatus(
                OdmExecutorResultReceivedEventExecutorResponse.ExecutorResultReceivedEventExecutorResponseStatus.PENDING
        );

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, never()).uploadPortStatuses(any(BDMarketplaceAccessRequestsUploadRes.class));
    }

    @Test
    public void testMarketplaceAccessRequestsPortUpdateWithMissingIdentifier() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );
        initialState.getOdmMarketplaceAccessRequestPortUploadResult().setAccessRequestIdentifier(null);

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, never()).uploadPortStatuses(any(BDMarketplaceAccessRequestsUploadRes.class));
    }

    @Test
    public void testMarketplaceAccessRequestsPortUpdateWithMissingProvider() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );
        initialState.getOdmMarketplaceAccessRequestPortUploadResult().setProvider(null);

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, never()).uploadPortStatuses(any(BDMarketplaceAccessRequestsUploadRes.class));
    }

    @Test
    public void testMarketplaceAccessRequestsPortUpdateWithMissingPorts() throws IOException, UseCaseExecutionException {
        MarketplaceAccessRequestsPortUpdateInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                MarketplaceAccessRequestsPortUpdateInitialState.class
        );
        initialState.getOdmMarketplaceAccessRequestPortUploadResult().getProvider().setDataProductPortsFqn(null);

        MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(initialState));
        MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort blindataOutboundPort = spy(new MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock());

        new MarketplaceAccessRequestsPortUpdate(odmOutboundPort, blindataOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getOdmMarketplaceAccessRequestPortUploadResult();
        verify(blindataOutboundPort, never()).uploadPortStatuses(any(BDMarketplaceAccessRequestsUploadRes.class));
    }
} 