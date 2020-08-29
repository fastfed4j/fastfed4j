package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.contract.IdentityProvider;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;

public class IdentityProviderEvaluator extends ProviderEvaluator {

    public void evaluate(Operation operation, IdentityProvider specimen1, IdentityProvider specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "jwksUri");
        performOperation(operation, specimen1, specimen2, "handshakeStartUri");

        for (String urn : specimen1.getAllRegistrationRequestExtensions().keySet()) {
            if (urn.equals(AuthenticationProfile.ENTERPRISE_SAML.getUrn())) {
                evaluateSamlRegistrationRequestExtension(operation, specimen1, specimen2);
            }
            else if (urn.equals(ProvisioningProfile.ENTERPRISE_SCIM.getUrn())) {
                evaluateScimRegistrationRequestExtension(operation, specimen1, specimen2);
            }
            else {
                throw new RuntimeException("Missing evaluator for extension " + urn);
            }
        }
    }

    private void evaluateSamlRegistrationRequestExtension(Operation operation,
                                                          IdentityProvider specimen1,
                                                          IdentityProvider specimen2)
    {
        EnterpriseSAML.RegistrationRequestExtension extension1 =
                specimen1.getEnterpriseSamlRegistrationRequestExtension();

        EnterpriseSAML.RegistrationRequestExtension extension2 =
                specimen2 == null ? null :
                specimen2.getEnterpriseSamlRegistrationRequestExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationRequestExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationRequestExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScimRegistrationRequestExtension(Operation operation,
                                                          IdentityProvider specimen1,
                                                          IdentityProvider specimen2)
    {
        EnterpriseSCIM.RegistrationRequestExtension extension1 =
                specimen1.getEnterpriseScimRegistrationRequestExtension();

        EnterpriseSCIM.RegistrationRequestExtension extension2 =
                specimen2 == null ? null :
                specimen2.getEnterpriseScimRegistrationRequestExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationRequestExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationRequestExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }
}
