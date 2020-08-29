package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.metadata.RegistrationResponse;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.contract.EnabledProfilesEvaluator;

public class RegistrationResponseEvaluator extends MetadataEvaluator {
    
    private static final org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationResponseExtensionEvaluator samlEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.saml.RegistrationResponseExtensionEvaluator();

    private static final org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationResponseExtensionEvaluator scimEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.scim.RegistrationResponseExtensionEvaluator();


    public void evaluate(Operation operation, RegistrationResponse specimen1, RegistrationResponse specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "handshakeFinalizeUri");

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

    private void evaluateSaml(Operation operation, RegistrationResponse specimen1, RegistrationResponse specimen2) {
        EnterpriseSAML.RegistrationResponseExtension extension1 =
                (EnterpriseSAML.RegistrationResponseExtension) specimen1.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        EnterpriseSAML.RegistrationResponseExtension extension2 =
                specimen2 == null ? null :
                        (EnterpriseSAML.RegistrationResponseExtension) specimen2.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        samlEvaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScim(Operation operation, RegistrationResponse specimen1, RegistrationResponse specimen2) {
        EnterpriseSCIM.RegistrationResponseExtension extension1 =
                (EnterpriseSCIM.RegistrationResponseExtension) specimen1.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        EnterpriseSCIM.RegistrationResponseExtension extension2 =
                specimen2 == null ? null :
                        (EnterpriseSCIM.RegistrationResponseExtension) specimen2.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        scimEvaluator.evaluate(operation, extension1, extension2);
    }
}
