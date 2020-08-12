package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.util.ValidationUtils;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.Objects;
import java.util.Set;

/**
 * Represents the Identity Provider Metadata, as defined in section 3.3.7 of the FastFed Core specification.
 */
public class IdentityProviderMetadata extends CommonProviderMetadata {
    private static final ValidationUtils validationUtils = new ValidationUtils();
    private String jwksUri;
    private String handshakeStartUri;

    /**
     * Constructs an empty instance
     */
    public IdentityProviderMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public IdentityProviderMetadata(IdentityProviderMetadata other) {
        super(other);
        this.jwksUri = other.jwksUri;
        this.handshakeStartUri = other.handshakeStartUri;
    }

    /**
     * Get the JWKS URI for the Identity Provider
     * @return uri
     */
    public String getJwksUri() {
        return jwksUri;
    }

    /**
     * Set the JWKS URI for the Identity Provider
     * @param jwksUri uri
     */
    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    /**
     * Get the Handshake Start URI to perform section 7.2.1.7 of the FastFed Core specification.
     * @return uri
     */
    public String getHandshakeStartUri() {
        return handshakeStartUri;
    }

    /**
     * Set the Handshake Start URI to perform section 7.2.1.7 of the FastFed Core specification.
     * @param handshakeStartUri uri
     */
    public void setHandshakeStartUri(String handshakeStartUri) {
        this.handshakeStartUri = handshakeStartUri;
    }

    /**
     * Retrieve Identity Provider Metadata from a URL endpoint
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param url the endpoint which hosts the metadata
     * @return IdentityProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     * @throws FastFedSecurityException if the metadata content violates the FastFed security requirements
     */
    public static IdentityProviderMetadata fromRemoteEndpoint(FastFedConfiguration configuration, String url)
            throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(url, "Missing value for retrievedFromUrl");
        String jsonString = ""; //TODO - implement the HTTP query
        IdentityProviderMetadata metadata = fromJson(configuration, jsonString);
        validationUtils.assertProviderDomainIsValid(url, metadata.getProviderDomain());
        return metadata;
    }

    /**
     * Map a JSON document into an instance of this class
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jsonString json document
     * @return IdentityProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     */
    public static IdentityProviderMetadata fromJson(FastFedConfiguration configuration, String jsonString)
            throws InvalidMetadataException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        IdentityProviderMetadata metadata = new IdentityProviderMetadata(configuration);
        metadata.hydrateAndValidate(jsonString);
        return metadata;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.IDENTITY_PROVIDER);
        super.hydrateFromJson(json);
        hydrateAttributes(json);
        hydrateExtensions(json);
    }

    private void hydrateAttributes(JSONObject json) {
        this.setJwksUri( json.getString(JSONMember.JWKS_URI));
        this.setHandshakeStartUri( json.getString(JSONMember.FASTFED_HANDSHAKE_START_URI));
    }

    private void hydrateExtensions(JSONObject json) {
        // Iterate through all the known profiles
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        for (String urn : registry.getAllUrns()) {
            Profile profile = registry.getByUrn(urn);
            //If a profile requires extended metadata, and the JSON contains it, hydrate the implementation
            if (profile.requiresIdentityProviderMetadataExtension() && json.containsKey(urn)) {
                Metadata implementation = registry.getByUrn(urn).newIdentityProviderMetadataExtension(getFastFedConfiguration());
                if (implementation == null) {
                    // This would occur from an implementation bug if a Profile indicates that a particular
                    // extension is required, but then neglects to implement a handler for the extension.
                    throw new RuntimeException(
                            "Missing implementation of IdentityProviderMetadataExtension for profile '" + urn + "'");
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
        validateRequiredUrl(errorAccumulator, JSONMember.JWKS_URI, jwksUri);
        validateRequiredUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);
    }

    private void validateExtensions(ErrorAccumulator errorAccumulator) {
        Set<String> capableProfiles = getCapabilities().filterToKnownProfiles();
        for (String profileUrn : capableProfiles) {
            validateProfile(errorAccumulator, profileUrn);
        }
    }

    private void validateProfile(ErrorAccumulator errorAccumulator, String profileUrn) {
        Profile profile = getFastFedConfiguration().getProfileRegistry().getByUrn(profileUrn);

        // If the Identity Provider includes a certain profile in their list of capabilities, and the profile
        // mandates the existence of extended attributes in the metadata, ensure the extension is defined.
        if (profile.requiresIdentityProviderMetadataExtension() && !hasMetadataExtension(profileUrn)) {
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
        IdentityProviderMetadata that = (IdentityProviderMetadata) o;
        return jwksUri.equals(that.jwksUri) &&
                handshakeStartUri.equals(that.handshakeStartUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwksUri, handshakeStartUri);
    }
}
