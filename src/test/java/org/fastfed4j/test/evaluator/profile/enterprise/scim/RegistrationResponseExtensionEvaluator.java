package org.fastfed4j.test.evaluator.profile.enterprise.scim;

import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.metadata.Oauth2JwtServiceMetadata;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.Oauth2JwtServiceMetadataEvaluator;


public class RegistrationResponseExtensionEvaluator extends MetadataEvaluator {

    private static final Oauth2JwtServiceMetadataEvaluator oauth2JwtServiceMetadataEvaluator = new Oauth2JwtServiceMetadataEvaluator();

    public void evaluate(Operation operation,
                         EnterpriseSCIM.RegistrationResponseExtension specimen1,
                         EnterpriseSCIM.RegistrationResponseExtension specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);
        performOperation(operation, specimen1, specimen2, "providerAuthenticationProtocolUrn");
        performOperation(operation, specimen1, specimen2, "scimServiceUri");

        if (specimen1.getProviderAuthenticationProtocolUrn().equals(ProviderAuthenticationProtocol.OAUTH2_JWT)) {
            oauth2JwtServiceMetadataEvaluator.evaluate(
                    operation,
                    (Oauth2JwtServiceMetadata) specimen1.getProviderAuthenticationMethod(),
                    specimen2 == null ? null : (Oauth2JwtServiceMetadata) specimen2.getProviderAuthenticationMethod()
            );
        }
        else {
            throw new RuntimeException("Missing evaluation method for " + specimen1.getProviderAuthenticationProtocolUrn());
        }
    }

}
