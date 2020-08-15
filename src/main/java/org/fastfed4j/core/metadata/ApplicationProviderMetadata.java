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
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.Objects;

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
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder(JSONMember.APPLICATION_PROVIDER);
        builder.putAll(super.toJson());
        builder.put(JSONMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);

        for (Metadata obj : getAllMetadataExtensions().values()) {
            builder.putAll(obj.toJson());
        }

        return builder.build();
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.APPLICATION_PROVIDER);
        super.hydrateFromJson(json);
        hydrateExtensions(json, Profile.ExtensionType.ApplicationProviderMetadata);
        setHandshakeRegisterUri(json.getString(JSONMember.FASTFED_HANDSHAKE_REGISTER_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        if (getCapabilities() != null) {
            validateExtensions(errorAccumulator, getCapabilities().getAllKnownProfiles(), Profile.ExtensionType.ApplicationProviderMetadata);
        }
        validateRequiredUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
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
