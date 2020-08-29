package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.DisplaySettings;
import org.fastfed4j.test.evaluator.Operation;

public class DisplaySettingsEvaluator extends MetadataEvaluator {

    public void evaluate(Operation operation, DisplaySettings specimen1, DisplaySettings specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "displayName");
        performOperation(operation, specimen1, specimen2, "logoUri");
        performOperation(operation, specimen1, specimen2, "iconUri");
        performOperation(operation, specimen1, specimen2, "license");
    }
}

