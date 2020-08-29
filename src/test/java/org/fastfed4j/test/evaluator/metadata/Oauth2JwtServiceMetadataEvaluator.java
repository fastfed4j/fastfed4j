package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.Oauth2JwtServiceMetadata;
import org.fastfed4j.test.evaluator.Operation;

public class Oauth2JwtServiceMetadataEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation,
                         Oauth2JwtServiceMetadata specimen1,
                         Oauth2JwtServiceMetadata specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation,specimen1, specimen2, "oauthTokenEndpoint");
        performOperation(operation,specimen1, specimen2, "oauthScope");
    }
}

