package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;

public class EnabledProfilesEvaluator extends MetadataEvaluator {
    public void evaluate(Operation operation, EnabledProfiles specimen1, EnabledProfiles specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "authenticationProfiles");
        performOperation(operation, specimen1, specimen2, "provisioningProfiles");
    }
}
