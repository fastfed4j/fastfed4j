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

    @Override
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder();
        builder.putAll(super.toJson());
        builder.put(JSONMember.FASTFED_HANDSHAKE_FINALIZE_URI, handshakeFinalizeUri);
        return builder.build();
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
        hydrateExtensions(json, Profile.ExtensionType.RegistrationResponse);
        setHandshakeFinalizeUri( json.getString(JSONMember.FASTFED_HANDSHAKE_FINALIZE_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateExtensions(errorAccumulator, enabledProfiles.getAllProfiles(), Profile.ExtensionType.RegistrationResponse);
        validateOptionalUrl(errorAccumulator, JSONMember.SCHEMA_GRAMMAR, handshakeFinalizeUri);
    }

}
