package org.fastfed4j.profile;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.metadata.Metadata;

import java.util.Optional;

/**
 *  Base class for all FastFed profile extensions.
 *
 *  <p>Within the FastFed specification, a profile simply adds additional attributes into the various FastFed data exchanges,
 *  such as the hosted metadata or the registration events.</p>
 *
 *  <p>In terms of code, this requires the implementation of new Metadata objects with
 *  getters/setters to access the extended attributes, plus methods to hydrate from JSON.</p>
 *
 *  <p>This interface defines the factory methods that each Profile class must implement in order to
 *  instantiate and hydrate the object extensions. Factory methods exist for each type of metadata
 *  in the FastFed spec.</p>
 *
 *  <p>If a Profile chooses not to extend a particular piece of the FastFed specification, the relevant
 *  factory can simply behave as a no-op that returns a null value, indicating that no extension exists.</p>
*/
public abstract class Profile {
    abstract public String getUrn();

    public enum ExtensionType {
        ApplicationProviderMetadata,
        IdentityProviderMetadata,
        RegistrationRequest,
        RegistrationResponse
    }

    public boolean requiresApplicationProviderMetadataExtension() { return false; }
    public boolean requiresIdentityProviderMetadataExtension() { return false; }
    public boolean requiresRegistrationRequestExtension() { return false; }
    public boolean requiresRegistrationResponseExtension() { return false; }

    public Optional<Metadata> newApplicationProviderMetadataExtension(FastFedConfiguration configuration) { return Optional.empty(); }
    public Optional<Metadata> newIdentityProviderMetadataExtension(FastFedConfiguration configuration) { return Optional.empty(); };
    public Optional<Metadata> newRegistrationRequestExtension(FastFedConfiguration configuration) { return Optional.empty(); }
    public Optional<Metadata> newRegistrationResponseExtension(FastFedConfiguration configuration) { return Optional.empty(); }
}
