package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.*;
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
    private final Map<String, Metadata> registrationRequestExtensions = new HashMap<>();

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
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public IdentityProvider(IdentityProvider other) {
        super(other);
        this.jwksUri = other.jwksUri;
        this.handshakeStartUri = other.handshakeStartUri;
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
     * When using the EnterpriseSAML profile, this method provides convenient access to SAML Metadata URI
     * for the Identity Provider.
     * @return samlMetadataUri, or null if the EnterpriseSAML profile is not in use.
     */
    public String getEnterpriseSamlMetadataUri() {
        String returnVal = null;
        EnterpriseSAML.RegistrationRequestExtension samlExtension =
                (EnterpriseSAML.RegistrationRequestExtension)getRegistrationRequestExtension(AuthenticationProfile.ENTERPRISE_SAML.toString());
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
        EnterpriseSCIM.RegistrationRequestExtension scimExtension =
                (EnterpriseSCIM.RegistrationRequestExtension)getRegistrationRequestExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());
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
        EnterpriseSCIM.RegistrationRequestExtension scimExtension =
                (EnterpriseSCIM.RegistrationRequestExtension)getRegistrationRequestExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());
        if (scimExtension != null) {
            returnVal = scimExtension.getProviderAuthenticationMethods();
        }
        return returnVal;
    }

    /**
     * When using the Enterprise SCIM profile with OAuth JWT authentication, this method provides convenient access
     * to the OAuth2 JWT client metadata.
     * @return Oauth2JwtClientMetadata if the client supports OAuth JWT with SCIM, otherwise null
     */
    public Oauth2JwtClientMetadata getEnterpriseScimOauth2JwtClient() {
        EnterpriseSCIM.RegistrationRequestExtension scimExtension =
                (EnterpriseSCIM.RegistrationRequestExtension)getRegistrationRequestExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());

        if (scimExtension == null || !scimExtension.getProviderAuthenticationMethods().supportsOauth2Jwt()) {
            return null;
        }

        return scimExtension.getProviderAuthenticationMethods().getOauth2JwtClient();
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addRegistrationRequestExtension(String profileUrn, Metadata metadata) {
        this.registrationRequestExtensions.put(profileUrn, metadata);
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method tests if extended metadata exists for a particular profile.
     * @param profileUrn urn of the profile
     * @return returns true if extended metadata exists for the profile
     */
    public boolean hasRegistrationRequestExtension(String profileUrn) {
        return this.registrationRequestExtensions.containsKey(profileUrn);
    }

    /**
     * As part of the FastFed Handshake, the Identity Provider may include additional information into the
     * Registration Request message depending on which authentication and provisioning profiles are in use.
     * This method returns the additional information from the contract for a given profile.
     * @param profileUrn urn of the profile
     * @return extended metadata for the profile
     */
    public Metadata getRegistrationRequestExtension(String profileUrn) {
        return this.registrationRequestExtensions.get(profileUrn);
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.IDENTITY_PROVIDER);
        super.hydrateFromJson(json);
        setJwksUri( json.getString(JSONMember.JWKS_URI));
        setHandshakeStartUri( json.getString(JSONMember.FASTFED_HANDSHAKE_START_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JSONMember.JWKS_URI, jwksUri);
        validateRequiredUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_START_URI, handshakeStartUri);
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
}
