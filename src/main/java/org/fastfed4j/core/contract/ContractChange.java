package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.core.metadata.IdentityProviderMetadata;
import org.fastfed4j.core.util.CompatibilityUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utility to determine the changes between two versions of a Contract. Indicates the change type and the list
 * of authentication/provisioning capabilities being added or removed.
 */
public class ContractChange {
    private static final CompatibilityUtils compatibilityUtils = new CompatibilityUtils();

    private final FastFedConfiguration configuration;
    private final Contract oldContract;
    private final Contract newContract;
    private ContractChangeType changeType;
    private Set<String> authenticationProfilesAdded = new HashSet<>();
    private Set<String> authenticationProfilesRemoved = new HashSet<>();
    private Set<String> provisioningProfilesAdded = new HashSet<>();
    private Set<String> provisioningProfilesRemoved = new HashSet<>();

    /**
     * Amend a Contract as a result of a FastFed Background Refresh, as defined in Section 4.1.5
     * of the FastFed Core specification.
     * @param currentContract the current active contract
     * @param idpMetadata refreshed version of the Identity Provider Metadata
     * @param appMetadata refreshed version of the Application Provider Metadata
     * @return ContractChange containing the original contract, new contract, and change details.
     */
    public static ContractChange backgroundRefresh(Contract currentContract,
                                                   IdentityProviderMetadata idpMetadata,
                                                   ApplicationProviderMetadata appMetadata)
    {
        Objects.requireNonNull(currentContract, "currentContract must not be null");
        Objects.requireNonNull(idpMetadata, "idpMetadata must not be null");
        Objects.requireNonNull(appMetadata, "appMetadata must not be null");

        // Clone the current contract
        Contract newContract = new Contract(currentContract);

        // Only a subset of attributes can be mutated by a recurring background refresh.
        newContract.getIdentityProvider().setProviderContactInformation( idpMetadata.getProviderContactInformation());
        newContract.getIdentityProvider().setDisplaySettings( idpMetadata.getDisplaySettings());
        newContract.getApplicationProvider().setProviderContactInformation( appMetadata.getProviderContactInformation());
        newContract.getApplicationProvider().setDisplaySettings( appMetadata.getDisplaySettings());

        // Amongst the Capabilities, only the signing algorithms can be mutated by a recurring background refresh.
        Capabilities sharedCapabilities =
                compatibilityUtils.getSharedCapabilities(idpMetadata.getCapabilities(), appMetadata.getCapabilities());

        Set<String> compatibleSigningAlgorithms = sharedCapabilities.getSigningAlgorithms();

        // For safety reasons, signing algorithms are not removed from the set of capabilities except during a
        // full FastFed Handshake, as the accidental removal of a signing algorithm that is currently in use may
        // cause breakage of existing federation relationships during the background refresh.
        // Instead, any new signing algorithms are added, with the expectations that parties will prefer to
        // use the strongest algorithms available, and hence old deprecated algorithms will naturally
        // cease being used when all parties publish their compatibility with newer algorithms.
        // Providers should only abandon a signing algorithm when confident it is no longer being used, or when
        // the security risk of continuing to use it outweighs the risk of breaking federation partners who are
        // still relying upon the old algorithm.
        // To reflect this logic, the following code starts with the current signing algorithms and then
        // overlays any new algorithms that are mutually compatible across both providers.
        Set<String> newSigningAlgorithms = new HashSet<>(currentContract.getSigningAlgorithms());
        newSigningAlgorithms.addAll(compatibleSigningAlgorithms);
        newContract.setSigningAlgorithms( newSigningAlgorithms);

        ContractChange contractChange = new ContractChange(currentContract, newContract);

        //Sanity check the change type. Anything besides "None" or "MetadataRefresh" implies a bug
        //in the logic of this function, and a potential security risk as critical aspects of the
        //contract would be changed outside the context of a FastFed Handshake.
        switch (contractChange.getChangeType()) {
            case None:
            case MetadataRefresh:
                break;
            default:
                throw new FastFedSecurityException(
                        "Illegal contract change during background refresh (changeType='"
                                + contractChange.getChangeType() + "')");

        }
        return contractChange;
    }

    /**
     * Construct a ContractChange by comparing prior and new versions of the contract.
     * @param newContract new version of the contract
     * @param oldContract prior version of the contract
     */
    public ContractChange(Contract newContract, Contract oldContract) {
        Objects.requireNonNull(newContract);
        this.configuration = newContract.getFastFedConfiguration();
        this.oldContract = oldContract;
        this.newContract = newContract;
        setChangeDetails();
    }

    /**
     * Returns the older version of the contract before the change
     * @return Contract
     */
    public Contract getOldContract() {
        return oldContract;
    }

    /**
     * Returns the new version of the contract after the change
     * @return Contract
     */
    public Contract getNewContract() {
        return newContract;
    }

    /**
     * Returns the type of change between the old and new versions
     * @return ContractChangeType
     */
    public ContractChangeType getChangeType() {
        return changeType;
    }

    /**
     * Returns the collection of new authentication profiles that were added to the contract and hence should be activated
     * activated if the change is accepted.
     * @return profile URNs that were added
     */
    public Set<String> getAuthenticationProfilesAdded() {
        return authenticationProfilesAdded;
    }

    /**
     * Returns the collection of authentication profiles that were removed from the contract and hence should be
     * disabled if the change is accepted.
     * @return profile URNs that were removed
     */
    public Set<String> getAuthenticationProfilesRemoved() {
        return authenticationProfilesRemoved;
    }

    /**
     * Returns the collection of new provisioning profiles that were added to the contract and hence should be activated
     * activated if the change is accepted.
     * @return profile URNs that were added
     */
    public Set<String> getProvisioningProfilesAdded() {
        return provisioningProfilesAdded;
    }

    /**
     * Returns the collection of provisioning profiles that were removed from the contract and hence should be
     * disabled if the change is accepted.
     * @return profile URNs that were removed
     */
    public Set<String> getProvisioningProfilesRemoved() {
        return provisioningProfilesRemoved;
    }

    /**
     * Determine which capabilities were added or removed and set the results into the object.
     */
    private void setChangeDetails() {
        EnabledProfiles oldProfiles = (oldContract == null ? new EnabledProfiles(configuration) : oldContract.getEnabledProfiles());
        EnabledProfiles newProfiles = newContract.getEnabledProfiles();

        this.authenticationProfilesAdded = getAdditions(oldProfiles.getAuthenticationProfiles(), newProfiles.getAuthenticationProfiles());
        this.authenticationProfilesRemoved = getRemovals(oldProfiles.getAuthenticationProfiles(), newProfiles.getAuthenticationProfiles());
        this.provisioningProfilesAdded = getAdditions(oldProfiles.getProvisioningProfiles(), newProfiles.getProvisioningProfiles());
        this.provisioningProfilesRemoved = getRemovals(oldProfiles.getProvisioningProfiles(), newProfiles.getProvisioningProfiles());

        boolean hasProfileChange = (
                !authenticationProfilesAdded.isEmpty()
                || !authenticationProfilesRemoved.isEmpty()
                || !provisioningProfilesAdded.isEmpty()
                || !provisioningProfilesRemoved.isEmpty()
        );

        boolean hasOtherChange = (!hasProfileChange && !newContract.equals(oldContract));

        boolean allProfilesRemoved = (
                newProfiles.getAuthenticationProfiles().isEmpty()
                && newProfiles.getProvisioningProfiles().isEmpty()
        );

        if (oldContract == null) {
            changeType = ContractChangeType.Create;
        }
        else if (allProfilesRemoved) {
            changeType = ContractChangeType.Terminate;
        }
        else if (hasProfileChange) {
            changeType = ContractChangeType.ProfileChange;
        }
        else if (hasOtherChange) {
            changeType = ContractChangeType.MetadataRefresh;
        }
        else {
            changeType = ContractChangeType.None;
        }
    }

    /**
     * Given a before-and-after view of a collection, determine which contents were added.
     * @param oldProfiles
     * @param newProfiles
     * @return additions
     */
    private Set<String> getAdditions (Set<String> oldProfiles, Set<String> newProfiles) {
        Set<String> result = new HashSet<>(newProfiles);
        for (String s : oldProfiles) {
            result.remove(s);
        }
        return result;
    }

    /**
     * Given a before-and-after view of a collection, determine which contents were removed.
     * @param oldProfiles
     * @param newProfiles
     * @return
     */
    private Set<String> getRemovals (Set<String> oldProfiles, Set<String> newProfiles) {
        Set<String> result = new HashSet<>(oldProfiles);
        for (String s : newProfiles) {
            result.remove(s);
        }
        return result;
    }
}
