package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.scim.enterprise.ProviderAuthenticationMethods;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains the subset of Identity Provider information that is copied into a contract for long-term retention.
 */
public class IdentityProvider extends Provider {

    private String jwksUri;
    private String handshakeStartUri;
    private Map<String, Metadata> identityProviderMetadataExtensions = new HashMap<>();
    private Map<String, Metadata> registrationRequestExtensions = new HashMap<>();

    /**
     * Constructs an empty instance
     */
    public IdentityProvider(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Constructs an instance from the source Identity Provider Metadata, extracting the subset of information
     * necessary for long-term retention in a contract.
     * @param metadata the IdentityProviderMetadata to extract from
     */
    public IdentityProvider(IdentityProviderMetadata metadata) {
        super(metadata);
        this.jwksUri = metadata.getJwksUri();
        this.handshakeStartUri = metadata.getHandshakeStartUri();
        for (Map.Entry<String, Metadata> entry : metadata.getAllMetadataExtensions().entrySet()) {
            this.addIdentityProviderMetadataExtension(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public IdentityProvider(IdentityProvider other) {
        super(other);
        this.jwksUri = other.jwksUri;
        this.handshakeStartUri = other.handshakeStartUri;
        if (other.identityProviderMetadataExtensions != null)
            this.identityProviderMetadataExtensions = cloneExtensions(other.identityProviderMetadataExtensions);
        if (other.registrationRequestExtensions != null)
            this.registrationRequestExtensions = cloneExtensions(other.registrationRequestExtensions);
    }

    /**
     * Gets the JWKS URI for the Identity Provider
     * @return jwksUri
     */
    public String getJwksUri() {
        return jwksUri;
    }

    /**
     * Sets the JWKS URI for the Identity Provider
     * @param jwks_uri uri
     */
    public void setJwksUri(String jwks_uri) {
        this.jwksUri = jwks_uri;
    }

    /**
     * Gets the Handshake Start URI for the Identity Provider
     * @return handshakeStartUri
     */
    public String getHandshakeStartUri() {
        return handshakeStartUri;
    }

    /**
     * Sets the Handshake Start URI for the Identity Provider
     * @param handshakeStartUri uri
     */
    public void setHandshakeStartUri(String handshakeStartUri) {
        this.handshakeStartUri = handshakeStartUri;
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to all the
     * extended values in the RegistrationRequest.
     * @return ApplicationProviderMetadataExtension for the EnterpriseSAML profile
     */
    public EnterpriseSAML.RegistrationRequestExtension getEnterpriseSamlRegistrationRequestExtension() {
        return (EnterpriseSAML.RegistrationRequestExtension)getRegistrationRequestExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to all the
     * extended values in the RegistrationRequest.
     * @return ApplicationProviderMetadataExtension for the EnterpriseSCIM profile
     */
    public EnterpriseSCIM.RegistrationRequestExtension getEnterpriseScimRegistrationRequestExtension() {
        return (EnterpriseSCIM.RegistrationRequestExtension)getRegistrationRequestExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to SAML Metadata URI
     * for the Identity Provider.
     * @return samlMetadataUri, or null if the EnterpriseSAML profile is not in use.
     */
    public String getEnterpriseSamlMetadataUri() {
        String returnVal = null;
        EnterpriseSAML.RegistrationRequestExtension samlExtension = getEnterpriseSamlRegistrationRequestExtension();
        if (samlExtension != null) {
            returnVal = samlExtension.getSamlMetadataUri();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to the contact information of
     * the SCIM provisioning client.
     * May differ from the contact information of the Identity Provider when SCIM provisioning has been delegated to a
     * distinct subsystem or an external provider.
     * @return providerContactInformation, or null if the EnterpriseSCIM profile is not in use
     */
    public ProviderContactInformation getEnterpriseScimClientContactInformation() {
        ProviderContactInformation returnVal = null;
        EnterpriseSCIM.RegistrationRequestExtension scimExtension = getEnterpriseScimRegistrationRequestExtension();
        if (scimExtension != null) {
            returnVal = scimExtension.getProviderContactInformation();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to the authentication methods
     * supported by the SCIM client.
     * @return providerAuthenticationMethods, or null if the EnterpriseSCIM profile is not in use
     */
    public ProviderAuthenticationMethods getEnterpriseScimClientAuthenticationMethods() {
        ProviderAuthenticationMethods returnVal = null;
        EnterpriseSCIM.RegistrationRequestExtension scimExtension = getEnterpriseScimRegistrationRequestExtension();
        if (scimExtension != null) {
            returnVal = scimExtension.getProviderAuthenticationMethods();
        }
        return returnVal;
    }

    /**
     * When using the Enterprise SCIM profile with OAuth Jwt authentication, this method provides convenient access
     * to the OAuth2 Jwt client metadata.
     * @return Oauth2JwtClientMetadata if the client supports OAuth Jwt with SCIM, otherwise null
     */
    public Oauth2JwtClientMetadata getEnterpriseScimOauth2JwtClient() {
        EnterpriseSCIM.RegistrationRequestExtension scimExtension = getEnterpriseScimRegistrationRequestExtension();
        if (scimExtension == null || !scimExtension.getProviderAuthenticationMethods().supportsOauth2Jwt()) {
            return null;
        }
        return scimExtension.getProviderAuthenticationMethods().getOauth2JwtClient();
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method tests if any extended metadata has been set.
     * @return returns true if extended metadata exists
     */
    public boolean hasIdentityProviderMetadataExtensions() {
        return !identityProviderMetadataExtensions.isEmpty();
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method tests if extended metadata exists for a particular profile.
     * @param profileUrn urn of the profile
     * @return returns true if extended metadata exists for the profile
     */
    public boolean hasIdentityProviderMetadataExtension(String profileUrn) {
        return identityProviderMetadataExtensions.containsKey(profileUrn);
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addIdentityProviderMetadataExtension(String profileUrn, Metadata metadata) {
        identityProviderMetadataExtensions.put(profileUrn, metadata);
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method returns the additional information from the contract for a given profile.
     * @param profileUrn urn of the profile
     * @return extended metadata for the profile
     */
    public Metadata getIdentityProviderMetadataExtension(String profileUrn) {
        return identityProviderMetadataExtensions.get(profileUrn);
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method returns a table of all the extensions which have been set, keyed by the URN of the profile extension.
     * @return table of extended metadata for all profiles, keyed by profile URNs
     */
    public Map<String,Metadata> getAllIdentityProviderMetadataExtensions() {
        return identityProviderMetadataExtensions;
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addRegistrationRequestExtension(String profileUrn, Metadata metadata) {
        registrationRequestExtensions.put(profileUrn, metadata);
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method tests if any extended metadata has been set.
     * @return returns true if extended metadata exists
     */
    public boolean hasRegistrationRequestExtensions() {
        return !registrationRequestExtensions.isEmpty();
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method tests if extended metadata exists for a particular profile.
     * @param profileUrn urn of the profile
     * @return returns true if extended metadata exists for the profile
     */
    public boolean hasRegistrationRequestExtension(String profileUrn) {
        return registrationRequestExtensions.containsKey(profileUrn);
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method returns the additional information from the contract for a given profile.
     * @param profileUrn urn of the profile
     * @return extended metadata for the profile
     */
    public Metadata getRegistrationRequestExtension(String profileUrn) {
        return registrationRequestExtensions.get(profileUrn);
    }

    /**
     * The Identity Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method returns a table of all the extensions which have been set, keyed by the URN of the profile extension.
     * @return table of extended metadata for all profiles, keyed by profile URNs
     */
    public Map<String,Metadata> getAllRegistrationRequestExtensions() {
        return registrationRequestExtensions;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.IDENTITY_PROVIDER);
        builder.putAll(super.toJson());
        builder.put(JsonMember.JWKS_URI, jwksUri);
        builder.put(JsonMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);

        if (hasIdentityProviderMetadataExtensions()) {
            JsonObject.Builder extensionBuilder = new JsonObject.Builder(JsonMember.IDENTITY_PROVIDER_METADATA_EXTENSIONS);
            for (Metadata obj : identityProviderMetadataExtensions.values()) {
                extensionBuilder.putAll(obj.toJson());
            }
            builder.putAll(extensionBuilder.build());
        }

        if (hasRegistrationRequestExtensions()) {
            JsonObject.Builder extensionBuilder = new JsonObject.Builder(JsonMember.REGISTRATION_REQUEST_EXTENSIONS);
            for (Metadata obj : registrationRequestExtensions.values()) {
                extensionBuilder.putAll(obj.toJson());
            }
            builder.putAll(extensionBuilder.build());
        }

        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        //Unlike most other metadata classes, this one has 2 points of extension. This is because it aggregates
        //several different artifacts together, and this includes the extended data for both
        //IdentityProviderMetadata and RegistrationRequest messages.
        //As a result, both entities require hydration
        hydrateExtensions(
                json.getObject(JsonMember.IDENTITY_PROVIDER_METADATA_EXTENSIONS),
                identityProviderMetadataExtensions,
                Profile.ExtensionType.IdentityProviderMetadata);

        hydrateExtensions(
                json.getObject(JsonMember.REGISTRATION_REQUEST_EXTENSIONS),
                registrationRequestExtensions,
                Profile.ExtensionType.RegistrationRequest);

        //Hydrate the remaining attributes.
        setJwksUri( json.getString(JsonMember.JWKS_URI));
        setHandshakeStartUri( json.getString(JsonMember.FASTFED_HANDSHAKE_START_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JsonMember.JWKS_URI, jwksUri);
        validateRequiredUrl(errorAccumulator, JsonMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);
    }

    public void validateExtensions(ErrorAccumulator errorAccumulator, EnabledProfiles enabledProfiles) {
        if (enabledProfiles == null)
            return;

        validateExtensions(
                errorAccumulator,
                identityProviderMetadataExtensions,
                enabledProfiles.getAllProfiles(),
                Profile.ExtensionType.IdentityProviderMetadata);

        validateExtensions(
                errorAccumulator,
                registrationRequestExtensions,
                enabledProfiles.getAllProfiles(),
                Profile.ExtensionType.RegistrationRequest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IdentityProvider that = (IdentityProvider) o;
        return jwksUri.equals(that.jwksUri) &&
                handshakeStartUri.equals(that.handshakeStartUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwksUri, handshakeStartUri);
    }

    // The following methods for handling extended metadata are defined in the base Metadata class.
    // However, since this implementation uses 2 different types of metadata extensions,
    // the default methods are disabled in favor of the more specific versions in this class.
    @Override
    public void addMetadataExtension(String profileUrn, Metadata ext) {
        throw new UnsupportedOperationException(
                "Use either addIdentityProviderMetadataExtension() or addRegistrationRequestExtension()");
    }

    @Override
    public Map<String, Metadata> getAllMetadataExtensions() {
        throw new UnsupportedOperationException(
                "Use either getAllIdentityProviderMetadataExtensions() or getAllRegistrationRequestExtensions()");
    }

    @Override
    public boolean hasMetadataExtension(String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either hasIdentityProviderMetadataExtension() or hasRegistrationRequestExtension()");
    }

    @Override
    public Metadata getMetadataExtension(String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either getIdentityProviderMetadataExtension() or getRegistrationRequestExtension()");
    }

    @Override
    public <T> T getMetadataExtension(Class<T> type, String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either getIdentityProviderMetadataExtension() or getRegistrationRequestExtension()");
    }
}
