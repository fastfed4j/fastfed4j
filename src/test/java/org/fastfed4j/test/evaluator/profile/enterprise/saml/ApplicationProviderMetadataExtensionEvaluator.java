package org.fastfed4j.test.evaluator.profile.enterprise.saml;

import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.DesiredAttributesEvaluator;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;

public class ApplicationProviderMetadataExtensionEvaluator extends MetadataEvaluator {

    private static final DesiredAttributesEvaluator desiredAttributesEvaluator = new DesiredAttributesEvaluator();

    public void evaluate(Operation operation,
                         EnterpriseSAML.ApplicationProviderMetadataExtension specimen1,
                         EnterpriseSAML.ApplicationProviderMetadataExtension specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        desiredAttributesEvaluator.evaluate(
                operation,
                specimen1.getDesiredAttributes(),
                specimen2 == null ? null : specimen2.getDesiredAttributes()
        );
    }
}
