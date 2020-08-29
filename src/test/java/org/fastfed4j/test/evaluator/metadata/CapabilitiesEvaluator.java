package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.test.evaluator.Operation;

public class CapabilitiesEvaluator extends MetadataEvaluator {
    public void evaluate(Operation operation, Capabilities specimen1, Capabilities specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "authenticationProfiles");
        performOperation(operation, specimen1, specimen2, "provisioningProfiles");
        performOperation(operation, specimen1, specimen2, "schemaGrammars");
        performOperation(operation, specimen1, specimen2, "signingAlgorithms");
    }
}
