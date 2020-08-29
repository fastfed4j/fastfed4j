package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.ProviderContactInformation;
import org.fastfed4j.test.evaluator.Operation;

public class ProviderContactInformationEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation, ProviderContactInformation specimen1, ProviderContactInformation specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "organization");
        performOperation(operation, specimen1, specimen2, "phone");
        performOperation(operation, specimen1, specimen2, "email");
    }
}
