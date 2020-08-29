package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.test.evaluator.Operation;

public class IdentityProviderMetadataEvaluator extends CommonProviderMetadataEvaluator {

    public void evaluate(Operation operation, ApplicationProviderMetadata specimen1, ApplicationProviderMetadata specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "jwksUri");
        performOperation(operation, specimen1, specimen2, "handshakeStartUri");
    }

}
