package org.fastfed4j.test.evaluator.profile.enterprise.scim;

import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.JwtEvaluator;
import org.fastfed4j.test.evaluator.metadata.ProviderContactInformationEvaluator;


public class RegistrationRequestExtensionEvaluator extends JwtEvaluator {

    private static final ProviderContactInformationEvaluator providerContactInformationEvaluator = new ProviderContactInformationEvaluator();
    private static final ProviderAuthenticationMethodsEvaluator providerAuthenticationMethodsEvaluator = new ProviderAuthenticationMethodsEvaluator();

    public void evaluate(Operation operation,
                         EnterpriseSCIM.RegistrationRequestExtension specimen1,
                         EnterpriseSCIM.RegistrationRequestExtension specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);

        providerContactInformationEvaluator.evaluate(
                operation,
                specimen1.getProviderContactInformation(),
                specimen2 == null ? null : specimen2.getProviderContactInformation());

        providerAuthenticationMethodsEvaluator.evaluate(
                operation,
                specimen1.getProviderAuthenticationMethods(),
                specimen2 == null ? null : specimen2.getProviderAuthenticationMethods()
        );
    }

}
