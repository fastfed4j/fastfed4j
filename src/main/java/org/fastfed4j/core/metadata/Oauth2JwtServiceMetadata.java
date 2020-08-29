package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;

import java.util.Objects;

/**
 * Represents the extended metadata required for a service using the OAuth2 Jwt Authentication Protocol,
 * as defined in section 3.2.2.1 of the FastFed Enterprise SCIM specification.
 */
public class Oauth2JwtServiceMetadata extends ProviderAuthenticationMetadata {
    private static final ProviderAuthenticationProtocol protocol = ProviderAuthenticationProtocol.OAUTH2_JWT;

    private String oauthTokenEndpoint;
    private String oauthScope;

    /**
     * Constructs an empty instance
     */
    public Oauth2JwtServiceMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     */
    public Oauth2JwtServiceMetadata(Oauth2JwtServiceMetadata other) {
        super(other);
        this.oauthTokenEndpoint = other.oauthTokenEndpoint;
        this.oauthScope = other.oauthScope;
    }

    public String getOauthTokenEndpoint() {
        return oauthTokenEndpoint;
    }

    public void setOauthTokenEndpoint(String oauthTokenEndpoint) {
        this.oauthTokenEndpoint = oauthTokenEndpoint;
    }

    public String getOauthScope() {
        return oauthScope;
    }

    public void setOauthScope(String oauthScope) {
        this.oauthScope = oauthScope;
    }

    @Override
    public ProviderAuthenticationProtocol getProviderAuthenticationProtocol() {
        return protocol;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(ProviderAuthenticationProtocol.OAUTH2_JWT.toString());
        builder.putAll(super.toJson());
        builder.put(JsonMember.OAUTH2_TOKEN_ENDPOINT, oauthTokenEndpoint);
        builder.put(JsonMember.OAUTH2_SCOPE, oauthScope);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        setOauthTokenEndpoint( json.getString(JsonMember.OAUTH2_TOKEN_ENDPOINT));
        setOauthScope( json.getString(JsonMember.OAUTH2_SCOPE));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JsonMember.OAUTH2_TOKEN_ENDPOINT, oauthTokenEndpoint);
        validateRequiredString(errorAccumulator, JsonMember.OAUTH2_SCOPE, oauthScope);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Oauth2JwtServiceMetadata that = (Oauth2JwtServiceMetadata) o;
        return Objects.equals(oauthTokenEndpoint, that.oauthTokenEndpoint) &&
                Objects.equals(oauthScope, that.oauthScope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oauthTokenEndpoint, oauthScope);
    }
}
