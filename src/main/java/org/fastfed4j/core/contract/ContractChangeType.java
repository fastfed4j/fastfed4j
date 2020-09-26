package org.fastfed4j.core.contract;

/**
 * Contract Change Types that may require different logical handling by implementors.
 */
public enum ContractChangeType {
        /**
         * No changes to the contract.
         */
        None,

        /**
         * Initial creation of a new contract.
         */
        Create,

        /**
         * Only mutable metadata was updated, such as display settings. No changes to the authentication or
         * provisioning profiles.
         * <p>Most often occurs during a FastFed background refresh, as defined in section 4.1.5
         * of the FastFed Core specification.</p>
         */
        MetadataRefresh,

        /**
         * Authentication or provisioning profiles were changed. This includes activating a new profile,
         * deactivating an existing profile, or modifying the user/group attributes transmitted through an existing
         * authentication or provisioning protocol. If the new Contract is accepted, the Provider must perform
         * any actions that are necessary to enable or disable these capabilities for end-users.
         */
        ProfileChange,

        /**
         * All authentication and provisioning profiles were removed and the relationship between the
         * providers should be deactivated. Authentication and provisioning capabilities should be disabled
         * for end-users.
         */
        Terminate
}
