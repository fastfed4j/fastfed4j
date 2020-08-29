package org.fastfed4j.core.json;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.ProviderContactInformation;
import org.fastfed4j.profile.scim.enterprise.ProviderAuthenticationMethods;
import org.fastfed4j.profile.scim.enterprise.RegistrationRequestExtension;

import java.util.Date;
import java.util.Objects;

/**
 * Base class for all FastFed messages that are represented as a Jwt.
 */
public abstract class Jwt extends Metadata {

    private String issuer;
    private String audience;
    private Date expiration;

    /**
     * Constructs an empty instance
     */
    public Jwt(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public Jwt(Jwt other) {
        super(other);
        this.issuer = other.issuer;
        this.audience = other.audience;
        this.expiration = other.expiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    // TODO
    //public static Jwt fromCompactSerialization(String s) {
    //    return new Jwt();
    //
    //}

    public String toCompactSerialization() {
        return ""; //TODO
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        builder.put(JsonMember.JWT_ISSUER, issuer);
        builder.put(JsonMember.JWT_AUDIENCE, audience);
        builder.put(JsonMember.JWT_EXPIRATION, expiration);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        this.setIssuer(json.getString(JsonMember.JWT_ISSUER));
        this.setAudience(json.getString(JsonMember.JWT_AUDIENCE));
        this.setExpiration(json.getDate(JsonMember.JWT_EXPIRATION));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JsonMember.JWT_ISSUER, issuer);
        validateRequiredString(errorAccumulator, JsonMember.JWT_AUDIENCE, audience);
        validateRequiredDate(errorAccumulator, JsonMember.JWT_EXPIRATION, expiration);
        //TODO - add full Jwt validation
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Jwt jwt = (Jwt) o;
        return Objects.equals(issuer, jwt.issuer) &&
                Objects.equals(audience, jwt.audience) &&
                Objects.equals(expiration, jwt.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), issuer, audience, expiration);
    }
}
