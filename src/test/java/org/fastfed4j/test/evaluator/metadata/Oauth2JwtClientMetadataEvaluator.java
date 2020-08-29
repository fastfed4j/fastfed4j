package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.Oauth2JwtClientMetadata;
import org.fastfed4j.test.evaluator.Operation;

public class Oauth2JwtClientMetadataEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation,
                         Oauth2JwtClientMetadata specimen1,
                         Oauth2JwtClientMetadata specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "jwksUri");
    }
}
