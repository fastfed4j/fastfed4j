package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.metadata.RegistrationRequest;
import org.fastfed4j.core.metadata.RegistrationResponse;
import org.fastfed4j.profile.Profile;

import java.util.Optional;

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
        public ApplicationProviderMetadataExtension(ApplicationProviderMetadataExtension other) { super(other); }
    }
    public static class RegistrationRequestExtension extends org.fastfed4j.profile.saml.enterprise.RegistrationExtension {
        public RegistrationRequestExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
        public RegistrationRequestExtension(RegistrationRequestExtension other) { super(other); }
    }
    public static class RegistrationResponseExtension extends org.fastfed4j.profile.saml.enterprise.RegistrationExtension {
        public RegistrationResponseExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
        public RegistrationResponseExtension(RegistrationResponseExtension other) { super(other); }
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
    public Optional<Metadata> newApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
        return Optional.of(new ApplicationProviderMetadataExtension(configuration));
    }

    // Registration Request Extensions
    @Override
    public boolean requiresRegistrationRequestExtension() {
        return true;
    }

    @Override
    public Optional<Metadata> newRegistrationRequestExtension(FastFedConfiguration configuration) {
        return Optional.of(new RegistrationRequestExtension(configuration));
    }

    // Registration Response Extensions
    @Override
    public boolean requiresRegistrationResponseExtension() {
        return true;
    }

    @Override
    public Optional<Metadata> newRegistrationResponseExtension(FastFedConfiguration configuration) {
        return Optional.of(new RegistrationResponseExtension(configuration));
    }
}

