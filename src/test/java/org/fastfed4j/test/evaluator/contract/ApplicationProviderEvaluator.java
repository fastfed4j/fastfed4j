package org.fastfed4j.test.evaluator.contract;

import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.contract.ApplicationProvider;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;

public class ApplicationProviderEvaluator extends ProviderEvaluator {

    public void evaluate(Operation operation, ApplicationProvider specimen1, ApplicationProvider specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "handshakeRegisterUri");
        performOperation(operation, specimen1, specimen2, "handshakeFinalizeUri");

        for (String urn : specimen1.getAllApplicationProviderMetadataExtensions().keySet()) {
            if (urn.equals(AuthenticationProfile.ENTERPRISE_SAML.getUrn())) {
                evaluateSamlApplicationProviderMetadataExtension(operation, specimen1, specimen2);
            }
            else if (urn.equals(ProvisioningProfile.ENTERPRISE_SCIM.getUrn())) {
                evaluateScimApplicationProviderMetadataExtension(operation, specimen1, specimen2);
            }
            else {
                throw new RuntimeException("Missing evaluator for extension " + urn);
            }
        }

        for (String urn : specimen1.getAllRegistrationResponseExtensions().keySet()) {
            if (urn.equals(AuthenticationProfile.ENTERPRISE_SAML.getUrn())) {
                evaluateSamlRegistrationResponseExtension(operation, specimen1, specimen2);
            }
            else if (urn.equals(ProvisioningProfile.ENTERPRISE_SCIM.getUrn())) {
                evaluateScimRegistrationResponseExtension(operation, specimen1, specimen2);
            }
            else {
                throw new RuntimeException("Missing evaluator for extension " + urn);
            }
        }
    }

    private void evaluateSamlApplicationProviderMetadataExtension(Operation operation,
                                                                  ApplicationProvider specimen1,
                                                                  ApplicationProvider specimen2)
    {
        EnterpriseSAML.ApplicationProviderMetadataExtension extension1 =
                specimen1.getEnterpriseSamlApplicationProviderMetadataExtension();

        EnterpriseSAML.ApplicationProviderMetadataExtension extension2 =
                specimen2 == null ? null :
                specimen2.getEnterpriseSamlApplicationProviderMetadataExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.saml.ApplicationProviderMetadataExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.saml.ApplicationProviderMetadataExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScimApplicationProviderMetadataExtension(Operation operation,
                                                                  ApplicationProvider specimen1,
                                                                  ApplicationProvider specimen2)
    {
        EnterpriseSCIM.ApplicationProviderMetadataExtension extension1 =
                specimen1.getEnterpriseScimApplicationProviderMetadataExtension();

        EnterpriseSCIM.ApplicationProviderMetadataExtension extension2 =
                specimen2 == null ? null :
                specimen2.getEnterpriseScimApplicationProviderMetadataExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.scim.ApplicationProviderMetadataExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.scim.ApplicationProviderMetadataExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateSamlRegistrationResponseExtension(Operation operation,
                                                           ApplicationProvider specimen1,
                                                           ApplicationProvider specimen2)
    {
        EnterpriseSAML.RegistrationResponseExtension extension1 =
                specimen1.getEnterpriseSamlRegistrationResponseExtension();

        EnterpriseSAML.RegistrationResponseExtension extension2 =
                specimen2 == null ? null :
                        specimen2.getEnterpriseSamlRegistrationResponseExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationResponseExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationResponseExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScimRegistrationResponseExtension(Operation operation,
                                                           ApplicationProvider specimen1,
                                                           ApplicationProvider specimen2)
    {
        EnterpriseSCIM.RegistrationResponseExtension extension1 =
                specimen1.getEnterpriseScimRegistrationResponseExtension();

        EnterpriseSCIM.RegistrationResponseExtension extension2 =
                specimen2 == null ? null :
                specimen2.getEnterpriseScimRegistrationResponseExtension();

        org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationResponseExtensionEvaluator evaluator =
                new org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationResponseExtensionEvaluator();

        evaluator.evaluate(operation, extension1, extension2);
    }
}
