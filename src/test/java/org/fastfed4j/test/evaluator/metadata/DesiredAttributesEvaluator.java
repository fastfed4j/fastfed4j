package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.test.evaluator.Operation;
import org.junit.Assert;

import java.util.Set;

public class DesiredAttributesEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation, DesiredAttributes specimen1, DesiredAttributes specimen2) {
        super.evaluate(operation, specimen1, specimen2);

        // The Desired Attributes are shaped slightly differently than most other metadata objects
        // because the contents are nestled under a schema grammar URN.
        // Hence, the visitation logic is customized.

        // First, check the collection of schema grammars for existence and equality.
        Set<SchemaGrammar> grammarList1 = specimen1.getAllSchemaGrammars();
        Set<SchemaGrammar> grammarList2 = specimen2 == null ? null : specimen2.getAllSchemaGrammars();
        switch (operation) {
            case AssertNotEmpty:
                Assert.assertNotNull("schema grammars are null", grammarList1);
                Assert.assertFalse("schema grammars is empty list", grammarList1.isEmpty());
                break;
            case AssertEquals:
                Assert.assertEquals("schema grammars are not equal", grammarList1, grammarList2);
                break;
        }

        // Then, for each schema grammar, execute the operation on the contents.
        for (SchemaGrammar grammar : grammarList1) {
            DesiredAttributes.ForSchemaGrammar desiredAttributes1 = specimen1.forSchemaGrammar(grammar);
            DesiredAttributes.ForSchemaGrammar desiredAttributes2 = specimen2 == null ? null : specimen2.forSchemaGrammar(grammar);
            performOperation(operation, desiredAttributes1, desiredAttributes2, "requiredUserAttributes");
            performOperation(operation, desiredAttributes1, desiredAttributes2, "optionalUserAttributes");
            performOperation(operation, desiredAttributes1, desiredAttributes2, "requiredGroupAttributes");
            performOperation(operation, desiredAttributes1, desiredAttributes2, "optionalGroupAttributes");
        }
    }
}
