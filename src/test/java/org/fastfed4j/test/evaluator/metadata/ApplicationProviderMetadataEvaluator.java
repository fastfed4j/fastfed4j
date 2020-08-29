package org.fastfed4j.test.evaluator.metadata;

import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.RegistrationResponse;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;

import java.util.Set;

public class ApplicationProviderMetadataEvaluator extends CommonProviderMetadataEvaluator {

    private static final org.fastfed4j.test.evaluator.profile.enterprise.saml.ApplicationProviderMetadataExtensionEvaluator samlEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.saml.ApplicationProviderMetadataExtensionEvaluator();

    private static final org.fastfed4j.test.evaluator.profile.enterprise.scim.ApplicationProviderMetadataExtensionEvaluator scimEvaluator =
            new org.fastfed4j.test.evaluator.profile.enterprise.scim.ApplicationProviderMetadataExtensionEvaluator();


    public void evaluate(Operation operation, ApplicationProviderMetadata specimen1, ApplicationProviderMetadata specimen2) {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "handshakeRegisterUri");

        Set<String> extensionUrns = specimen1.getAllMetadataExtensions().keySet();
        for (String urn : extensionUrns) {
            if (urn.equals(AuthenticationProfile.ENTERPRISE_SAML.getUrn())) {
                evaluateSaml(operation, specimen1, specimen2);
            }
            else if (urn.equals(ProvisioningProfile.ENTERPRISE_SCIM.getUrn())) {
                evaluateScim(operation, specimen1, specimen2);
            }
            else {
                throw new RuntimeException("Missing evaluator for extension " + urn);
            }
        }
    }

    private void evaluateSaml(Operation operation, ApplicationProviderMetadata specimen1, ApplicationProviderMetadata specimen2) {
        EnterpriseSAML.ApplicationProviderMetadataExtension extension1 =
                (EnterpriseSAML.ApplicationProviderMetadataExtension) specimen1.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        EnterpriseSAML.ApplicationProviderMetadataExtension extension2 =
                specimen2 == null ? null :
                (EnterpriseSAML.ApplicationProviderMetadataExtension) specimen2.getMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());

        samlEvaluator.evaluate(operation, extension1, extension2);
    }

    private void evaluateScim(Operation operation, ApplicationProviderMetadata specimen1, ApplicationProviderMetadata specimen2) {
        EnterpriseSCIM.ApplicationProviderMetadataExtension extension1 =
                (EnterpriseSCIM.ApplicationProviderMetadataExtension) specimen1.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        EnterpriseSCIM.ApplicationProviderMetadataExtension extension2 =
                specimen2 == null ? null :
                (EnterpriseSCIM.ApplicationProviderMetadataExtension) specimen2.getMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());

        scimEvaluator.evaluate(operation, extension1, extension2);
    }

}
