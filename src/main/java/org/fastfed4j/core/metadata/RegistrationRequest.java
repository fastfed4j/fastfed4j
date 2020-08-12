package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
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
        hydrateAttributes(json);
        hydrateExtensions(json);
    }

    private void hydrateAttributes(JSONObject json) {
        setAuthenticationProfiles( json.getStringSet(JSONMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getStringSet(JSONMember.PROVISIONING_PROFILES));
    }

    private void hydrateExtensions(JSONObject json) {
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        for (String urn : registry.getAllUrns()) {
            Profile profile = registry.getByUrn(urn);
            if (profile.requiresRegistrationRequestExtension() && json.containsKey(urn)) {
                Metadata extension = registry.getByUrn(urn).newRegistrationRequestExtension(getFastFedConfiguration());
                if (extension == null) {
                    // This would occur from an implementation bug if a Profile indicates that a particular
                    // extension is required, but then neglects to implement a handler for the extension.
                    throw new RuntimeException(
                            "Missing implementation of RegistrationRequestExtension for profile '" + urn + "'");
                }
                extension.hydrateFromJson(json.getObject(urn));
                addMetadataExtension(urn, extension);
            }
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateAttributes(errorAccumulator);
        validateExtensions(errorAccumulator);
    }

    private void validateAttributes(ErrorAccumulator errorAccumulator) {
        validateOptionalStringCollection(errorAccumulator, JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
    }

    private void validateExtensions(ErrorAccumulator errorAccumulator) {
        for (String profileUrn : authenticationProfiles) {
            validateProfile(errorAccumulator, profileUrn);
        }
        for (String profileUrn : provisioningProfiles) {
            validateProfile(errorAccumulator, profileUrn);
        }
    }

    private void validateProfile(ErrorAccumulator errorAccumulator, String profileUrn) {
        Profile profile = getFastFedConfiguration().getProfileRegistry().getByUrn(profileUrn);

        if (profile == null) {
            errorAccumulator.add("Unrecognized profile: " + profileUrn);
            return;
        }

        if (profile.requiresRegistrationRequestExtension() && !hasMetadataExtension(profileUrn)) {
            errorAccumulator.add("Missing value for '" + profileUrn + "'");
            return;
        }

        if (hasMetadataExtension(profileUrn)) {
            getMetadataExtension(profileUrn).validate(errorAccumulator);
        }
    }
}
