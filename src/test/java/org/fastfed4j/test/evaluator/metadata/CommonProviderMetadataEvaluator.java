package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.metadata.CommonProviderMetadata;
import org.fastfed4j.test.evaluator.Operation;

public class CommonProviderMetadataEvaluator extends MetadataEvaluator {

    private static final ProviderContactInformationEvaluator providerContactInformationEvaluator = new ProviderContactInformationEvaluator();
    private static final DisplaySettingsEvaluator displaySettingsEvaluator = new DisplaySettingsEvaluator();
    private static final CapabilitiesEvaluator capabilitiesEvaluator = new CapabilitiesEvaluator();

    public void evaluate(Operation operation, CommonProviderMetadata specimen1, CommonProviderMetadata specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "entityId");
        performOperation(operation, specimen1, specimen2, "providerDomain");

        providerContactInformationEvaluator.evaluate(
                operation,
                specimen1.getProviderContactInformation(),
                specimen2 == null ? null : specimen2.getProviderContactInformation()
        );

        displaySettingsEvaluator.evaluate(
                operation,
                specimen1.getDisplaySettings(),
                specimen2 == null ? null : specimen2.getDisplaySettings()
        );

        capabilitiesEvaluator.evaluate(
                operation,
                specimen1.getCapabilities(),
                specimen2 == null ? null : specimen2.getCapabilities()
        );
    }
}
