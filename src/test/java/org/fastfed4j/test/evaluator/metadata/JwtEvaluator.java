package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.json.Jwt;
import org.fastfed4j.test.evaluator.Operation;

public class JwtEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation, Jwt specimen1, Jwt specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "issuer");
        performOperation(operation, specimen1, specimen2, "audience");
        performOperation(operation, specimen1, specimen2, "expiration");
    }

}
