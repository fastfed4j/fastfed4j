package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.util.ValidationUtils;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.Objects;
import java.util.Set;

/**
 * Represents the Application Provider Metadata defined in section 3.3.8 of the FastFed Core specification.
 */
public class ApplicationProviderMetadata extends CommonProviderMetadata {
    private static final ValidationUtils validationUtils = new ValidationUtils();

    private String handshakeRegisterUri;

    /**
     * Constructs an empty instance
     */
    public ApplicationProviderMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public ApplicationProviderMetadata(ApplicationProviderMetadata other) {
        super(other);
        this.handshakeRegisterUri = other.handshakeRegisterUri;
    }

    /**
     * Get the Handshake Register URI to perform section 7.2.3.1 of the FastFed Core specification
     * @return uri
     */
    public String getHandshakeRegisterUri() {
        return handshakeRegisterUri;
    }

    /**
     * Set the Handshake Register URI to perform section 7.2.3.1 of the FastFed Core specification
     * @param handshakeRegisterUri uri
     */
    public void setHandshakeRegisterUri(String handshakeRegisterUri) {
        this.handshakeRegisterUri = handshakeRegisterUri;
    }

    /**
     * Convenience method to get the metadata extensions defined by the EnterpriseSAML profile.
     * @return EnterpriseSAML metadata extensions
     */
    public EnterpriseSAML.ApplicationProviderMetadataExtension getEnterpriseSamlExtension() {
        return getMetadataExtension(EnterpriseSAML.ApplicationProviderMetadataExtension.class, AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * Convenience method to get the metadata extensions defined by the EnterpriseSCIM profile.
     * @return EnterpriseSCIM metadata extensions
     */
    public EnterpriseSCIM.ApplicationProviderMetadataExtension getEnterpriseScimExtension() {
        return getMetadataExtension(EnterpriseSCIM.ApplicationProviderMetadataExtension.class, ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * Retrieve Application Provider Metadata from a URL endpoint
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param url the endpoint which hosts the metadata
     * @return ApplicationProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     * @throws FastFedSecurityException if the metadata content violates the FastFed security requirements
     */
    public static ApplicationProviderMetadata fromRemoteEndpoint(FastFedConfiguration configuration, String url)
            throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(url, "url must not be null");
        String jsonString = ""; //TODO - implement the HTTP query
        ApplicationProviderMetadata metadata = fromJson(configuration, jsonString);
        validationUtils.assertProviderDomainIsValid(url, metadata.getProviderDomain());
        return metadata;
    }

    /**
     * Map a JSON document into an instance of this class
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jsonString json document
     * @return ApplicationProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     */
    public static ApplicationProviderMetadata fromJson(FastFedConfiguration configuration, String jsonString)
            throws InvalidMetadataException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        ApplicationProviderMetadata metadata = new ApplicationProviderMetadata(configuration);
        metadata.hydrateAndValidate(jsonString);
        return metadata;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.APPLICATION_PROVIDER);
        super.hydrateFromJson(json);
        hydrateAttributes(json);
        hydrateExtensions(json);
    }

    private void hydrateAttributes(JSONObject json) {
        this.setHandshakeRegisterUri(json.getString(JSONMember.FASTFED_HANDSHAKE_REGISTER_URI));
    }

    private void hydrateExtensions(JSONObject json) {
        // Iterate through all the known profiles
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        for (String urn : registry.getAllUrns()) {
            Profile profile = registry.getByUrn(urn);
            //If a profile requires extended metadata, and the JSON contains it, hydrate the implementation
            if (profile.requiresApplicationProviderMetadataExtension() && json.containsKey(urn)) {
                Metadata implementation = registry.getByUrn(urn).newApplicationProviderMetadataExtension(getFastFedConfiguration());
                if (implementation == null) {
                    // This would occur from an implementation bug if a Profile indicates that a particular
                    // extension is required, but then neglects to implement a handler for the extension.
                    throw new RuntimeException(
                            "Missing implementation of ApplicationProviderMetadataExtension for profile '" + urn + "'");
                }
                implementation.hydrateFromJson(json.getObject(urn));
                addMetadataExtension(urn, implementation);
            }
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateAttributes(errorAccumulator);
        validateExtensions(errorAccumulator);
    }

    private void validateAttributes(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
    }

    private void validateExtensions(ErrorAccumulator errorAccumulator) {
        Set<String> capableProfiles = getCapabilities().filterToKnownProfiles();
        for (String profileUrn : capableProfiles) {
            validateProfile(errorAccumulator, profileUrn);
        }
    }

    private void validateProfile(ErrorAccumulator errorAccumulator, String profileUrn) {
        Profile profile = getFastFedConfiguration().getProfileRegistry().getByUrn(profileUrn);

        // If the Application Provider includes a certain profile in their list of capabilities, and the profile
        // mandates the existence of extended attributes in the metadata, ensure the extension is defined.
        if (profile.requiresApplicationProviderMetadataExtension() && !hasMetadataExtension(profileUrn)) {
            errorAccumulator.add("Missing value for '" + profileUrn + "'");
            return;
        }

        // If an extension is defined, ensure it is valid.
        if (hasMetadataExtension(profileUrn)) {
            getMetadataExtension(profileUrn).validate(errorAccumulator);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProviderMetadata metadata = (ApplicationProviderMetadata) o;
        return handshakeRegisterUri.equals(metadata.handshakeRegisterUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), handshakeRegisterUri);
    }
}
