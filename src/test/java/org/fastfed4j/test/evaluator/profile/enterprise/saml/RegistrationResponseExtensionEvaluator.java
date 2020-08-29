package org.fastfed4j.test.evaluator.profile.enterprise.saml;

import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;


public class RegistrationResponseExtensionEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation,
                         EnterpriseSAML.RegistrationResponseExtension specimen1,
                         EnterpriseSAML.RegistrationResponseExtension specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "samlMetadataUri");
    }

}
