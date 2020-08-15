package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.json.JWT;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a Registration Request message, as defined in section 7.2.3.1 of the FastFed Core specification.
 */
public class RegistrationRequest extends JWT {
    Set<String> authenticationProfiles;
    Set<String> provisioningProfiles;

    /**
     * Constructs an empty instance
     */
    public RegistrationRequest(FastFedConfiguration configuration) {
        super(configuration);
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
        allProfiles.addAll(authenticationProfiles);
        allProfiles.addAll(provisioningProfiles);
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
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder();
        builder.putAll(super.toJson());
        builder.put(JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        builder.put(JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
        return builder.build();
    }

    /**
     * Parses and validates a JWT and then constructs an instance of this class from the contents of the token.
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jwt Java Web Token in compact serialization form
     * @return RegistrationRequest
     * @throws InvalidMetadataException if the JWT is malformed or missing content
     * @throws FastFedSecurityException if the JWT signature is invalid or expired
     */
    public static RegistrationRequest fromJwt(FastFedConfiguration configuration,
                                              String jwt)
        throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jwt, "jwt must not be null");
        RegistrationRequest registrationRequest = new RegistrationRequest(configuration);
        // TODO - Deserialize and validate the jwt
        String json = jwt;
        registrationRequest.hydrateAndValidate(json);
        return registrationRequest;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        hydrateExtensions(json, Profile.ExtensionType.RegistrationRequest);
        setAuthenticationProfiles( json.getStringSet(JSONMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getStringSet(JSONMember.PROVISIONING_PROFILES));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateExtensions(errorAccumulator, getAllProfiles(), Profile.ExtensionType.RegistrationRequest);
        validateOptionalStringCollection(errorAccumulator, JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
    }

}
