package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.profile.Profile;

import java.util.Optional;

/**
 * Represents the extensions defined in the FastFed Enterprise SCIM Profile.
 */
public class EnterpriseSCIM extends Profile {
    private static final String PROFILE_URN = ProvisioningProfile.ENTERPRISE_SCIM.getUrn();

    // Namespace the extension classes under this package, to minimize naming ambiguity when
    // several profiles are concurrently in use.
    // E.g. the public class will be "EnterpriseSAML.ApplicationProviderMetadataExtension"
    public static class ApplicationProviderMetadataExtension extends org.fastfed4j.profile.scim.enterprise.ApplicationProviderMetadataExtension {
        public ApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
        public ApplicationProviderMetadataExtension(ApplicationProviderMetadataExtension other) { super(other); }
    }
    public static class RegistrationRequestExtension extends org.fastfed4j.profile.scim.enterprise.RegistrationRequestExtension {
        public RegistrationRequestExtension(FastFedConfiguration configuration) {
            super(configuration);
        }
        public RegistrationRequestExtension(RegistrationRequestExtension other) { super(other); }
    }
    public static class RegistrationResponseExtension extends org.fastfed4j.profile.scim.enterprise.RegistrationResponseExtension {
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
        return Optional.of(new EnterpriseSCIM.ApplicationProviderMetadataExtension(configuration));
    }

    // Registration Request Extensions
    @Override
    public boolean requiresRegistrationRequestExtension() {
        return true;
    }

    @Override
    public Optional<Metadata> newRegistrationRequestExtension(FastFedConfiguration configuration) {
        return Optional.of(new EnterpriseSCIM.RegistrationRequestExtension(configuration));
    }

    // Registration Response Extensions
    @Override
    public boolean requiresRegistrationResponseExtension() {
        return true;
    }

    @Override
    public Optional<Metadata> newRegistrationResponseExtension(FastFedConfiguration configuration) {
        return Optional.of(new EnterpriseSCIM.RegistrationResponseExtension(configuration));
    }

}

