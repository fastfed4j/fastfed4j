package org.fastfed4j.core.contract;

public enum ContractProposalStatus {

    /**
     * Proposal is waiting to be Accepted or Cancelled.
     */
    Pending,

    /**
     * Proposal was accepted and the new contract will take effect for end-users.
     */
    Accepted, // The Proposal was accepted and activated.

    /**
     * Proposal was cancelled and abandoned. Will NOT take effect for end-users.
     */
    Cancelled

}
