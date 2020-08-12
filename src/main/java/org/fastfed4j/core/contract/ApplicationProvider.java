package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.Oauth2JwtServiceMetadata;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains the subset of Application Provider information that is copied into a contract for long-term retention.
 */
public class ApplicationProvider extends Provider {

    private String handshakeRegisterUri;
    private String handshakeFinalizeUri;
    private final Map<String, Metadata> registrationResponseExtensions = new HashMap<>();

    /**
     * Constructs an empty instance
     */
    public ApplicationProvider(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Constructs an instance from the source Application Provider Metadata, extracting the subset of information
     * necessary for long-term retention in a contract.
     * @param metadata the ApplicationProviderMetadata to extract from
     */
    public ApplicationProvider(ApplicationProviderMetadata metadata) {
        super(metadata);
        this.handshakeRegisterUri = metadata.getHandshakeRegisterUri();
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public ApplicationProvider(ApplicationProvider other) {
        super(other);
        this.handshakeRegisterUri = other.handshakeRegisterUri;
    }

    /**
     * Gets the Handshake Register URI for the Application Provider
     * @return handshakeRegisterUri
     */
    public String getHandshakeRegisterUri() {
        return handshakeRegisterUri;
    }

    /**
     * Sets the Handshake Register URI for the Application Provider
     * @param handshakeRegisterUri uri
     */
    public void setHandshakeRegisterUri(String handshakeRegisterUri) {
        this.handshakeRegisterUri = handshakeRegisterUri;
    }

    /**
     * Tests if a Handshake Finalize URI was provided by the Application Provider
     * @return returns true if a handshake finalization URI was provided by the Application Provider
     */
    public boolean hasHandshakeFinalizeUri() {
        return (handshakeFinalizeUri != null && !handshakeFinalizeUri.isEmpty());
    }

    /**
     * Gets the Handshake Finalize URI for the Application Provider
     * @return handshakeFinalizeUri
     */
    public String getHandshakeFinalizeUri() {
        return handshakeFinalizeUri;
    }

    /**
     * Sets the Handshake Finalize URI for the Application Provider
     * @param handshakeFinalizeUri uri
     */
    public void setHandshakeFinalizeUri(String handshakeFinalizeUri) {
        this.handshakeFinalizeUri = handshakeFinalizeUri;
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to the Desired Attributes
     * to be included in the SAML messages, as defined in section 3.1.2 and section 4 of the FastFed
     * Enterprise SAML Profile specification.
     * @return desiredAttributes, or null if the EnterpriseSAML profile is not in use.
     */
    public DesiredAttributes getEnterpriseSamlDesiredAttributes() {
        DesiredAttributes returnVal = null;
        EnterpriseSAML.ApplicationProviderMetadataExtension samlExtension =
                getMetadataExtension(EnterpriseSAML.ApplicationProviderMetadataExtension.class, AuthenticationProfile.ENTERPRISE_SAML.toString());
        if (samlExtension != null) {
            returnVal = samlExtension.getDesiredAttributes();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to SAML Metadata URI
     * for the Application Provider.
     * @return samlMetadataUri, or null if the EnterpriseSAML profile is not in use.
     */
    public String getEnterpriseSamlMetadataUri() {
        String returnVal = null;
        EnterpriseSAML.RegistrationResponseExtension samlExtension =
                (EnterpriseSAML.RegistrationResponseExtension)getRegistrationResponseExtension(AuthenticationProfile.ENTERPRISE_SAML.toString());
        if (samlExtension != null) {
            returnVal = samlExtension.getSamlMetadataUri();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to the Desired Attributes
     * to be included in the SCIM messages, as defined in section 3.1.2 of the FastFed Enterprise SCIM Profile
     * specification.
     * @return desiredAttributes, or null if the EnterpriseSAML profile is not in use.
     */
    public DesiredAttributes getEnterpriseScimDesiredAttributes() {
        DesiredAttributes returnVal = null;
        EnterpriseSCIM.ApplicationProviderMetadataExtension scimExtension =
                getMetadataExtension(EnterpriseSCIM.ApplicationProviderMetadataExtension.class, ProvisioningProfile.ENTERPRISE_SCIM.toString());
        if (scimExtension != null) {
            returnVal = scimExtension.getDesiredAttributes();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to the SCIM server URI hosted
     * by the Application Provider.
     * @return scimServiceUri, or null if the EnterpriseSCIM profile is not in use
     */
    public String getEnterpriseScimServiceUri() {
        String returnVal = null;
        EnterpriseSCIM.RegistrationResponseExtension scimExtension =
                (EnterpriseSCIM.RegistrationResponseExtension)getRegistrationResponseExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());
        if (scimExtension != null) {
            returnVal = scimExtension.getScimServiceUri();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to the authentication methods
     * supported by the SCIM server.
     * @return providerAuthenticationMethods, or null if the EnterpriseSCIM profile is not in use
     */
    public ProviderAuthenticationProtocol getEnterpriseScimServiceAuthenticationProtocol() {
        ProviderAuthenticationProtocol returnVal = null;
        EnterpriseSCIM.RegistrationResponseExtension scimExtension =
                (EnterpriseSCIM.RegistrationResponseExtension)getRegistrationResponseExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());
        if (scimExtension != null) {
            returnVal = scimExtension.getProviderAuthenticationProtocolUrn();
        }
        return returnVal;
    }

    /**
     * When using the Enterprise SCIM profile with OAuth JWT authentication, this method provides convenient access
     * to the OAuth2 JWT metadata of the SCIM service.
     * @return Oauth2JwtServiceMetadata
     */
    public Oauth2JwtServiceMetadata getEnterpriseScimOauth2JwtService() {
        EnterpriseSCIM.RegistrationResponseExtension scimExtension =
                (EnterpriseSCIM.RegistrationResponseExtension)getRegistrationResponseExtension(ProvisioningProfile.ENTERPRISE_SCIM.toString());

        if (scimExtension == null || scimExtension.getProviderAuthenticationProtocolUrn() != ProviderAuthenticationProtocol.OAUTH2_JWT) {
            return null;
        }
        return (Oauth2JwtServiceMetadata)scimExtension.getProviderAuthenticationMethod();
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addRegistrationResponseExtension(String profileUrn, Metadata metadata) {
        this.registrationResponseExtensions.put(profileUrn, metadata);
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method tests if extended metadata exists for a particular profile.
     * @param profileUrn urn of the profile
     * @return returns true if extended metadata exists for the profile
     */
    public boolean hasRegistrationResponseExtension(String profileUrn) {
        return this.registrationResponseExtensions.containsKey(profileUrn);
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method returns the additional information from the contract for a given profile.
     * @param profileUrn urn of the profile
     * @return extended metadata for the profile
     */
    public Metadata getRegistrationResponseExtension(String profileUrn) {
        return this.registrationResponseExtensions.get(profileUrn);
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.APPLICATION_PROVIDER);
        super.hydrateFromJson(json);
        setHandshakeRegisterUri( json.getString(JSONMember.FASTFED_HANDSHAKE_REGISTER_URI));
        setHandshakeFinalizeUri( json.getString(JSONMember.FASTFED_HANDSHAKE_FINALIZE_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
        validateOptionalUrl(errorAccumulator, JSONMember.FASTFED_HANDSHAKE_FINALIZE_URI, handshakeFinalizeUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProvider that = (ApplicationProvider) o;
        return handshakeRegisterUri.equals(that.handshakeRegisterUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), handshakeRegisterUri);
    }
}
