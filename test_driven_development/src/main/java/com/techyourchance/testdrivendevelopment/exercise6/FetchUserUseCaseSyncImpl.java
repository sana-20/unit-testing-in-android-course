package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;


public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    UsersCache mUsersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        mFetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        mUsersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        if (mUsersCache.getUser(userId) != null) {
            return new UseCaseResult(Status.SUCCESS, mUsersCache.getUser(userId));
        }

        FetchUserHttpEndpointSync.EndpointResult result;
        try {
            result = mFetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        switch (result.getStatus()) {
            case SUCCESS:
                User user = new User(result.getUserId(), result.getUsername());
                mUsersCache.cacheUser(user);
                return new UseCaseResult(Status.SUCCESS, user);
            case AUTH_ERROR:
            case GENERAL_ERROR:
                return new UseCaseResult(Status.FAILURE, null);
            default:
                throw new RuntimeException("invalid endpoint result: " + result);
        }
    }
}
