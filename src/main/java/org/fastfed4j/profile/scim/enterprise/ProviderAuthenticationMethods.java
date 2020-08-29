package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.Oauth2JwtClientMetadata;
import org.fastfed4j.core.metadata.ProviderAuthenticationMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the Provider Authentication Methods as defined in sections 3.2.1 and 3.2.1.1 of
 * the FastFed Enterprise SCIM specification.
 *
 * <p>The contents are a map where the key is the URN of the Provider Authentication Protocol and the value is a
 * Metadata structure containing the extended attributes required by the protocol.</p>
 *
 * <p>Currently, the implementation only supports the OAuth2 Jwt protocol. It throws a runtime exception if any other
 * unknown protocol is encountered. If additional protocols become adopted in the future, the implementation will need
 * to be updated to handle them.</p>
 */
public class ProviderAuthenticationMethods extends Metadata {

    /**
     * Constructs an empty instance
     */
    public ProviderAuthenticationMethods(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     */
    public ProviderAuthenticationMethods(ProviderAuthenticationMethods other) {
        super(other);
    }

    /**
     * Adds a Provider Authentication method to the list of supported methods.
     * @param metadata metadata for the provider authentication method
     */
    public void addProviderAuthenticationMethod(ProviderAuthenticationMetadata metadata) {
        addMetadataExtension(metadata.getProviderAuthenticationProtocol().toString(), metadata);
    }

    /**
     * Tests if a protocol exists in the list of supported Provider Authentication methods.
     * @param protocol urn
     * @return true if the protocol is supported
     */
    public boolean hasProviderAuthenticationMethod(ProviderAuthenticationProtocol protocol) {
        return hasMetadataExtension(protocol.getUrn());
    }

    /**
     * Get the metaadata for a particular Provider Authentication method
     * @param protocol urn
     * @return ProviderAuthenticationMetadata if the protocol is supported, else null
     */
    public ProviderAuthenticationMetadata getProviderAuthenticationMethod(ProviderAuthenticationProtocol protocol) {
        return getMetadataExtension(ProviderAuthenticationMetadata.class, protocol.getUrn());
    }

    /**
     * Get the URNs for all supported Provider Authentication protocols
     * @return set of all supported protocols
     */
    public Set<ProviderAuthenticationProtocol> getAllSupportedAuthenticationProtocols() {
        Set<ProviderAuthenticationProtocol> returnVal = new HashSet<>();
        for (String key : getAllMetadataExtensions().keySet()) {
            returnVal.add(ProviderAuthenticationProtocol.fromString(key));
        }
        return returnVal;
    }

    /**
     * Convenience method to test if the OAuth Jwt protocol is supported by the Identity Provider
     * @return true if supported
     */
    public boolean supportsOauth2Jwt() {
        return hasMetadataExtension(ProviderAuthenticationProtocol.OAUTH2_JWT.getUrn());
    }

    /**
     * Convenience method to get the extended metadata for a client who supports the OAuth Jwt protocol.
     * @return OauthJwtClient, or null if the OAuth Jwt protocol is not supported
     */
    public Oauth2JwtClientMetadata getOauth2JwtClient() {
        Oauth2JwtClientMetadata returnVal = null;
        if (supportsOauth2Jwt()) {
            returnVal = (Oauth2JwtClientMetadata)getMetadataExtension(ProviderAuthenticationProtocol.OAUTH2_JWT.getUrn());
        }
        return returnVal;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.PROVIDER_AUTHENTICATION_METHODS);
        builder.putAll(super.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.PROVIDER_AUTHENTICATION_METHODS);
        super.hydrateFromJson(json);

        for (String protocolUrn : json.keySet()) {
            if (protocolUrn.equals(ProviderAuthenticationProtocol.OAUTH2_JWT.toString())) {
                Oauth2JwtClientMetadata metadataExtension = new Oauth2JwtClientMetadata(getFastFedConfiguration());
                metadataExtension.hydrateFromJson(json.getObject(protocolUrn));
                addMetadataExtension(protocolUrn, metadataExtension);
            }
            else {
                // TODO - log an error
                // "No implementation for JSON hydration of Provider Authentication Protocol: " + protocolUrn
                // If another protocol is added in the future, update the Javadocs for the class to reflect the change.
            }
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        for (Metadata extendedMetadata : getAllMetadataExtensions().values()) {
            extendedMetadata.validate(errorAccumulator);
        }
    }


}
