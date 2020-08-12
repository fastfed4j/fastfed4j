package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a Registration Response message, as defined in section 7.2.3.3 of the FastFed Core specification.
 */
public class RegistrationResponse extends Metadata {

    EnabledProfiles enabledProfiles;
    String handshakeFinalizeUri;

    /**
     * Constructor
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param enabledProfiles the authentication and provisioning profiles that were enabled by the Identity Provider
     *                        in the preceding Registration Request. This is necessary to correctly validate
     *                        the Registration Response.
     */
    public RegistrationResponse(FastFedConfiguration configuration, EnabledProfiles enabledProfiles) {
        super(configuration);
        this.enabledProfiles = enabledProfiles;
    }

    /**
     * Gets the handshake finalize uri to perform section 7.2.4.1 of the FastFed Core specification
     * @return uri
     */
    public Optional<String> getHandshakeFinalizeUri() {
        return Optional.ofNullable(handshakeFinalizeUri);
    }

    /**
     * Sets the handshake finalize uri to perform section 7.2.4.1 of the FastFed Core specification
     */
    public void setHandshakeFinalizeUri(String handshakeFinalizeUri) {
        this.handshakeFinalizeUri = handshakeFinalizeUri;
    }

    /**
     * Convenience method to get the extensions defined by the EnterpriseSAML profile.
     * @return EnterpriseSAML extensions, or null if the EnterpriseSAML profile is not in use.
     */
    public EnterpriseSAML.RegistrationResponseExtension getEnterpriseSamlExtension() {
        return getMetadataExtension(EnterpriseSAML.RegistrationResponseExtension.class, AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * Convenience method to get the extensions defined by the EnterpriseSCIM profile.
     * @return EnterpriseSCIM extensions, or null if the EnterpriseSCIM profile is not in use.
     */
    public EnterpriseSCIM.RegistrationResponseExtension getEnterpriseScimExtension() {
        return getMetadataExtension(EnterpriseSCIM.RegistrationResponseExtension.class, ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * Map a JSON document into an instance of this class
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jsonString json document
     * @param enabledProfiles used during validation to ensure the appropriate extended attributes are defined for
     *                        all of the enabled authentication and provisioning profiles
     * @return RegistrationResponse
     * @throws InvalidMetadataException
     */
    public static RegistrationResponse fromJson(FastFedConfiguration configuration,
                                                String jsonString,
                                                EnabledProfiles enabledProfiles)
            throws InvalidMetadataException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        Objects.requireNonNull(enabledProfiles, "enabledProfiles must not be null");
        RegistrationResponse registrationResponse = new RegistrationResponse(configuration, enabledProfiles);
        registrationResponse.hydrateAndValidate(jsonString);
        return registrationResponse;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        hydrateAttributes(json);
        hydrateExtensions(json);
    }

    private void hydrateAttributes(JSONObject json) {
        setHandshakeFinalizeUri( json.getString(JSONMember.FASTFED_HANDSHAKE_FINALIZE_URI));
    }

    private void hydrateExtensions(JSONObject json) {
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        for (String urn : registry.getAllUrns()) {
            Profile profile = registry.getByUrn(urn);
            if (profile.requiresRegistrationResponseExtension() && json.containsKey(urn)) {
                Metadata extension = registry.getByUrn(urn).newRegistrationResponseExtension(getFastFedConfiguration());
                if (extension == null) {
                    // This would occur from an implementation bug if a Profile indicates that a particular
                    // extension is required, but then neglects to implement a handler for the extension.
                    throw new RuntimeException(
                            "Missing implementation of RegistrationResponseExtension for profile '" + urn + "'");
                }
                extension.hydrateFromJson(json.getObject(urn));
                addMetadataExtension(urn, extension);
            }
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateAttributes(errorAccumulator);
        validateExtensions(errorAccumulator);
    }

    private void validateAttributes(ErrorAccumulator errorAccumulator) {
        validateOptionalUrl(errorAccumulator, JSONMember.SCHEMA_GRAMMAR, handshakeFinalizeUri);
    }

    private void validateExtensions(ErrorAccumulator errorAccumulator) {
        // In this validation step, we only examine the profiles that were chosen to be enabled,
        // as opposed to iterating over all known profiles in the FastFedConfiguration.
        // The reason is because, if a profile was not used, then it is OK & correct to not include any
        // extended metadata for it in the Registration message, and hence we should not signal a validation
        // exception due to the absence of the extension.
        for (String profileUrn : enabledProfiles.getAuthenticationProfiles()) {
            validateProfile(errorAccumulator, profileUrn);
        }
        for (String profileUrn : enabledProfiles.getProvisioningProfiles()) {
            validateProfile(errorAccumulator, profileUrn);
        }
    }

    private void validateProfile(ErrorAccumulator errorAccumulator, String profileUrn) {
        Profile profile = getFastFedConfiguration().getProfileRegistry().getByUrn(profileUrn);

        if (profile.requiresRegistrationResponseExtension() && !hasMetadataExtension(profileUrn)) {
            errorAccumulator.add("Missing value for '" + profileUrn + "'");
            return;
        }

        if (hasMetadataExtension(profileUrn)) {
            getMetadataExtension(profileUrn).validate(errorAccumulator);
        }
    }
}
