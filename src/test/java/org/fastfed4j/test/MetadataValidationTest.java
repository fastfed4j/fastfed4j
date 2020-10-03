package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.profile.ProfileRegistry;
import org.fastfed4j.test.data.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;


/**
 * These tests exercise the scenarios that generate InvalidMetadataException. This includes:
 * <ul>
 *     <li>Malformed JSON</li>
 *     <li>Missing attributes</li>
 *     <li>Invalid values for attributes</li>
 * </ul>
 */
public class MetadataValidationTest {

    private static final FastFedConfiguration config = FastFedConfiguration.DEFAULT;
    private static final FastFedConfiguration configWithNoProfileExtensions = getConfigWithNoProfileExtensions();
    private static final EnabledProfiles emptyProfiles = new EnabledProfiles(config);

    @Test
    public void testMissingJson() {
        String json = "";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 1);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 1);
        assertErrorCount(new RegistrationRequest(config),                 json, 1);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 1);
        assertErrorCount(new Contract(config),                            json, 1);
        assertErrorCount(new ContractProposal(config),                    json, 1);
    }

    @Test
    public void testMalformedJson() {
        String json = "{malformed";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 1);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 1);
        assertErrorCount(new RegistrationRequest(config),                 json, 1);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 1);
        assertErrorCount(new Contract(config),                            json, 1);
        assertErrorCount(new ContractProposal(config),                    json, 1);
    }

    @Test
    public void testEmptyJson() {
        String json = "{}";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 7);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 6);
        assertErrorCount(new RegistrationRequest(config),                 json, 3);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 0);
        assertErrorCount(new Contract(config),                            json, 4);
        assertErrorCount(new ContractProposal(config),                    json, 2);
    }

    @Test
    public void testInvalidTypesInJson() {
        String json;
        json = IdentityProviderJson.INVALID_TYPES;
        assertErrorCount(new IdentityProviderMetadata(config),            json, 11);

        json = ApplicationProviderJson.INVALID_TYPES;
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 11);

        json = RegistrationRequestJson.INVALID_TYPES;
        assertErrorCount(new RegistrationRequest(config),                 json, 10);

        json = RegistrationResponseJson.INVALID_TYPES;
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 3);

        json = ContractJson.INVALID_TYPES;
        assertErrorCount(new Contract(config),                            json, 38);

        json = ContractProposalJson.INVALID_TYPES;
        assertErrorCount(new ContractProposal(config),                    json, 40);
    }

    @Test
    public void testMissingProfileExtensionsInApplicationMetadata() {
        String json = ApplicationProviderJson.MISSING_PROFILE_EXTENSIONS;

        // First, use the default FastFedConfiguration which has awareness of all Profiles implemented by this library.
        // Tests should fail on both the SAML and SCIM profiles.
        ApplicationProviderMetadata appMetadata = new ApplicationProviderMetadata(config);
        assertErrorCount(appMetadata, json, 2);

        // Next, test with a FastFedConfiguration that has no Profiles in the registry.
        // Because unrecognized profiles are ignored when deserializing the JSON, this should result
        // in no errors because the extensions are completely ignored.
        ApplicationProviderMetadata appMetadataWithEmptyProfileRegistry = new ApplicationProviderMetadata(configWithNoProfileExtensions);
        assertErrorCount(appMetadataWithEmptyProfileRegistry, json, 0);
    }

    @Test
    public void testMissingProfileExtensionsInRegistrationRequest() {
        String json = RegistrationRequestJson.MISSING_PROFILE_EXTENSIONS;

        // First, use the default FastFedConfiguration which has awareness of all Profiles implemented by this library.
        // Tests should fail on both the SAML and SCIM profiles.
        RegistrationRequest registrationRequest = new RegistrationRequest(config);
        assertErrorCount(registrationRequest, json, 2);

        // Next, test with a FastFedConfiguration that has no Profiles in the registry.
        // Because unrecognized profiles are ignored when deserializing the JSON, this should result
        // in no errors because the extensions are completely ignored.
        RegistrationRequest registrationRequestWithEmptyProfileRegistry = new RegistrationRequest(configWithNoProfileExtensions);
        assertErrorCount(registrationRequestWithEmptyProfileRegistry, json, 0);
    }

    @Test
    public void testMissingProfileExtensionsInRegistrationResponse() {
        String json = RegistrationResponseJson.MISSING_PROFILE_EXTENSIONS;

        // First, use the default FastFedConfiguration which has awareness of all Profiles implemented by this library.
        // Tests should fail on both the SAML and SCIM profiles.
        RegistrationResponse registrationResponse = new RegistrationResponse(config, TestUtils.getAllEnabledProfiles());
        assertErrorCount(registrationResponse, json, 2);

        // Next, test with a FastFedConfiguration that has no Profiles in the registry.
        // Because unrecognized profiles are ignored when deserializing the JSON, this should result
        // in no errors because the extensions are completely ignored.
        RegistrationResponse registrationResponseWithEmptyProfileRegistry =
                new RegistrationResponse(configWithNoProfileExtensions, new EnabledProfiles(configWithNoProfileExtensions));
        assertErrorCount(registrationResponseWithEmptyProfileRegistry, json, 0);
    }

    @Test
    public void testUnsupportedLicense() {
        // Construct an instance of FastFedConfiguration with supported licenses that differ from the JSON
        FastFedConfiguration.Builder configBuilder = new FastFedConfiguration.Builder();
        configBuilder.setSupportedLicenses( Set.of("https://mock-license.example.com/"));
        FastFedConfiguration localConfig = configBuilder.build();

        // Hydrate from JSON and ensure that validation fails
        try {
            ApplicationProviderMetadata.fromJson(localConfig, ApplicationProviderJson.FULLY_POPULATED);
        }
        catch (InvalidMetadataException ex) {
            // Success
            return;
        }
        throw new AssertionError("Expected IncompatibleProvidersException for unsupported license");
    }

    @Test
    public void testIllegalGroupAttributesInEnterpriseSAMLApplicationMetadata() {
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, ApplicationProviderJson.FULLY_POPULATED);
        DesiredAttributes.ForSchemaGrammar attributes =
                appMetadata.getEnterpriseSamlExtension().getDesiredAttributes().getForSchemaGrammar(config.getPreferredSchemaGrammar());

        // Test RequiredGroupAttributes
        attributes.setRequiredGroupAttributes(Set.of("displayName"));
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());

        // Reset
        errorAccumulator.clear();
        attributes.setRequiredGroupAttributes(new HashSet<>());

        // Test OptionalGroupAttributes
        attributes.setOptionalGroupAttributes(Set.of("members"));
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());

        // Reset again and ensure validation passes
        errorAccumulator.clear();
        attributes.setOptionalGroupAttributes(new HashSet<>());
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(0, errorAccumulator.getErrors().size());
    }

    @Test
    public void testInvalidSamlSubject() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Inject an illegal value of SAML Subject
        UserAttribute illegalAttribute = new UserAttribute(config, JsonMember.SAML_SUBJECT);
        illegalAttribute.set("displayName");
        appMetadata.getEnterpriseSamlExtension().setSamlSubject(illegalAttribute);

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());
    }

    @Test
    public void testInvalidSamlAttributes() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Inject an illegal value of SAML Attribute
        Set<String> illegalValues = Set.of("illegalOne", "illegalTwo");
        appMetadata.getEnterpriseSamlExtension().getDesiredAttributes().getRequiredUserAttributes().addAll(illegalValues);

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(2, errorAccumulator.getErrors().size());
    }

    @Test
    public void testInvalidScimUserAttributes() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Remove all the required values from the SCIM User Attribute
        Set<String> requiredUserAttributes = appMetadata.getEnterpriseScimExtension().getDesiredAttributes().getRequiredUserAttributes();
        requiredUserAttributes.remove("externalId");
        requiredUserAttributes.remove("userName");
        requiredUserAttributes.remove("active");

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());
    }

    @Test
    public void testInvalidScimGroupAttributes() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Remove all the required values from the SCIM Group Attribute.
        // This is a little bit tricky. If we simply removed all the group attributes, this implies group provisioning
        // is disabled and no validation occurs. Instead, we move them into the optional bucket.
        Set<String> requiredGroupAttributes = appMetadata.getEnterpriseScimExtension().getDesiredAttributes().getRequiredGroupAttributes();
        Set<String> optionalGroupAttributes = appMetadata.getEnterpriseScimExtension().getDesiredAttributes().getOptionalGroupAttributes();
        requiredGroupAttributes.clear();
        optionalGroupAttributes.addAll(Set.of("externalId", "displayName", "members"));

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());

        // Shift the attributes back and ensure validation succeeds
        requiredGroupAttributes.addAll(Set.of("externalId", "displayName"));
        optionalGroupAttributes.addAll(Set.of("members"));
        errorAccumulator.clear();

        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(0, errorAccumulator.getErrors().size());

        // Remove the "members" field from the Optional category and ensure validation fails.
        optionalGroupAttributes.clear();
        errorAccumulator.clear();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());


    }

    @Test
    public void testMissingSchemaGrammarInSamlDesiredAttributes() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Remove the SCIM 2.0 attributes from the Enterprise SAML DesiredAttributes
        appMetadata.getEnterpriseSamlExtension().getDesiredAttributes().remove(SchemaGrammar.SCIM);

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());
    }

    @Test
    public void testMissingSchemaGrammarInScimDesiredAttributes() {
        // Create an instance of Application Provider Metadata
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata appMetadata = ApplicationProviderMetadata.fromJson(config, json);

        // Remove the SCIM 2.0 attributes from the Enterprise SCIM DesiredAttributes
        appMetadata.getEnterpriseScimExtension().getDesiredAttributes().remove(SchemaGrammar.SCIM);

        // Ensure validation fails
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        appMetadata.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());
    }

    @Test
    public void testScimMaxGroupMembershipChangesOutOfBounds() {
        assertMaxGroupMembershipChangesOutOfBounds(FastFedConfiguration.SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES - 1);
        assertMaxGroupMembershipChangesOutOfBounds(FastFedConfiguration.SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES + 1);
    }

    private void assertMaxGroupMembershipChangesOutOfBounds(int maxGroupMembershipChanges) {
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        ApplicationProviderMetadata app = ApplicationProviderMetadata.fromJson(config, ApplicationProviderJson.FULLY_POPULATED);
        // Pre-test check: confirm validation passes
        app.validate(errorAccumulator);
        // Modify the max group membership changes and then ensure validation fails
        app.getEnterpriseScimExtension().setMaxGroupMembershipChanges(maxGroupMembershipChanges);
        app.validate(errorAccumulator);
        Assert.assertEquals(1, errorAccumulator.getErrors().size());
    }

    /**
     * Helper method to hydrate a Metadata object from JSON and assert the number of validation errors
     * matches expections.
     */
    private void assertErrorCount(Metadata metadata, String json, int expectedErrorCount) {
        try {
            metadata.hydrateAndValidate(json);
        }
        catch (InvalidMetadataException ex) {
            int actualErrorCount = ex.getErrorAccumulator().getErrors().size();
            if (actualErrorCount != expectedErrorCount) {
                StringBuilder builder = new StringBuilder();
                builder.append("Unexpected number of validation errors for ").append(metadata.getClass().getName());
                builder.append(" (expected ").append(expectedErrorCount).append(", ");
                builder.append("received ").append(actualErrorCount).append(")\n");
                ex.getErrorAccumulator().getErrors().forEach(error -> builder.append("  ").append(error).append("\n"));
                throw new AssertionError(builder.toString());
            }
        }
    }

    /**
     * Helper method to construct an instance of FastFedConfiguration with all the profile extensions removed
     * from the registry. Used in tests to verify unrecognized extensions are successfully ignored.
     */
    private static FastFedConfiguration getConfigWithNoProfileExtensions() {
        FastFedConfiguration.Builder builder = new FastFedConfiguration.Builder();
        builder.setProfileRegistry(new ProfileRegistry());
        return builder.build();
    }
}
