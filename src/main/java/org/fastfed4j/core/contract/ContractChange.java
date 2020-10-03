package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidChangeException;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.core.util.CompatibilityUtils;

import java.util.HashSet;
import java.util.Map;
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
    private Set<String> userAttributesAdded = new HashSet<>();
    private Set<String> userAttributesRemoved = new HashSet<>();
    private Set<String> groupAttributesAdded = new HashSet<>();
    private Set<String> groupAttributesRemoved = new HashSet<>();
    private boolean isActivatingGroupProvisioning;
    private boolean isDeactivatingGroupProvisioning;

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
        // See Section 4.1.5 of the FastFed Core specification.
        newContract.getIdentityProvider().getProviderContactInformation().setEmail( idpMetadata.getProviderContactInformation().getEmail());
        newContract.getIdentityProvider().getProviderContactInformation().setPhone( idpMetadata.getProviderContactInformation().getPhone());
        newContract.getIdentityProvider().getProviderContactInformation().setOrganization( idpMetadata.getProviderContactInformation().getOrganization());
        newContract.getIdentityProvider().getDisplaySettings().setLogoUri( idpMetadata.getDisplaySettings().getLogoUri());
        newContract.getIdentityProvider().getDisplaySettings().setIconUri( idpMetadata.getDisplaySettings().getIconUri());

        newContract.getApplicationProvider().getProviderContactInformation().setEmail( appMetadata.getProviderContactInformation().getEmail());
        newContract.getApplicationProvider().getProviderContactInformation().setPhone( appMetadata.getProviderContactInformation().getPhone());
        newContract.getApplicationProvider().getProviderContactInformation().setOrganization( appMetadata.getProviderContactInformation().getOrganization());
        newContract.getApplicationProvider().getDisplaySettings().setLogoUri( appMetadata.getDisplaySettings().getLogoUri());
        newContract.getApplicationProvider().getDisplaySettings().setIconUri( appMetadata.getDisplaySettings().getIconUri());

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
            case MetadataChange:
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
     * @throws InvalidChangeException if the new contract would cause a prohibited change
     */
    public ContractChange(Contract oldContract, Contract newContract) {
        Objects.requireNonNull(newContract);
        this.configuration = newContract.getFastFedConfiguration();
        this.oldContract = oldContract;
        this.newContract = newContract;
        populateChangeDetails();
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

    public boolean hasChangesToAuthenticationProfiles() {
        return (!authenticationProfilesAdded.isEmpty() || !authenticationProfilesRemoved.isEmpty());
    }

    public boolean hasChangesToProvisioningProfiles() {
        return (!provisioningProfilesAdded.isEmpty() || !provisioningProfilesRemoved.isEmpty());
    }

    public boolean hasChangesToDesiredAttributes() {
        return (!userAttributesAdded.isEmpty() || !userAttributesRemoved.isEmpty() ||
                !groupAttributesAdded.isEmpty() || !groupAttributesRemoved.isEmpty());
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
     * Returns the collection of user attributes that will be newly exposed to the Application as a result
     * of the contract change, expressed in the PreferredSchemaGrammar defined in the FastFedConfiguration.
     * Currently, the only supported schema grammar is SCIM 2.0, as described in Section 3.3.4 of the FastFed Core
     * specification.
     * @return user attributes added to the contract
     */
    public Set<String> getUserAttributesAdded() {
        return userAttributesAdded;
    }

    /**
     * Returns the collection of user attributes that were removed from the contract and hence, going forward, will
     * cease being shared with the Application. The attributes are expressed in the PreferredSchemaGrammar defined
     * in the FastFedConfiguration. Currently, the only supported schema grammar is SCIM 2.0, as described
     * in Section 3.3.4 of the FastFed Core specification.
     * @return user attributes removed from the contract
     */
    public Set<String> getUserAttributesRemoved() {
        return userAttributesRemoved;
    }

    /**
     * Returns the collection of group attributes that will be newly exposed to the Application as a result
     * of the contract change, expressed in the PreferredSchemaGrammar defined in the FastFedConfiguration.
     * Currently, the only supported schema grammar is SCIM 2.0, as described in Section 3.3.4 of the FastFed Core
     * specification.
     * @return group attributes added to the contract
     */
    public Set<String> getGroupAttributesAdded() {
        return groupAttributesAdded;
    }

    /**
     * Returns the collection of group attributes that were removed from the contract and hence, going forward, will
     * cease being shared with the Application. The attributes are expressed in the PreferredSchemaGrammar defined
     * in the FastFedConfiguration. Currently, the only supported schema grammar is SCIM 2.0, as described
     * in Section 3.3.4 of the FastFed Core specification.
     * @return group attributes removed from the contract
     */
    public Set<String> getGroupAttributesRemoved() {
        return groupAttributesRemoved;
    }

    /**
     * Indicates whether the contract change will result in newly enabling the provisioning of groups to the Application.
     * Note that this may not fully activate group provisioning as each protocol may impose additional
     * requirements. For example, sections 4.3 and 4.4 of the Enterprise SCIM Profile require that Applications
     * host a SCIM /ResourceTypes endpoint to prove they support Groups before provisioning will occur. This method
     * simply indicates that the Application has asked for group information in the set of DesiredAttributes.
     * @return true if the change will newly enable the provisioning of groups to the Application
     */
    public boolean isActivatingGroupProvisioning() {
        return isActivatingGroupProvisioning;
    }

    /**
     * Indicates that group provisioning was previously enabled and that the contract change will disable it.
     * Going forward, the Application will cease receiving group provisioning information.
     * @return true if the change will disable the provisioning of groups to the Application
     */
    public boolean isDeactivatingGroupProvisioning() {
        return isDeactivatingGroupProvisioning;
    }

    /**
     * Determine which capabilities were added or removed and set the results into the object.
     */
    private void populateChangeDetails() {

        boolean hasChange = !newContract.equals(oldContract);

        boolean hasProfilesAddedOrRemoved = setChangeDetailsForAuthAndProvisioningProfiles();

        boolean exposesNewAttributesToApplication = setChangeDetailsForDesiredAttributes();

        boolean hasOtherProfileChange = determineIfProfilesChanged();

        boolean hasOtherMetadataChange = (hasChange && !hasProfilesAddedOrRemoved && !hasOtherProfileChange);

        boolean allProfilesRemoved = hasEnabledProfiles(oldContract) && !hasEnabledProfiles(newContract);

        if (oldContract == null) {

            changeType = ContractChangeType.Create;
        }
        else if (allProfilesRemoved) {
            changeType = ContractChangeType.Terminate;
        }
        else if (hasProfilesAddedOrRemoved || hasOtherProfileChange) {
            changeType = ContractChangeType.ProfileChange;
        }
        else if (hasOtherMetadataChange) {
            changeType = ContractChangeType.MetadataChange;
        }
        else {
            changeType = ContractChangeType.None;
        }
    }

    private boolean hasEnabledProfiles(Contract contract) {
        if (contract == null) {
            return false;
        }
        return !contract.getEnabledProfiles().getAuthenticationProfiles().isEmpty()
               || !contract.getEnabledProfiles().getProvisioningProfiles().isEmpty();
    }

    private boolean determineIfProfilesChanged() {
        Map<String, Metadata> oldAppProfiles = oldContract == null ? null : oldContract.getApplicationProvider().getAllApplicationProviderMetadataExtensions();
        Map<String, Metadata> newAppProfiles = newContract.getApplicationProvider().getAllApplicationProviderMetadataExtensions();
        Map<String, Metadata> oldIdpProfiles = oldContract == null ? null : oldContract.getIdentityProvider().getAllIdentityProviderMetadataExtensions();
        Map<String, Metadata> newIdpProfiles = newContract.getIdentityProvider().getAllIdentityProviderMetadataExtensions();

        return (!newAppProfiles.equals(oldAppProfiles) || !newIdpProfiles.equals(oldIdpProfiles));
    }

    private boolean setChangeDetailsForAuthAndProvisioningProfiles() {
        EnabledProfiles oldProfiles = (oldContract == null ? new EnabledProfiles(configuration) : oldContract.getEnabledProfiles());
        EnabledProfiles newProfiles = newContract.getEnabledProfiles();

        this.authenticationProfilesAdded = getAdditions(oldProfiles.getAuthenticationProfiles(), newProfiles.getAuthenticationProfiles());
        this.authenticationProfilesRemoved = getRemovals(oldProfiles.getAuthenticationProfiles(), newProfiles.getAuthenticationProfiles());
        this.provisioningProfilesAdded = getAdditions(oldProfiles.getProvisioningProfiles(), newProfiles.getProvisioningProfiles());
        this.provisioningProfilesRemoved = getRemovals(oldProfiles.getProvisioningProfiles(), newProfiles.getProvisioningProfiles());

        boolean hasProfileChange = (
                !authenticationProfilesAdded.isEmpty() ||
                !authenticationProfilesRemoved.isEmpty() ||
                !provisioningProfilesAdded.isEmpty() ||
                !provisioningProfilesRemoved.isEmpty()
        );

        return hasProfileChange;
    }

    private boolean setChangeDetailsForDesiredAttributes() {
        // Determine which attributes are being added/removed.
        SchemaGrammar schemaGrammar = newContract.getFastFedConfiguration().getPreferredSchemaGrammar();

        // Get the new attributes. Transform nulls into empty values.
        DesiredAttributes.ForSchemaGrammar newAttributes = newContract.getConsolidatedDesiredAttributes().getForSchemaGrammar(schemaGrammar);
        if (newAttributes == null) {
            newAttributes = new DesiredAttributes.ForSchemaGrammar(schemaGrammar); //Empty value
        }

        // Get the old attributes. Transform nulls into empty values.
        DesiredAttributes.ForSchemaGrammar oldAttributes;
        if (oldContract == null) {
            oldAttributes = new DesiredAttributes.ForSchemaGrammar(schemaGrammar); //Empty value
        } else {
            oldAttributes = oldContract.getConsolidatedDesiredAttributes().getForSchemaGrammar(schemaGrammar);
            if (oldAttributes == null) {
                oldAttributes = new DesiredAttributes.ForSchemaGrammar(schemaGrammar); //Empty value
            }
        }

        //Merge the Required/Optional categories into a single set
        Set<String> oldUserAttributes = new HashSet<>();
        oldUserAttributes.addAll(oldAttributes.getRequiredUserAttributes());
        oldUserAttributes.addAll(oldAttributes.getOptionalUserAttributes());

        Set<String> oldGroupAttributes = new HashSet<>();
        oldGroupAttributes.addAll(oldAttributes.getRequiredGroupAttributes());
        oldGroupAttributes.addAll(oldAttributes.getOptionalGroupAttributes());

        Set<String> newUserAttributes = new HashSet<>();
        newUserAttributes.addAll(newAttributes.getRequiredUserAttributes());
        newUserAttributes.addAll(newAttributes.getOptionalUserAttributes());

        Set<String> newGroupAttributes = new HashSet<>();
        newGroupAttributes.addAll(newAttributes.getRequiredGroupAttributes());
        newGroupAttributes.addAll(newAttributes.getOptionalGroupAttributes());

        // Determine which attributes are being added/removed
        this.userAttributesAdded = getAdditions(oldUserAttributes, newUserAttributes);
        this.userAttributesRemoved = getRemovals(oldUserAttributes, newUserAttributes);
        this.groupAttributesAdded = getAdditions(oldGroupAttributes, newGroupAttributes);
        this.groupAttributesRemoved = getRemovals(oldGroupAttributes, newGroupAttributes);

        // Determine if group provisioning is being activated/deactivated
        boolean oldGroupAttributesExist = oldGroupAttributes != null && !oldGroupAttributes.isEmpty();
        boolean newGroupAttributesExist = newGroupAttributes != null && !newGroupAttributes.isEmpty();
        this.isActivatingGroupProvisioning = !oldGroupAttributesExist && newGroupAttributesExist;
        this.isDeactivatingGroupProvisioning = oldGroupAttributesExist && !newGroupAttributesExist;

        // Determine if the change contains any attribute changes
        boolean hasAttributeChange = (
                !userAttributesAdded.isEmpty() ||
                !userAttributesRemoved.isEmpty() ||
                !groupAttributesAdded.isEmpty() ||
                !groupAttributesRemoved.isEmpty()
        );

        return hasAttributeChange;
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
