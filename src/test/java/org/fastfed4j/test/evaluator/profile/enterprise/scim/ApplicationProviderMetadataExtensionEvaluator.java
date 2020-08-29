package org.fastfed4j.test.evaluator.profile.enterprise.scim;

import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.DesiredAttributesEvaluator;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;

public class ApplicationProviderMetadataExtensionEvaluator extends MetadataEvaluator {

    private static final DesiredAttributesEvaluator desiredAttributesEvaluator = new DesiredAttributesEvaluator();

    public void evaluate(Operation operation,
                         EnterpriseSCIM.ApplicationProviderMetadataExtension specimen1,
                         EnterpriseSCIM.ApplicationProviderMetadataExtension specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "canSupportNestedGroups");
        performOperation(operation, specimen1, specimen2, "maxGroupMembershipChanges");

        desiredAttributesEvaluator.evaluate(
                operation,
                specimen1.getDesiredAttributes(),
                specimen2 == null ? null : specimen2.getDesiredAttributes()
        );
    }
}
