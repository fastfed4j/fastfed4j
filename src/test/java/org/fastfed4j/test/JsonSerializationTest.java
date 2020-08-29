package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonParser;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.IdentityProviderMetadata;
import org.fastfed4j.core.metadata.RegistrationRequest;
import org.fastfed4j.core.metadata.RegistrationResponse;
import org.fastfed4j.test.data.*;
import org.fastfed4j.test.evaluator.contract.ContractEvaluator;
import org.fastfed4j.test.evaluator.contract.ContractProposalEvaluator;
import org.fastfed4j.test.evaluator.metadata.ApplicationProviderMetadataEvaluator;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.IdentityProviderMetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationRequestEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationResponseEvaluator;
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
}
