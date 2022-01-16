package com.techyourchance.testdrivendevelopment.exercise7;

interface FetchReputationUseCaseSync {

    enum UseCaseStatus {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    class UseCaseResult {
        private UseCaseStatus useCaseStatus;
        private int reputation;

        public UseCaseResult(UseCaseStatus useCaseStatus, int reputation) {
            this.useCaseStatus = useCaseStatus;
            this.reputation = reputation;
        }

        public UseCaseStatus getUseCaseStatus() {
            return useCaseStatus;
        }

        public int getReputation() {
            return reputation;
        }
    }

    UseCaseResult getReputationSync();

}