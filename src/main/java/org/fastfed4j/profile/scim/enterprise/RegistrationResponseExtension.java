package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.Oauth2JwtServiceMetadata;
import org.fastfed4j.core.metadata.ProviderAuthenticationMetadata;
import org.fastfed4j.core.util.ReflectionUtils;

import java.util.Objects;

/**
 * Represents the extensions to the Registration Response messages defined by
 * section 3.2.2 of the FastFed Enterprise SCIM Profile.
 */
public class RegistrationResponseExtension extends Metadata {
    private String scimServiceUri;
    private ProviderAuthenticationProtocol providerAuthenticationProtocolUrn;
    private ProviderAuthenticationMetadata providerAuthenticationMetadata;

    /**
     * Constructs an empty instance
     */
    public RegistrationResponseExtension(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public RegistrationResponseExtension(RegistrationResponseExtension other) {
        super(other);
        this.scimServiceUri = other.scimServiceUri;
        this.providerAuthenticationProtocolUrn = other.providerAuthenticationProtocolUrn;
        if (other.providerAuthenticationMetadata != null) {
            // ProviderAuthenticationMetadata is an abstract base class, so reflection is used to create
            // a copy of whatever concrete implementation is in use.
            this.providerAuthenticationMetadata = (ProviderAuthenticationMetadata) ReflectionUtils.copy(other.providerAuthenticationMetadata);
        }
    }

    /**
     * Get the URI for the SCIM service hosted by the Application Provider
     * @return scim service uri
     */
    public String getScimServiceUri() {
        return scimServiceUri;
    }

    /**
     * Set the URI for the SCIM service hosted by the Application Provider
     * @param scimServiceUri scim service uri
     */
    public void setScimServiceUri(String scimServiceUri) {
        this.scimServiceUri = scimServiceUri;
    }

    /**
     * Get the URN of the authentication protocol to be used when calling the SCIM service.
     * @return ProviderAuthenticationProtocol
     */
    public ProviderAuthenticationProtocol getProviderAuthenticationProtocolUrn() {
        return providerAuthenticationProtocolUrn;
    }

    /**
     * Set the URN of the authentication protocol to be used when calling the SCIM service.
     * @param providerAuthenticationProtocolUrn ProviderAuthenticationProtocol
     */
    public void setProviderAuthenticationProtocolUrn(ProviderAuthenticationProtocol providerAuthenticationProtocolUrn) {
        this.providerAuthenticationProtocolUrn = providerAuthenticationProtocolUrn;
    }

    /**
     * Get the extended metadata for the Provider Authentication Method
     */
    public ProviderAuthenticationMetadata getProviderAuthenticationMethod() {
        return providerAuthenticationMetadata;
    }

    /**
     * Set the extended metadata for the Provider Authentication Method
     */
    public void setProviderAuthenticationMethod(ProviderAuthenticationMetadata providerAuthenticationMetadata) {
        this.providerAuthenticationMetadata = providerAuthenticationMetadata;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
        builder.putAll(super.toJson());
        builder.put(JsonMember.SCIM_SERVICE_URI, scimServiceUri);
        if (providerAuthenticationMetadata != null)
            builder.putAll(providerAuthenticationMetadata.toJson());
        if (providerAuthenticationProtocolUrn != null)
            builder.put(JsonMember.PROVIDER_AUTHENTICATION_METHOD, providerAuthenticationProtocolUrn.getUrn());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        // SCIM Service URL
        setScimServiceUri(json.getString(JsonMember.SCIM_SERVICE_URI));

        // AUTHENTICATION PROTOCOL URN
        boolean protocolIsSupported = false;
        String protocol = json.getString(JsonMember.PROVIDER_AUTHENTICATION_METHOD);
        if (protocol != null) {
            if (ProviderAuthenticationProtocol.isValid(protocol)) {
                protocolIsSupported = true;
                setProviderAuthenticationProtocolUrn(ProviderAuthenticationProtocol.fromString(protocol));
            } else {
                json.getErrorAccumulator().add(
                        "Unsupported value for \"" + getFullyQualifiedName(JsonMember.PROVIDER_AUTHENTICATION_METHOD) +
                        "\" (value=\"" + protocol + "\")"
                );
            }
        }

        // EXTENDED METADATA FOR THE AUTHENTICATION PROTOCOL
        JsonObject protocolJson = json.getObject(protocol);
        if (protocolIsSupported && protocolJson != null) {
            if (protocol.equals( ProviderAuthenticationProtocol.OAUTH2_JWT.getUrn())) {
                Oauth2JwtServiceMetadata oauth2JwtServiceMetadata = new Oauth2JwtServiceMetadata(getFastFedConfiguration());
                oauth2JwtServiceMetadata.hydrateFromJson(protocolJson);
                setProviderAuthenticationMethod(oauth2JwtServiceMetadata);
            }
            else {
                throw new RuntimeException("No implementation for JSON hydration of Provider Authentication Protocol: "
                                            + providerAuthenticationProtocolUrn.toString());
            }
        }

    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JsonMember.SCIM_SERVICE_URI, scimServiceUri);
        validateRequiredObject(errorAccumulator, JsonMember.PROVIDER_AUTHENTICATION_METHOD, providerAuthenticationProtocolUrn);
        validateRequiredObject(errorAccumulator, providerAuthenticationProtocolUrn.toString(), providerAuthenticationMetadata);

        if (providerAuthenticationMetadata != null) { providerAuthenticationMetadata.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegistrationResponseExtension that = (RegistrationResponseExtension) o;
        return Objects.equals(scimServiceUri, that.scimServiceUri) &&
                providerAuthenticationProtocolUrn == that.providerAuthenticationProtocolUrn &&
                Objects.equals(providerAuthenticationMetadata, that.providerAuthenticationMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scimServiceUri, providerAuthenticationProtocolUrn, providerAuthenticationMetadata);
    }
}
