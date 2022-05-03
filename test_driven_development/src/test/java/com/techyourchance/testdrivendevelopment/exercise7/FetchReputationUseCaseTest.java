package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.NetworkErrorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseTest {
    // region constants
    private static final int REPUTATION = 1;
    private static final int DEFAULT_REPUTATION = 0;
    // endregion constants

    // region helper fields
    GetReputationHttpEndpointSyncTd mGetReputationHttpEndpointSyncTd;
    // endregion helper fields

    FetchReputationUseCaseSyncImpl SUT;

    @Before
    public void setup() throws Exception {
        mGetReputationHttpEndpointSyncTd = new GetReputationHttpEndpointSyncTd();
        SUT = new FetchReputationUseCaseSyncImpl(mGetReputationHttpEndpointSyncTd);
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

    private void endpointNetworkError() {
        mGetReputationHttpEndpointSyncTd.mNetworkError = true;
    }

    private void endpointGeneralError() {
        mGetReputationHttpEndpointSyncTd.mGeneralError = true;
    }

    // endregion helper methods

    // region helper clases

    private class GetReputationHttpEndpointSyncTd implements GetReputationHttpEndpointSync {
        public boolean mGeneralError;
        public boolean mNetworkError;

        @Override
        public EndpointResult getReputationSync() throws NetworkErrorException {
            if (mNetworkError) {
                throw new NetworkErrorException();
            } else if (mGeneralError) {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, DEFAULT_REPUTATION);
            } else {
                return new EndpointResult(EndpointStatus.SUCCESS, REPUTATION);
            }
        }
    }
    // endregion helper clases
}

