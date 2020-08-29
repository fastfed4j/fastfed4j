package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.metadata.RegistrationRequest;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;

public class RegistrationRequestEvaluator extends JwtEvaluator {

    private static final org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationRequestExtensionEvaluator samlEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationRequestExtensionEvaluator();

    private static final org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationRequestExtensionEvaluator scimEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationRequestExtensionEvaluator();


    public void evaluate(Operation operation, RegistrationRequest specimen1, RegistrationRequest specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "authenticationProfiles");
        performOperation(operation, specimen1, specimen2, "provisioningProfiles");

        for (String urn : specimen1.getAllMetadataExtensions().keySet()) {
            if (urn.equals(AuthenticationProfile.ENTERPRISE_SAML.getUrn())) {
                evaluateSaml(operation, specimen1, specimen2);
            }
            else if (urn.equals(ProvisioningProfile.ENTERPRISE_SCIM.getUrn())) {
                evaluateScim(operation, specimen1, specimen2);
            }
            else {
                throw new RuntimeException("Missing evaluator for " + urn);
            }
        }
    }

    private void evaluateSaml(Operation operation, RegistrationRequest specimen1, RegistrationRequest specimen2) {
        EnterpriseSAML.RegistrationRequestExtension extension1 =
                (EnterpriseSAML.RegistrationRequestExtension) specimen1.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        EnterpriseSAML.RegistrationRequestExtension extension2 =
                specimen2 == null ? null :
                (EnterpriseSAML.RegistrationRequestExtension) specimen2.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        samlEvaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScim(Operation operation, RegistrationRequest specimen1, RegistrationRequest specimen2) {
        EnterpriseSCIM.RegistrationRequestExtension extension1 =
                (EnterpriseSCIM.RegistrationRequestExtension) specimen1.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        EnterpriseSCIM.RegistrationRequestExtension extension2 =
                specimen2 == null ? null :
                (EnterpriseSCIM.RegistrationRequestExtension) specimen2.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        scimEvaluator.evaluate(operation, extension1, extension2);
    }
}
