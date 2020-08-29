package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;

import java.util.Objects;

/**
 * Represents the extended metadata required for a client using the OAuth2 Jwt Authentication Protocol,
 * as defined in section 3.2.1.1 of the FastFed Enterprise SCIM specification.
 */
public class Oauth2JwtClientMetadata extends ProviderAuthenticationMetadata {
    private static final ProviderAuthenticationProtocol protocol = ProviderAuthenticationProtocol.OAUTH2_JWT;

    private String jwksUri;

    /**
     * Constructs an empty instance
     */
    public Oauth2JwtClientMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     */
    public Oauth2JwtClientMetadata(Oauth2JwtClientMetadata other) {
        super(other);
        this.jwksUri = other.jwksUri;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    @Override
    public ProviderAuthenticationProtocol getProviderAuthenticationProtocol() {
        return protocol;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(ProviderAuthenticationProtocol.OAUTH2_JWT.toString());
        builder.putAll(super.toJson());
        builder.put(JsonMember.JWKS_URI, jwksUri);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        setJwksUri( json.getString(JsonMember.JWKS_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JsonMember.JWKS_URI, jwksUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Oauth2JwtClientMetadata that = (Oauth2JwtClientMetadata) o;
        return jwksUri.equals(that.jwksUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwksUri);
    }

}
