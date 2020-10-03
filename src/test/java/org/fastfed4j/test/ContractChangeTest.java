package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractChange;
import org.fastfed4j.core.contract.ContractChangeType;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.test.data.ContractJson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ContractChangeTest {

    private static final FastFedConfiguration config = FastFedConfiguration.DEFAULT;
    private static final int NUMBER_OF_AUTHENTICATION_PROFILES_IN_TEST_DATA = 1;
    private static final int NUMBER_OF_PROVISIONING_PROFILES_IN_TEST_DATA = 1;
    private static final int NUMBER_OF_USER_ATTRIBUTES_IN_TEST_DATA = 6;
    private static final int NUMBER_OF_GROUP_ATTRIBUTES_IN_TEST_DATA = 3;
    private static final int NUMBER_OF_USER_ATTRIBUTES_IN_SCIM_PROFILE = 6;
    private static final int NUMBER_OF_USER_ATTRIBUTES_IN_SAML_PROFILE = 4;
    private static final int NUMBER_OF_USER_ATTRIBUTES_UNIQUE_TO_SCIM_PROFILE = NUMBER_OF_USER_ATTRIBUTES_IN_SCIM_PROFILE -
                                                                                NUMBER_OF_USER_ATTRIBUTES_IN_SAML_PROFILE;

    @Test
    public void testNoChange() {
        for (String json : ContractJson.ALL_VALID_VARIATIONS) {
            Contract oldContract = Contract.fromJson(config, json);
            Contract newContract = Contract.fromJson(config, json);

            ContractChange change = new ContractChange(oldContract, newContract);
            Assert.assertEquals(ContractChangeType.None, change.getChangeType());
            Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
            Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
            Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
            Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
            Assert.assertEquals(0, change.getUserAttributesAdded().size());
            Assert.assertEquals(0, change.getUserAttributesRemoved().size());
            Assert.assertEquals(0, change.getGroupAttributesAdded().size());
            Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
            Assert.assertFalse(change.isActivatingGroupProvisioning());
            Assert.assertFalse(change.isDeactivatingGroupProvisioning());
        }
    }

    @Test
    public void testCreation() {
        Contract oldContract = null;
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.Create, change.getChangeType());
        Assert.assertEquals(NUMBER_OF_AUTHENTICATION_PROFILES_IN_TEST_DATA, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(NUMBER_OF_PROVISIONING_PROFILES_IN_TEST_DATA, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(NUMBER_OF_USER_ATTRIBUTES_IN_TEST_DATA, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(NUMBER_OF_GROUP_ATTRIBUTES_IN_TEST_DATA, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertTrue(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testMetadataChange() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Change a piece of metadata
        newContract.getApplicationProvider().getDisplaySettings().setLogoUri("htts://example.com/DUMMY.png");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.MetadataChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testAuthenticationProfileAdded() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Remove the prior authentication profiles
        oldContract.getEnabledProfiles().getAuthenticationProfiles().clear();

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(NUMBER_OF_AUTHENTICATION_PROFILES_IN_TEST_DATA, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testAuthenticationProfileRemoved() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Remove the current authentication profiles
        newContract.getEnabledProfiles().getAuthenticationProfiles().clear();

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(NUMBER_OF_AUTHENTICATION_PROFILES_IN_TEST_DATA, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testProvisioningProfileAdded() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Remove the prior provisioning profiles
        oldContract.getEnabledProfiles().getProvisioningProfiles().clear();

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(NUMBER_OF_PROVISIONING_PROFILES_IN_TEST_DATA, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(NUMBER_OF_USER_ATTRIBUTES_UNIQUE_TO_SCIM_PROFILE, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(NUMBER_OF_GROUP_ATTRIBUTES_IN_TEST_DATA, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertTrue(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testProvisioningProfileRemoved() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Remove the current provisioning profiles
        newContract.getEnabledProfiles().getProvisioningProfiles().clear();

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(NUMBER_OF_PROVISIONING_PROFILES_IN_TEST_DATA, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(NUMBER_OF_USER_ATTRIBUTES_UNIQUE_TO_SCIM_PROFILE, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(NUMBER_OF_GROUP_ATTRIBUTES_IN_TEST_DATA, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertTrue(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testAllProfilesRemoved() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        //Remove the current provisioning profiles
        newContract.getEnabledProfiles().getAuthenticationProfiles().clear();
        newContract.getEnabledProfiles().getProvisioningProfiles().clear();

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.Terminate, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(NUMBER_OF_AUTHENTICATION_PROFILES_IN_TEST_DATA, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(NUMBER_OF_PROVISIONING_PROFILES_IN_TEST_DATA, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(NUMBER_OF_USER_ATTRIBUTES_IN_TEST_DATA, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(NUMBER_OF_GROUP_ATTRIBUTES_IN_TEST_DATA, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertTrue(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeAddedToSamlExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set new values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("name.givenName", "name.familyName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("name.givenName", "name.familyName", "name.middleName")));

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(1, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeAddedToSamlExtensionButAlreadyExistsForScim() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "displayName")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "displayName")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "displayName")));

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeRemovedFromSamlExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set new values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("name.givenName", "name.familyName", "name.middleName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("name.givenName", "name.familyName")));

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(1, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeRemovedFromSamlExtensionButStillExistsForScim() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "displayName")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "displayName")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "displayName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testSamlSubjectChanged_addsNewAttribute() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("emails[primary eq true].value");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(1, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testSamlSubjectChanged_removesExistingAttribute() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("emails[primary eq true].value");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(1, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testSamlSubjectChanged_preservesExistingAttributes() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("emails[primary eq true].value");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeAddedToScimExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(1, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeAddedToScimExtensionButAlreadyExistsForSaml() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "emails[primary eq true].value")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "emails[primary eq true].value")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeRemovedFromScimExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(1, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUserAttributeRemovedFromScimExtensionButStillExistsForSaml() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active", "emails[primary eq true].value")));
        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "emails[primary eq true].value")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "emails[primary eq true].value")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testEnablingGroupProvisioningInScimExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName", "active")));

        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(
                        Set.of("externalId", "userName", "active"),
                        null,
                        Set.of("externalId", "displayName"),
                        Set.of("members")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(3, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertTrue(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testUpdatingGroupProvisioningInScimExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(
                        Set.of("externalId", "userName", "active"),
                        null,
                        Set.of("externalId", "displayName"),
                        null));

        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(
                        Set.of("externalId", "userName", "active"),
                        null,
                        Set.of("externalId", "displayName"),
                        Set.of("members")));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(1, change.getGroupAttributesAdded().size());
        Assert.assertEquals(0, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertFalse(change.isDeactivatingGroupProvisioning());
    }

    @Test
    public void testDisablingGroupProvisioningInScimExtension() {
        Contract oldContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);
        Contract newContract = Contract.fromJson(config, ContractJson.FULLY_POPULATED);

        // Set values for the SCIM Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(
                        Set.of("externalId", "userName", "active"),
                        null,
                        Set.of("externalId", "displayName"),
                        Set.of("members")));

        newContract.getApplicationProvider().getEnterpriseScimApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(
                        Set.of("externalId", "userName", "active"),
                        null,
                        null,
                        null));

        // Set values for the SAML Desired Attributes
        oldContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));
        newContract.getApplicationProvider().getEnterpriseSamlApplicationProviderMetadataExtension().setDesiredAttributes(
                generateDesiredAttributes(Set.of("externalId", "userName")));

        // Set values for the SAML Subject
        oldContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");
        newContract.getApplicationProvider().getEnterpriseSamlSubject().set("externalId");

        ContractChange change = new ContractChange(oldContract, newContract);
        Assert.assertEquals(ContractChangeType.ProfileChange, change.getChangeType());
        Assert.assertEquals(0, change.getAuthenticationProfilesAdded().size());
        Assert.assertEquals(0, change.getAuthenticationProfilesRemoved().size());
        Assert.assertEquals(0, change.getProvisioningProfilesAdded().size());
        Assert.assertEquals(0, change.getProvisioningProfilesRemoved().size());
        Assert.assertEquals(0, change.getUserAttributesAdded().size());
        Assert.assertEquals(0, change.getUserAttributesRemoved().size());
        Assert.assertEquals(0, change.getGroupAttributesAdded().size());
        Assert.assertEquals(3, change.getGroupAttributesRemoved().size());
        Assert.assertFalse(change.isActivatingGroupProvisioning());
        Assert.assertTrue(change.isDeactivatingGroupProvisioning());
    }

    private DesiredAttributes generateDesiredAttributes(Set<String> requiredUserAttributes) {
        return generateDesiredAttributes(requiredUserAttributes, null, null, null);
    }

    private DesiredAttributes generateDesiredAttributes(Set<String> requiredUserAttributes,
                                                        Set<String> optionalUserAttributes,
                                                        Set<String> requiredGroupAttributes,
                                                        Set<String> optionalGroupAttributes)
    {
        DesiredAttributes.ForSchemaGrammar forSchemaGrammar = new DesiredAttributes.ForSchemaGrammar(config.getPreferredSchemaGrammar());
        if (requiredUserAttributes != null)
            forSchemaGrammar.setRequiredUserAttributes(requiredUserAttributes);
        if (optionalUserAttributes != null)
            forSchemaGrammar.setOptionalUserAttributes(optionalUserAttributes);
        if (requiredGroupAttributes != null)
            forSchemaGrammar.setRequiredGroupAttributes(requiredGroupAttributes);
        if (optionalGroupAttributes != null)
            forSchemaGrammar.setOptionalGroupAttributes(optionalGroupAttributes);

        DesiredAttributes desiredAttributes = new DesiredAttributes(config);
        desiredAttributes.setForSchemaGrammar(forSchemaGrammar);
        return desiredAttributes;
    }
}
