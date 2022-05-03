package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.NetworkErrorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseTestMock {

    private static final int REPUTATION = 1;

    private static final int DEFAULT_REPUTATION = 0;

    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;

    FetchReputationUseCaseSyncImpl SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSyncImpl(mGetReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void getReputationSync_success_successReturned() throws Exception {
        //Arrange
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getUseCaseStatus(), is(FetchReputationUseCaseSyncImpl.UseCaseStatus.SUCCESS));
    }

    @Test
    public void getReputationSync_networkError_failureReturned() throws Exception {
        //Arrange
        endpointNetworkError();
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getUseCaseStatus(), is(FetchReputationUseCaseSyncImpl.UseCaseStatus.NETWORK_ERROR));
    }

    @Test
    public void getReputationSync_generalError_failureReturned() throws Exception {
        //Arrange
        endpointGeneralError();
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getUseCaseStatus(), is(FetchReputationUseCaseSyncImpl.UseCaseStatus.FAILURE));
    }

    @Test
    public void getReputationSync_success_correctReputationReturned() throws Exception {
        //Arrange
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void getReputationSync_networkError_zeroReturned() throws Exception {
        //Arrange
        endpointNetworkError();
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(DEFAULT_REPUTATION));
    }

    @Test
    public void getReputationSync_generalError_zeroReturned() throws Exception {
        //Arrange
        endpointGeneralError();
        //Act
        FetchReputationUseCaseSyncImpl.UseCaseResult result = SUT.getReputationSync();
        //Assert
        assertThat(result.getReputation(), is(DEFAULT_REPUTATION));
    }


    // region helper methods

    private void success() throws NetworkErrorException {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, REPUTATION));
    }

    private void endpointNetworkError() throws NetworkErrorException{
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenThrow(new NetworkErrorException());
    }

    private void endpointGeneralError() throws NetworkErrorException{
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, DEFAULT_REPUTATION));
    }

    // endregion helper methods

}

