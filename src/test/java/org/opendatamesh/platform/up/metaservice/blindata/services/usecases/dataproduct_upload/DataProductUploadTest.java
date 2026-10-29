package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DataProductUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private UseCaseLogger originalLogger;
    private UseCaseLogger mockLogger;

    @BeforeEach
    void setUpLogger() {
        originalLogger = UseCaseLoggerContext.getUseCaseLogger();
        mockLogger = mock(UseCaseLogger.class);
        UseCaseLoggerContext.setUseCaseLogger(mockLogger);
    }

    @AfterEach
    void restoreLogger() {
        UseCaseLoggerContext.setUseCaseLogger(originalLogger);
    }

    private DataProductUploadInitialState load(String fixture) throws IOException {
        return objectMapper.readValue(
                Resources.toByteArray(getClass().getResource(fixture)),
                DataProductUploadInitialState.class
        );
    }

    private DataProductUploadBlindataOutboundPort newPort(DataProductUploadInitialState initialState) {
        return spy(new DataProductUploadBlindataOutboundPortMock(initialState));
    }

    private void run(DataProductUploadInitialState initialState, DataProductUploadBlindataOutboundPort port) throws UseCaseExecutionException {
        new DataProductUpload(new DataProductUploadOdmOutboundPortMock(initialState), port).execute();
    }

    private BDDataProductRes captureCreated(DataProductUploadBlindataOutboundPort port) {
        ArgumentCaptor<BDDataProductRes> captor = ArgumentCaptor.forClass(BDDataProductRes.class);
        verify(port, times(1)).createDataProduct(captor.capture());
        verify(port, never()).updateDataProduct(any());
        return captor.getValue();
    }

    private BDDataProductRes captureUpdated(DataProductUploadBlindataOutboundPort port) {
        ArgumentCaptor<BDDataProductRes> captor = ArgumentCaptor.forClass(BDDataProductRes.class);
        verify(port, times(1)).updateDataProduct(captor.capture());
        verify(port, never()).createDataProduct(any());
        return captor.getValue();
    }

    @Test
    public void testDataProductCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUpdateAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(1)).updateDataProduct(any());
        verify(blindataOutboundPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductAndResponsibilityCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUpdateAndResponsibilityCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUploadWithEmptyOwner() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_4.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductCreationWithAdditionalProperties() throws UseCaseExecutionException, IOException {
        // load initial state
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_5.json")),
                DataProductUploadInitialState.class
        );

        // prepare ports
        DataProductUploadOdmOutboundPort odmOutboundPort =
                new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort =
                spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        // execute use case
        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        // capture arguments passed to createDataProduct and updateDataProduct
        ArgumentCaptor<BDDataProductRes> createCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);
        ArgumentCaptor<BDDataProductRes> updateCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);

        // verify create called once, update never called
        verify(blindataOutboundPort, times(1)).createDataProduct(createCaptor.capture());
        verify(blindataOutboundPort, never()).updateDataProduct(updateCaptor.capture());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());

        // inspect the captured DataProduct for creation
        BDDataProductRes createdDp = createCaptor.getValue();
        assertThat(createdDp).isNotNull();

        List<BDAdditionalPropertiesRes> expectedProps =
                Lists.newArrayList(new BDAdditionalPropertiesRes("property", "propertyToBeImported"));

        assertThat(createdDp.getAdditionalProperties())
                .as("Check that additional properties include the custom prop")
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedProps);

        assertThat(createdDp.getProductType()).isEqualTo("source-aligned");

        // confirm that no update was captured
        assertThat(updateCaptor.getAllValues()).isEmpty();
    }

    @Test
    public void testDataProductCreationWithArrayAdditionalProperties() throws UseCaseExecutionException, IOException {
        // load initial state with array custom properties
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_6.json")),
                DataProductUploadInitialState.class
        );

        // prepare ports
        DataProductUploadOdmOutboundPort odmOutboundPort =
                new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort =
                spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        // execute use case
        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        // capture arguments passed to createDataProduct
        ArgumentCaptor<BDDataProductRes> createCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);

        // verify create called once, update never called
        verify(blindataOutboundPort, times(1)).createDataProduct(createCaptor.capture());
        verify(blindataOutboundPort, never()).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());

        // inspect the captured DataProduct for creation
        BDDataProductRes createdDp = createCaptor.getValue();
        assertThat(createdDp).isNotNull();

        // Check that array properties create multiple separate additional properties
        List<BDAdditionalPropertiesRes> additionalProps = createdDp.getAdditionalProperties();
        assertThat(additionalProps).hasSize(6); // 2 categories + 3 tags + 1 single property

        // Verify that array values create separate properties for each element
        List<BDAdditionalPropertiesRes> tagsProps = additionalProps.stream()
                .filter(prop -> "tags".equals(prop.getName()))
                .collect(Collectors.toList());
        assertThat(tagsProps).hasSize(3);
        assertThat(tagsProps).extracting("value")
                .containsExactlyInAnyOrder("tag1", "tag2", "tag3");

        List<BDAdditionalPropertiesRes> categoriesProps = additionalProps.stream()
                .filter(prop -> "categories".equals(prop.getName()))
                .collect(Collectors.toList());
        assertThat(categoriesProps).hasSize(2);
        assertThat(categoriesProps).extracting("value")
                .containsExactlyInAnyOrder("cat1", "cat2");

        BDAdditionalPropertiesRes singleProp = additionalProps.stream()
                .filter(prop -> "singleProperty".equals(prop.getName()))
                .findFirst()
                .orElse(null);
        assertThat(singleProp).isNotNull();
        assertThat(singleProp.getValue()).isEqualTo("singleValue");
    }

    @Test
    public void testDataProductUpdatePreservesVersion() throws UseCaseExecutionException, IOException {
        // load initial state for update scenario
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        // Set an existing version in Blindata that differs from ODM version
        String existingVersionInBlindata = "0.5.0";
        String odmVersion = initialState.getDataProductInfo().getVersion(); // "1.0.0"
        initialState.getExistentDataProduct().setVersion(existingVersionInBlindata);

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        // capture arguments passed to updateDataProduct
        ArgumentCaptor<BDDataProductRes> updateCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);

        // verify update called once, create never called
        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(1)).updateDataProduct(updateCaptor.capture());

        // inspect the captured DataProduct for update
        BDDataProductRes updatedDp = updateCaptor.getValue();
        assertThat(updatedDp).isNotNull();

        // Verify that the version is preserved from the old product, not updated from ODM
        assertThat(updatedDp.getVersion())
                .as("Version should be preserved from existing Blindata product, not updated from ODM")
                .isEqualTo(existingVersionInBlindata)
                .isNotEqualTo(odmVersion);
    }


    @Test
    public void testUpdatePreservesStoredTeamWhenModeOff() throws Exception {
        DataProductUploadInitialState state = load("initial_state_7.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("team-a");
        verify(port, never()).findDataProductDomain(any());
    }

    @Test
    public void testCreateWithoutTeamWhenModeOff() throws Exception {
        DataProductUploadInitialState state = load("initial_state_8.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureCreated(port).getTeamCode()).isNull();
        verify(port, never()).findDataProductDomain(any());
    }

    @Test
    public void testCreateAppliesDomainTeamWhenIfMissing() throws Exception {
        DataProductUploadInitialState state = load("initial_state_9.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureCreated(port).getTeamCode()).isEqualTo("dom-team");
    }

    @Test
    public void testUpdateAppliesDomainTeamWhenIfMissingAndStoredTeamEmpty() throws Exception {
        DataProductUploadInitialState state = load("initial_state_10.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("dom-team");
    }

    @Test
    public void testUpdateKeepsStoredTeamWhenIfMissing() throws Exception {
        DataProductUploadInitialState state = load("initial_state_11.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("team-a");
        verify(port, never()).findDataProductDomain(any());
    }

    @Test
    public void testUpdateReplacesStoredTeamWhenAlways() throws Exception {
        DataProductUploadInitialState state = load("initial_state_12.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("dom-team");
    }

    @Test
    public void testDomainNotFoundWarnsWhenBlocking() throws Exception {
        DataProductUploadInitialState state = load("initial_state_13.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("team-a");
        ArgumentCaptor<String> warn = ArgumentCaptor.forClass(String.class);
        verify(mockLogger, times(1)).warn(warn.capture());
        assertThat(warn.getValue()).contains("[#206]");
    }

    @Test
    public void testDomainNotFoundLogsInfoWhenNotBlocking() throws Exception {
        DataProductUploadInitialState state = load("initial_state_14.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("team-a");
        verify(mockLogger, never()).warn(anyString());
        ArgumentCaptor<String> info = ArgumentCaptor.forClass(String.class);
        verify(mockLogger, atLeastOnce()).info(info.capture());
        assertThat(info.getAllValues()).filteredOn(m -> m.contains("not found on Blindata")).hasSize(1)
                .allSatisfy(m -> assertThat(m).doesNotContain("[#206]"));
    }

    @Test
    public void testDomainWithoutTeamKeepsTeamWithoutWarning() throws Exception {
        DataProductUploadInitialState state = load("initial_state_15.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, port);
        assertThat(captureUpdated(port).getTeamCode()).isEqualTo("team-a");
        verify(mockLogger, never()).warn(anyString());

        DataProductUploadInitialState createState = load("initial_state_16.json");
        DataProductUploadBlindataOutboundPort createPort = newPort(createState);
        run(createState, createPort);
        assertThat(captureCreated(createPort).getTeamCode()).isNull();
        verify(mockLogger, never()).warn(anyString());
    }

    @Test
    public void testDryRunLooksUpDomainWithoutWriting() throws Exception {
        DataProductUploadInitialState state = load("initial_state_9.json");
        DataProductUploadBlindataOutboundPort port = newPort(state);
        run(state, new DataProductUploadBlindataOutboundPortDryRunImpl(port));
        verify(port, times(1)).findDataProductDomain("Test Domain");
        verify(port, never()).createDataProduct(any());
        verify(port, never()).updateDataProduct(any());
    }
}
