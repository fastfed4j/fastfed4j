package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.Oauth2JwtServiceMetadata;
import org.fastfed4j.core.metadata.ProviderAuthenticationMetadata;

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
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        // SCIM Service URL
        setScimServiceUri(json.getString(JSONMember.SCIM_SERVICE_URI));

        // AUTHENTICATION PROTOCOL URN
        String protocol = json.getString(JSONMember.PROVIDER_AUTHENTICATION_METHOD);
        boolean protocolIsSupported = false;
        if (protocol != null) {
            if (ProviderAuthenticationProtocol.isValid(protocol)) {
                protocolIsSupported = true;
                setProviderAuthenticationProtocolUrn(ProviderAuthenticationProtocol.fromString(protocol));
            } else {
                json.getErrorAccumulator().add(
                        "Unsupported '" + JSONMember.PROVIDER_AUTHENTICATION_METHOD
                        + "' (value='" + protocol + "')"
                );
            }
        }

        // EXTENDED METADATA FOR THE AUTHENTICATION PROTOCOL
        if (protocolIsSupported && json.containsValueForMember(protocol)) {
            if (protocol.equals( ProviderAuthenticationProtocol.OAUTH2_JWT.getUrn())) {
                Oauth2JwtServiceMetadata oauth2JwtServiceMetadata = new Oauth2JwtServiceMetadata(getFastFedConfiguration());
                oauth2JwtServiceMetadata.hydrateFromJson(json.getObject(providerAuthenticationProtocolUrn.toString()));
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
        validateRequiredUrl(errorAccumulator, JSONMember.SCIM_SERVICE_URI, scimServiceUri);
        validateRequiredObject(errorAccumulator, JSONMember.PROVIDER_AUTHENTICATION_METHOD, providerAuthenticationProtocolUrn);
        validateRequiredObject(errorAccumulator, providerAuthenticationProtocolUrn.toString(), providerAuthenticationMetadata);

        if (providerAuthenticationMetadata != null) { providerAuthenticationMetadata.validate(errorAccumulator); }
    }
}
