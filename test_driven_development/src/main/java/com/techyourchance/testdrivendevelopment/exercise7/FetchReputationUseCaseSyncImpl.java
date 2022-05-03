package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.NetworkErrorException;

public class FetchReputationUseCaseSyncImpl implements FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    public FetchReputationUseCaseSyncImpl(GetReputationHttpEndpointSync mGetReputationHttpEndpointSync) {
        this.mGetReputationHttpEndpointSync = mGetReputationHttpEndpointSync;
    }

    @Override
    public UseCaseResult getReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult result;

        try {
            result = mGetReputationHttpEndpointSync.getReputationSync();
        } catch (NetworkErrorException e) {
            return new UseCaseResult(UseCaseStatus.NETWORK_ERROR, 0);
        }

        switch (result.getStatus()) {
            case SUCCESS:
                return new UseCaseResult(UseCaseStatus.SUCCESS, result.getReputation());
            case GENERAL_ERROR:
                return new UseCaseResult(UseCaseStatus.FAILURE, result.getReputation());
            default:
                throw new RuntimeException("RunTimeException " + result);
        }
    }
}