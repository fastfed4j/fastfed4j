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
         * Creation of a new contract. No prior contract.
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
         * Authentication or provisioning profiles were changed. Requires action to formally
         * enable or disable these capabilities for end-users.
         */
        ProfileChange,

        /**
         * All authentication and provisioning profiles were removed and the relationship between the
         * providers should be deactivated. Authentication and provisioning capabilities should be disabled
         * for end-users.
         */
        Terminate
}
