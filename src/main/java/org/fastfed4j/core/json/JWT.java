package org.fastfed4j.core.json;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.metadata.Metadata;

import java.util.Date;

/**
 * Base class for all FastFed messages that are represented as a JWT.
 */
public abstract class JWT extends Metadata {

    private String issuer;
    private String audience;
    private Date expiration;

    /**
     * Constructs an empty instance
     */
    public JWT (FastFedConfiguration configuration) {
        super(configuration);
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
    //public static JWT fromCompactSerialization(String s) {
    //    return new JWT();
    //
    //}

    public String toCompactSerialization() {
        return ""; //TODO
    }

    @Override
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder();
        builder.put(JSONMember.JWT_ISSUER, issuer);
        builder.put(JSONMember.JWT_AUDIENCE, audience);
        builder.put(JSONMember.JWT_EXPIRATION, expiration);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        this.setIssuer(json.getString(JSONMember.JWT_ISSUER));
        this.setAudience(json.getString(JSONMember.JWT_AUDIENCE));
        this.setExpiration(json.getDate(JSONMember.JWT_EXPIRATION));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JSONMember.JWT_ISSUER, issuer);
        validateRequiredString(errorAccumulator, JSONMember.JWT_AUDIENCE, audience);
        validateRequiredDate(errorAccumulator, JSONMember.JWT_EXPIRATION, expiration);
        //TODO - add full JWT validation
    }
}
