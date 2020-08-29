package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.contract.Provider;
import org.fastfed4j.core.metadata.DisplaySettings;
import org.fastfed4j.core.metadata.ProviderContactInformation;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.DisplaySettingsEvaluator;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.ProviderContactInformationEvaluator;

public class ProviderEvaluator extends MetadataEvaluator {

    private static final ProviderContactInformationEvaluator providerContactInformationEvaluator =
            new ProviderContactInformationEvaluator();

    private static final DisplaySettingsEvaluator displaySettingsEvaluator =
            new DisplaySettingsEvaluator();


    public void evaluate(Operation operation, Provider specimen1, Provider specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "entityId");
        performOperation(operation, specimen1, specimen2, "providerDomain");

        providerContactInformationEvaluator.evaluate(
            operation,
            specimen1.getDisplaySettings(),
            specimen2 == null ? null : specimen2.getDisplaySettings()
        );

        displaySettingsEvaluator.evaluate(
                operation,
                specimen1.getDisplaySettings(),
                specimen2 == null ? null : specimen2.getDisplaySettings()
        );
    }
}
