package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonParser;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.test.data.*;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.contract.ContractEvaluator;
import org.fastfed4j.test.evaluator.contract.ContractProposalEvaluator;
import org.fastfed4j.test.evaluator.metadata.ApplicationProviderMetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationRequestEvaluator;
import org.fastfed4j.test.evaluator.metadata.RegistrationResponseEvaluator;
import org.junit.Test;

/**
 * Tests the copy constructor on all the Metadata implementations.
 */
public class MetadataCopyConstructorTest {

    FastFedConfiguration config = FastFedConfiguration.DEFAULT;

    @Test
    public void testIdentityProviderMetadata() {
        ApplicationProviderMetadataEvaluator evaluator = new ApplicationProviderMetadataEvaluator();
        for (String json : IdentityProviderJson.ALL_VALID_VARIATIONS) {
            IdentityProviderMetadata original = IdentityProviderMetadata.fromJson(config, json);
            IdentityProviderMetadata copy = new IdentityProviderMetadata(original);
            evaluate(evaluator, original, copy);
        }
    }

    @Test
    public void testApplicationProviderMetadata() {
        ApplicationProviderMetadataEvaluator evaluator = new ApplicationProviderMetadataEvaluator();
        for (String json : ApplicationProviderJson.ALL_VALID_VARIATIONS) {
            ApplicationProviderMetadata original = ApplicationProviderMetadata.fromJson(config, json);
            ApplicationProviderMetadata copy = new ApplicationProviderMetadata(original);
            evaluate(evaluator, original, copy);
        }
    }

    @Test
    public void testRegistrationRequest() {
        RegistrationRequestEvaluator evaluator = new RegistrationRequestEvaluator();
        for (String json : RegistrationRequestJson.ALL_VALID_VARIATIONS) {
            RegistrationRequest original = RegistrationRequest.fromJson(config, json);
            RegistrationRequest copy = new RegistrationRequest(original);
            evaluate(evaluator, original, copy);
        }
    }

    @Test
    public void testRegistrationResponse() {
        RegistrationResponseEvaluator evaluator = new RegistrationResponseEvaluator();
        for (String json : RegistrationResponseJson.ALL_VALID_VARIATIONS) {
            // The following code manually executes the JSON hydration to side-step validation
            // that would otherwise occur when invoking the more common fromJson() method.
            // The registration response is unique in that validation depends on knowing
            // the profiles being enabled, and those can vary based on previous actions in the FastFed Handshake.
            // It's all complicated, and we're not actually testing the validation here, so side-stepping
            // the complexity.
            RegistrationResponse original = new RegistrationResponse(config, new EnabledProfiles(config));
            original.hydrateFromJson( JsonParser.parse(json, new ErrorAccumulator()));
            RegistrationResponse copy = new RegistrationResponse(original);
            evaluate(evaluator, original, copy);
        }
    }

    @Test
    public void testContract() {
        ContractEvaluator evaluator = new ContractEvaluator();
        for (String json : ContractJson.ALL_VALID_VARIATIONS) {
            Contract original = Contract.fromJson(config, json);
            Contract copy = new Contract(original);
            evaluate(evaluator, original, copy);
        }
    }

    @Test
    public void testContractProposal() {
        ContractProposalEvaluator evaluator = new ContractProposalEvaluator();
        for (String json : ContractProposalJson.ALL_VALID_VARIATIONS) {
            ContractProposal original = ContractProposal.fromJson(config, json);
            ContractProposal copy = new ContractProposal(original);
            evaluate(evaluator, original, copy);
        }
    }

    private void evaluate(MetadataEvaluator evaluator, Metadata original, Metadata copy) {
        evaluator.evaluate(Operation.AssertEquals, original, copy);
        evaluator.evaluate(Operation.ToggleAndAssertNotEquals, original, copy);
    }
}
