package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.util.ValidationUtils;
import org.fastfed4j.profile.Profile;

import java.util.Objects;

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

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.IDENTITY_PROVIDER);
        builder.putAll(super.toJson());
        builder.put(JsonMember.JWKS_URI, jwksUri);
        builder.put(JsonMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);
        return builder.build();
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
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.IDENTITY_PROVIDER);
        super.hydrateFromJson(json);
        hydrateExtensions(json, Profile.ExtensionType.IdentityProviderMetadata);
        setJwksUri(json.getString(JsonMember.JWKS_URI));
        setHandshakeStartUri(json.getString(JsonMember.FASTFED_HANDSHAKE_START_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JsonMember.JWKS_URI, jwksUri);
        validateRequiredUrl(errorAccumulator, JsonMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);
        if (getCapabilities() != null)
            validateExtensions(errorAccumulator, getCapabilities().getAllKnownProfileUrns(), Profile.ExtensionType.IdentityProviderMetadata);
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
