package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.json.Jwt;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a Registration Request message, as defined in section 7.2.3.1 of the FastFed Core specification.
 */
public class RegistrationRequest extends Jwt {
    Set<String> authenticationProfiles;
    Set<String> provisioningProfiles;

    /**
     * Constructs an empty instance
     */
    public RegistrationRequest(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     */
    public RegistrationRequest(RegistrationRequest other) {
        super(other);
        if (other.authenticationProfiles != null)
            this.authenticationProfiles = new HashSet<>(other.authenticationProfiles);
        if (other.provisioningProfiles != null)
            this.provisioningProfiles = new HashSet<>(other.provisioningProfiles);
    }

    /**
     * Gets the authentication profiles to be enabled between the Identity Provider and Application Provider.
     * @return authentication profile URNs
     */
    public Set<String> getAuthenticationProfiles() {
        return authenticationProfiles;
    }

    /**
     * Sets the authentication profiles to be enabled between the Identity Provider and Application Provider.
     * @param authenticationProfiles authentication profile URNs
     */
    public void setAuthenticationProfiles(Set<String> authenticationProfiles) {
        this.authenticationProfiles = authenticationProfiles;
    }

    /**
     * Gets the provisioning profiles to be enabled between the Identity Provider and Application Provider.
     * @return provisioning profile URNs
     */
    public Set<String> getProvisioningProfiles() {
        return provisioningProfiles;
    }

    /**
     * Sets the provisioning profiles to be enabled between the Identity Provider and Application Provider.
     * @param provisioningProfiles provisioning profile URNs
     */
    public void setProvisioningProfiles(Set<String> provisioningProfiles) {
        this.provisioningProfiles = provisioningProfiles;
    }

    /**
     * Convenience method to get a collection of all the authentication and provisioning profiles.
     * @return all authentication and provisioning profile URNs contained within the RegistrationRequest
     */
    public Set<String> getAllProfiles() {
        Set<String> allProfiles = new HashSet<>();
        if (authenticationProfiles != null) {
            allProfiles.addAll(authenticationProfiles);
        }
        if (provisioningProfiles != null) {
            allProfiles.addAll(provisioningProfiles);
        }
        return allProfiles;
    }

    /**
     * Convenience method to get the extensions defined by the EnterpriseSAML profile.
     * @return EnterpriseSAML extensions, or null if the EnterpriseSAML profile is not in use.
     */
    public EnterpriseSAML.RegistrationRequestExtension getEnterpriseSamlExtension() {
        return getMetadataExtension(EnterpriseSAML.RegistrationRequestExtension.class, AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * Convenience method to get the extensions defined by the EnterpriseSCIM profile.
     * @return EnterpriseSCIM extensions, or null if the EnterpriseSCIM profile is not in use.
     */
    public EnterpriseSCIM.RegistrationRequestExtension getEnterpriseScimExtension() {
        return getMetadataExtension(EnterpriseSCIM.RegistrationRequestExtension.class, ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        builder.putAll(super.toJson());
        if (authenticationProfiles != null)
            builder.put(JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        if (provisioningProfiles != null)
            builder.put(JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
        for (Metadata obj : getAllMetadataExtensions().values()) {
            builder.putAll(obj.toJson());
        }
        return builder.build();
    }

    /**
     * Parses and validates a Jwt and then constructs an instance of this class from the contents of the token.
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jwt Java Web Token in compact serialization form
     * @return RegistrationRequest
     * @throws InvalidMetadataException if the Jwt is malformed or missing content
     * @throws FastFedSecurityException if the Jwt signature is invalid or expired
     */
    public static RegistrationRequest fromJwt(FastFedConfiguration configuration,
                                              String jwt)
        throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jwt, "jwt must not be null");
        // TODO - Deserialize and validate the jwt
        String json = jwt;
        return fromJson(configuration, json);
    }

    /**
     * Map a JSON document into an instance of this class
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param json json document
     * @return RegistrationRequest
     * @throws InvalidMetadataException if the json is malformed or missing content
     */
    public static RegistrationRequest fromJson(FastFedConfiguration configuration,
                                               String json)
            throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(json, "json must not be null");
        RegistrationRequest registrationRequest = new RegistrationRequest(configuration);
        registrationRequest.hydrateAndValidate(json);
        return registrationRequest;
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        hydrateExtensions(json, Profile.ExtensionType.RegistrationRequest);
        setAuthenticationProfiles( json.getStringSet(JsonMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getStringSet(JsonMember.PROVISIONING_PROFILES));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateExtensions(errorAccumulator, getAllProfiles(), Profile.ExtensionType.RegistrationRequest);
        validateOptionalStringCollection(errorAccumulator, JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegistrationRequest that = (RegistrationRequest) o;
        return Objects.equals(authenticationProfiles, that.authenticationProfiles) &&
                Objects.equals(provisioningProfiles, that.provisioningProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), authenticationProfiles, provisioningProfiles);
    }
}
