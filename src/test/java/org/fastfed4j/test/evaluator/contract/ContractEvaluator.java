package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;

public class ContractEvaluator extends MetadataEvaluator {

    private static final IdentityProviderEvaluator identityProviderEvaluator = new IdentityProviderEvaluator();
    private static final ApplicationProviderEvaluator applicationProviderEvaluator = new ApplicationProviderEvaluator();
    private static final EnabledProfilesEvaluator enabledProfilesEvaluator = new EnabledProfilesEvaluator();

    public void evaluate(Operation operation, Contract specimen1, Contract specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "signingAlgorithms");

        identityProviderEvaluator.evaluate(
                operation,
                specimen1.getIdentityProvider(),
                specimen2 == null ? null : specimen2.getIdentityProvider()
        );

        applicationProviderEvaluator.evaluate(
                operation,
                specimen1.getApplicationProvider(),
                specimen2 == null ? null : specimen2.getApplicationProvider()
        );

        enabledProfilesEvaluator.evaluate(
                operation,
                specimen1.getEnabledProfiles(),
                specimen2 == null ? null : specimen2.getEnabledProfiles()
        );
    }
}
