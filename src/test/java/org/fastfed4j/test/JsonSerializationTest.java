package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.json.JsonParser;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.data.*;
import org.fastfed4j.test.evaluator.contract.ContractEvaluator;
import org.fastfed4j.test.evaluator.contract.ContractProposalEvaluator;
import org.fastfed4j.test.evaluator.metadata.ApplicationProviderMetadataEvaluator;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.IdentityProviderMetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationRequestEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationResponseEvaluator;
import org.json.simple.JSONObject;
import org.junit.*;


/**
 * The following tests evaluate each metadata object for the following actions:
 * <ol>
 *     <li>Serialization - converting an existing object into JSON</li>
 *     <li>Deserialization - hydrating a new object from JSON</li>
 * </ol>
 * It accomplishes these actions through two kinds of tests. First, deserialization is tested by hydrating
 * an object from a JSON string that is 100% populated in all values and ensuring that none of the values are missing
 * in the hydrated object. Second, serialization is tested by hydrating an object from JSON, serializing it back out
 * to JSON, then rehydrating yet again. At the end, the two objects are compared to ensure they remain equal after
 * the serialization/deserialization cycle, ensuring no data was lost or corrupted.
 */
public class JsonSerializationTest {

    private static final FastFedConfiguration config = FastFedConfiguration.DEFAULT;
    private static final IdentityProviderMetadataEvaluator identityProviderMetadataEvaluator = new IdentityProviderMetadataEvaluator();
    private static final ApplicationProviderMetadataEvaluator applicationProviderMetadataEvaluator = new ApplicationProviderMetadataEvaluator();
    private static final RegistrationRequestEvaluator registrationRequestEvaluator = new RegistrationRequestEvaluator();
    private static final RegistrationResponseEvaluator tegistrationResponseEvaluator = new RegistrationResponseEvaluator();
    private static final ContractEvaluator contractEvaluator = new ContractEvaluator();
    private static final ContractProposalEvaluator contractProposalEvaluator = new ContractProposalEvaluator();

    @Test
    public void testIdentityProviderMetadataDeserialization() {
        String json = IdentityProviderJson.FULLY_POPULATED;
        IdentityProviderMetadata metadata = IdentityProviderMetadata.fromJson(config, json);
        identityProviderMetadataEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testIdentityProviderMetadataSerialization() {
        for (String json : IdentityProviderJson.ALL_VALID_VARIATIONS) {
            IdentityProviderMetadata original = IdentityProviderMetadata.fromJson(config, json);
            IdentityProviderMetadata rehydrated = IdentityProviderMetadata.fromJson(config, original.toJson().toString());
            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testApplicationProviderMetadataDeserialization() {
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata metadata = ApplicationProviderMetadata.fromJson(config, json);
        applicationProviderMetadataEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testApplicationProviderMetadataSerialization() {
        for (String json : ApplicationProviderJson.ALL_VALID_VARIATIONS) {
            ApplicationProviderMetadata original = ApplicationProviderMetadata.fromJson(config, json);
            ApplicationProviderMetadata rehydrated = ApplicationProviderMetadata.fromJson(config, original.toJson().toString());
            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testRegistrationRequestDeserialization() {
        String json = RegistrationRequestJson.FULLY_POPULATED;
        RegistrationRequest metadata = RegistrationRequest.fromJson(config, json);
        registrationRequestEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testRegistrationRequestSerialization() {
        for (String json : RegistrationRequestJson.ALL_VALID_VARIATIONS) {
            RegistrationRequest original = RegistrationRequest.fromJson(config, json);
            RegistrationRequest rehydrated = RegistrationRequest.fromJson(config, original.toJson().toString());
            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testRegistrationResponseDeserialization() {
        String json = RegistrationResponseJson.FULLY_POPULATED;
        RegistrationResponse metadata = RegistrationResponse.fromJson(config, json, TestUtils.getAllEnabledProfiles());
        registrationRequestEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testRegistrationResponseSerialization() {
        for (String json : RegistrationRequestJson.ALL_VALID_VARIATIONS) {
            // The following code manually executes the JSON hydration to side-step validation
            // that would otherwise occur when invoking the more common fromJson() method.
            // The registration response is unique in that validation depends on knowing
            // the profiles being enabled, and those can vary based on previous actions in the FastFed Handshake.
            // It's all complicated, and we're not actually testing the validation here, so side-stepping
            // the complexity.
            RegistrationResponse original = new RegistrationResponse(config, new EnabledProfiles(config));
            original.hydrateFromJson( JsonParser.parse(json, new ErrorAccumulator()));

            RegistrationResponse rehydrated = new RegistrationResponse(config, new EnabledProfiles(config));
            rehydrated.hydrateFromJson( JsonParser.parse(original.toJson().toString(), new ErrorAccumulator()));

            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testContractDeserialization() {
        String json = ContractJson.FULLY_POPULATED;
        Contract metadata = Contract.fromJson(config, json);
        contractEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testContractSerialization() {
        for (String json : ContractJson.ALL_VALID_VARIATIONS) {
            Contract original = Contract.fromJson(config, json);
            Contract rehydrated = Contract.fromJson(config, original.toJson().toString());
            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testContractProposalDeserialization() {
        String json = ContractProposalJson.FULLY_POPULATED;
        ContractProposal metadata = ContractProposal.fromJson(config, json);
        contractProposalEvaluator.evaluate(Operation.AssertNotEmpty, metadata);
    }

    @Test
    public void testContractProposalSerialization() {
        for (String json : ContractProposalJson.ALL_VALID_VARIATIONS) {
            ContractProposal original = ContractProposal.fromJson(config, json);
            ContractProposal rehydrated = ContractProposal.fromJson(config, original.toJson().toString());
            Assert.assertEquals(original, rehydrated);
        }
    }

    @Test
    public void testUnknownProfilesInApplicationMetadata() {
        String json = ApplicationProviderJson.UNKNOWN_PROFILES;
        // Ensure this doesn't throw an InvalidMetadataException
        ApplicationProviderMetadata metadata = ApplicationProviderMetadata.fromJson(config, json);
    }

    @Test
    public void testEnterpriseScimDefaultValuesInApplicationMetadataExtension() {
        // Hydrates from a JSON object containing no values for CanSupportNestedGroups and
        // MaxGroupMembershipChanges. Ensures that default values are injected during hydration.
        String json = ApplicationProviderJson.ONLY_ENTERPRISE_SCIM_WITH_MINIMAL_SCIM_SETTINGS;
        ApplicationProviderMetadata metadata = ApplicationProviderMetadata.fromJson(config, json);
        Assert.assertEquals(
                (int)FastFedConfiguration.SCIM_DEFAULT_VALUE_OF_MAX_GROUP_MEMBERSHIP_CHANGES,
                (int)metadata.getEnterpriseScimExtension().getMaxGroupMembershipChanges()
        );
        Assert.assertEquals(
                FastFedConfiguration.SCIM_DEFAULT_VALUE_OF_NESTED_GROUP_SUPPORT,
                metadata.getEnterpriseScimExtension().getCanSupportNestedGroups()
        );
    }

    @Test
    public void testEnterpriseScimConfigValuesInApplicationMetadataExtension() {
        // Customizes the values of CanSupportNestedGroups and MaxGroupMembershipChanges, then ensures
        // these values get included when serializing to JSON.
        FastFedConfiguration.Builder builder = new FastFedConfiguration.Builder();
        builder.setCanSupportNestedGroupsInScim(true);
        builder.setMaxGroupMembershipChangesInScim(123);
        FastFedConfiguration localConfig = builder.build();

        // For convenience, construct a new ApplicationProviderMetadata object from JSON,
        // then overwrite the EnterpriseSCIM.ApplicationProviderMetadataExtension with a new value
        // to exercise this test case. (Otherwise, the values will just be whatever came from the original JSON.)
        String json = ApplicationProviderJson.FULLY_POPULATED;
        ApplicationProviderMetadata metadata = ApplicationProviderMetadata.fromJson(localConfig, json);
        EnterpriseSCIM.ApplicationProviderMetadataExtension scimExtension = new EnterpriseSCIM.ApplicationProviderMetadataExtension(localConfig);
        scimExtension.setDesiredAttributes(metadata.getEnterpriseScimExtension().getDesiredAttributes());
        metadata.addMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn(), scimExtension);

        // At this point, we have an ApplicationMetadata object containing an EnterpriseSCIM extension with no values
        // explicitly set for CanSupportNestedGroups and MaxGroupMembershipChanges.
        // Convert it to JSON and back, and ensure what we get is the settings from our FastFedConfiguration.
        ApplicationProviderMetadata rehydrated = ApplicationProviderMetadata.fromJson(localConfig, metadata.toJson().toString());
        Assert.assertEquals(true, metadata.getEnterpriseScimExtension().getCanSupportNestedGroups());
        Assert.assertEquals(123, (int)metadata.getEnterpriseScimExtension().getMaxGroupMembershipChanges());
    }

    @Test
    public void testNormalizationOfScimAttributes() {
        String json =
                "     {" +
                "       \"desired_attributes\": {\n" +
                "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
                "           \"required_user_attributes\": [\n" +
                "             \"  externalId  \",\n" +
                "             \"urn:ietf:params:scim:schemas:core:2.0:User:userName\"\n" +
                "           ],\n" +
                "           \"required_group_attributes\": [\n" +
                "             \" displayName \",\n" +
                "             \"urn:ietf:params:scim:schemas:core:2.0:User:externalId\"\n" +
                "           ]\n" +
                "         }\n" +
                "       }\n" +
                "     }";

        DesiredAttributes desiredAttributes = new DesiredAttributes(config);
        desiredAttributes.hydrateAndValidate(json);
        Assert.assertTrue(desiredAttributes.getRequiredUserAttributes().contains("externalId"));
        Assert.assertTrue(desiredAttributes.getRequiredUserAttributes().contains("userName"));
        Assert.assertEquals(2, desiredAttributes.getRequiredUserAttributes().size());
        Assert.assertTrue(desiredAttributes.getRequiredGroupAttributes().contains("displayName"));
        Assert.assertTrue(desiredAttributes.getRequiredGroupAttributes().contains("externalId"));
        Assert.assertEquals(2, desiredAttributes.getRequiredUserAttributes().size());
    }
}
