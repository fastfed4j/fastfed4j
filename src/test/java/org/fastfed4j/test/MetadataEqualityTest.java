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
 * Tests the equals() method on all the Metadata implementations.
 */
public class MetadataEqualityTest {

    FastFedConfiguration config = FastFedConfiguration.DEFAULT;

    @Test
    public void testIdentityProviderMetadata() {
        ApplicationProviderMetadataEvaluator evaluator = new ApplicationProviderMetadataEvaluator();
        for (String json : IdentityProviderJson.ALL_VALID_VARIATIONS) {
            IdentityProviderMetadata specimen1 = IdentityProviderMetadata.fromJson(config, json);
            IdentityProviderMetadata specimen2 = IdentityProviderMetadata.fromJson(config, json);
            evaluate(evaluator, specimen1, specimen2);
        }
    }

    @Test
    public void testApplicationProviderMetadata() {
        ApplicationProviderMetadataEvaluator evaluator = new ApplicationProviderMetadataEvaluator();
        for (String json : ApplicationProviderJson.ALL_VALID_VARIATIONS) {
            ApplicationProviderMetadata specimen1 = ApplicationProviderMetadata.fromJson(config, json);
            ApplicationProviderMetadata specimen2 = ApplicationProviderMetadata.fromJson(config, json);
            evaluate(evaluator, specimen1, specimen2);
        }
    }

    @Test
    public void testRegistrationRequest() {
        RegistrationRequestEvaluator evaluator = new RegistrationRequestEvaluator();
        for (String json : RegistrationRequestJson.ALL_VALID_VARIATIONS) {
            RegistrationRequest specimen1 = RegistrationRequest.fromJson(config, json);
            RegistrationRequest specimen2 = RegistrationRequest.fromJson(config, json);
            evaluate(evaluator, specimen1, specimen2);
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
            RegistrationResponse specimen1 = new RegistrationResponse(config, new EnabledProfiles(config));
            specimen1.hydrateFromJson(JsonParser.parse(json, new ErrorAccumulator()));

            RegistrationResponse specimen2 = new RegistrationResponse(config, new EnabledProfiles(config));
            specimen2.hydrateFromJson(JsonParser.parse(json, new ErrorAccumulator()));

            evaluate(evaluator, specimen1, specimen2);
        }
    }

    @Test
    public void testContract() {
        ContractEvaluator evaluator = new ContractEvaluator();
        for (String json : ContractJson.ALL_VALID_VARIATIONS) {
            Contract specimen1 = Contract.fromJson(config, json);
            Contract specimen2 = Contract.fromJson(config, json);
            evaluate(evaluator, specimen1, specimen2);
        }
    }

    @Test
    public void testContractProposal() {
        ContractProposalEvaluator evaluator = new ContractProposalEvaluator();
        for (String json : ContractProposalJson.ALL_VALID_VARIATIONS) {
            ContractProposal specimen1 = ContractProposal.fromJson(config, json);
            ContractProposal specimen2 = ContractProposal.fromJson(config, json);
            evaluate(evaluator, specimen1, specimen2);
        }
    }

    private void evaluate(MetadataEvaluator evaluator, Metadata original, Metadata copy) {
        evaluator.evaluate(Operation.AssertEquals, original, copy);
        evaluator.evaluate(Operation.ToggleAndAssertNotEquals, original, copy);
    }
}