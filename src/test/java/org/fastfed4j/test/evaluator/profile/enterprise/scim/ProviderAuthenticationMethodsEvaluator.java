package org.fastfed4j.test.evaluator.profile.enterprise.scim;

import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.profile.scim.enterprise.ProviderAuthenticationMethods;
import org.fastfed4j.test.evaluator.Operation;
import org.fastfed4j.test.evaluator.metadata.MetadataEvaluator;
import org.fastfed4j.test.evaluator.metadata.Oauth2JwtClientMetadataEvaluator;

public class ProviderAuthenticationMethodsEvaluator extends MetadataEvaluator {

    private static final Oauth2JwtClientMetadataEvaluator oauth2JwtClientMetadataEvaluator = new Oauth2JwtClientMetadataEvaluator();

    public void evaluate(Operation operation,
                         ProviderAuthenticationMethods specimen1,
                         ProviderAuthenticationMethods specimen2)
    {
        super.evaluate(operation, specimen1, specimen2);

        for (String urn : specimen1.getAllMetadataExtensions().keySet()) {
            if (urn.equals(ProviderAuthenticationProtocol.OAUTH2_JWT.toString())) {
                oauth2JwtClientMetadataEvaluator.evaluate(
                        operation,
                        specimen1.getOauth2JwtClient(),
                        specimen2 == null ? null : specimen2.getOauth2JwtClient()
                );
            }
            else {
                throw new RuntimeException("Missing evaluation method for " + urn);
            }
        }
    }
}
