package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.metadata.RegistrationRequest;
import org.fastfed4j.core.metadata.RegistrationResponse;
import org.fastfed4j.profile.Profile;

/**
 * Represents the extensions defined in the FastFed Enterprise SAML Profile.
 */
public class EnterpriseSAML extends Profile {
    private static final String PROFILE_URN = AuthenticationProfile.ENTERPRISE_SAML.getUrn();

    // Namespace the extension classes under this package, to minimize naming ambiguity when
    // several profiles are concurrently in use.
    // E.g. the public class will be "EnterpriseSAML.ApplicationProviderMetadataExtension"
    public static class ApplicationProviderMetadataExtension extends org.fastfed4j.profile.saml.enterprise.ApplicationProviderMetadataExtension {
        public ApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
    }
    public static class RegistrationRequestExtension extends org.fastfed4j.profile.saml.enterprise.RegistrationExtension {
        public RegistrationRequestExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
    }
    public static class RegistrationResponseExtension extends org.fastfed4j.profile.saml.enterprise.RegistrationExtension {
        public RegistrationResponseExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
    }

    // Profile URN
    @Override
    public String getUrn() {
        return PROFILE_URN;
    }

    // Application Provider Metadata Extensions
    @Override
    public boolean requiresApplicationProviderMetadataExtension() {
        return true;
    }

    @Override
    public Metadata newApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
        return new ApplicationProviderMetadataExtension(configuration);
    }

    // Registration Request Extensions
    @Override
    public boolean requiresRegistrationRequestExtension() {
        return true;
    }

    @Override
    public Metadata newRegistrationRequestExtension(FastFedConfiguration configuration) {
        return new RegistrationRequestExtension(configuration);
    }

    // Registration Response Extensions
    @Override
    public boolean requiresRegistrationResponseExtension() {
        return true;
    }

    @Override
    public Metadata newRegistrationResponseExtension(FastFedConfiguration configuration) {
        return new RegistrationResponseExtension(configuration);
    }
}

