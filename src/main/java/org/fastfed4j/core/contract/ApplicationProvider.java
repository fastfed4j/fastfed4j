package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.profile.Profile;
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
    private Map<String, Metadata> applicationProviderMetadataExtensions = new HashMap<>();
    private Map<String, Metadata> registrationResponseExtensions = new HashMap<>();

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
        for (Map.Entry<String, Metadata> entry : metadata.getAllMetadataExtensions().entrySet()) {
            this.addApplicationProviderMetadataExtension(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public ApplicationProvider(ApplicationProvider other) {
        super(other);
        this.handshakeRegisterUri = other.handshakeRegisterUri;
        this.handshakeFinalizeUri = other.handshakeFinalizeUri;
        this.applicationProviderMetadataExtensions = cloneExtensions(other.applicationProviderMetadataExtensions);
        this.registrationResponseExtensions = cloneExtensions(other.registrationResponseExtensions);
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
     * When using the EnterpriseSAML profile, this method provides convenient access to all the
     * extended values in the ApplicationProviderMetadata.
     * @return ApplicationProviderMetadataExtension for the EnterpriseSAML profile
     */
    public EnterpriseSAML.ApplicationProviderMetadataExtension getEnterpriseSamlApplicationProviderMetadataExtension() {
        return (EnterpriseSAML.ApplicationProviderMetadataExtension)getApplicationProviderMetadataExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to all the
     * extended values in the ApplicationProviderMetadata.
     * @return ApplicationProviderMetadataExtension for the EnterpriseSCIM profile
     */
    public EnterpriseSCIM.ApplicationProviderMetadataExtension getEnterpriseScimApplicationProviderMetadataExtension() {
        return (EnterpriseSCIM.ApplicationProviderMetadataExtension)getApplicationProviderMetadataExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to all the
     * extended values in the RegistrationResponse.
     * @return RegistrationResponse for the EnterpriseSAML profile
     */
    public EnterpriseSAML.RegistrationResponseExtension getEnterpriseSamlRegistrationResponseExtension() {
        return (EnterpriseSAML.RegistrationResponseExtension)getRegistrationResponseExtension(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * When using the EnterpriseSCIM profile, this method provides convenient access to all the
     * extended values in the RegistrationResponse.
     * @return RegistrationResponse for the EnterpriseSCIM profile
     */
    public EnterpriseSCIM.RegistrationResponseExtension getEnterpriseScimRegistrationResponseExtension() {
        return (EnterpriseSCIM.RegistrationResponseExtension)getRegistrationResponseExtension(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to the User Attribute
     * to be populated in the SAML Subject, as defined in section 3.1.2 and section 4 of the FastFed
     * Enterprise SAML Profile specification.
     * @return userAttribute, or null if the EnterpriseSAML profile is not in use.
     */
    public UserAttribute getEnterpriseSamlSubject() {
        UserAttribute returnVal = null;
        EnterpriseSAML.ApplicationProviderMetadataExtension samlExtension = getEnterpriseSamlApplicationProviderMetadataExtension();
        if (samlExtension != null) {
            returnVal = samlExtension.getSamlSubject();
        }
        return returnVal;
    }

    /**
     * When using the EnterpriseSAML profile, this method provides convenient access to the Desired Attributes
     * to be included in the SAML messages, as defined in section 3.1.2 and section 4 of the FastFed
     * Enterprise SAML Profile specification.
     * @return desiredAttributes, or null if the EnterpriseSAML profile is not in use.
     */
    public DesiredAttributes getEnterpriseSamlDesiredAttributes() {
        DesiredAttributes returnVal = null;
        EnterpriseSAML.ApplicationProviderMetadataExtension samlExtension = getEnterpriseSamlApplicationProviderMetadataExtension();
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
        EnterpriseSAML.RegistrationResponseExtension samlExtension = getEnterpriseSamlRegistrationResponseExtension();
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
        EnterpriseSCIM.ApplicationProviderMetadataExtension scimExtension = getEnterpriseScimApplicationProviderMetadataExtension();
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
        EnterpriseSCIM.RegistrationResponseExtension scimExtension = getEnterpriseScimRegistrationResponseExtension();
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
        EnterpriseSCIM.RegistrationResponseExtension scimExtension = getEnterpriseScimRegistrationResponseExtension();
        if (scimExtension != null) {
            returnVal = scimExtension.getProviderAuthenticationProtocolUrn();
        }
        return returnVal;
    }

    /**
     * When using the Enterprise SCIM profile with OAuth Jwt authentication, this method provides convenient access
     * to the OAuth2 Jwt metadata of the SCIM service.
     * @return Oauth2JwtServiceMetadata
     */
    public Oauth2JwtServiceMetadata getEnterpriseScimOauth2JwtService() {
        EnterpriseSCIM.RegistrationResponseExtension scimExtension = getEnterpriseScimRegistrationResponseExtension();
        if (scimExtension == null || scimExtension.getProviderAuthenticationProtocolUrn() != ProviderAuthenticationProtocol.OAUTH2_JWT) {
            return null;
        }
        return (Oauth2JwtServiceMetadata)scimExtension.getProviderAuthenticationMethod();
    }

    /**
     * The Application Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method tests if any extended metadata has been set.
     * @return returns true if extended metadata exists
     */
    public boolean hasApplicationProviderMetadataExtensions() {
        return !applicationProviderMetadataExtensions.isEmpty();
    }

    /**
     * The Application Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method tests if extended metadata exists for a particular profile.
     * @param profileUrn urn of the profile
     * @return returns true if extended metadata exists for the profile
     */
    public boolean hasApplicationProviderMetadataExtension(String profileUrn) {
        return applicationProviderMetadataExtensions.containsKey(profileUrn);
    }

    /**
     * The Application Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addApplicationProviderMetadataExtension(String profileUrn, Metadata metadata) {
        applicationProviderMetadataExtensions.put(profileUrn, metadata);
    }

    /**
     * The Application Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method returns the additional information from the contract for a given profile.
     * @param profileUrn urn of the profile
     * @return extended metadata for the profile
     */
    public Metadata getApplicationProviderMetadataExtension(String profileUrn) {
        return applicationProviderMetadataExtensions.get(profileUrn);
    }

    /**
     * The Application Provider may include additional information into it's hosted metadata files,
     * depending on which authentication and provisioning profiles are in use.
     * This method returns a table of all the extensions which have been set, keyed by the URN of the profile extension.
     * @return table of extended metadata for all profiles, keyed by profile URNs
     */
    public Map<String,Metadata> getAllApplicationProviderMetadataExtensions() {
        return applicationProviderMetadataExtensions;
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method captures the additional information into the contract for a given profile.
     * @param profileUrn urn of the profile
     * @param metadata contents of the metadata extension defined by the profile
     */
    public void addRegistrationResponseExtension(String profileUrn, Metadata metadata) {
        registrationResponseExtensions.put(profileUrn, metadata);
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method tests if any extended metadata has been set.
     * @return returns true if extended metadata exists
     */
    public boolean hasRegistrationResponseExtensions() {
        return !registrationResponseExtensions.isEmpty();
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
        return registrationResponseExtensions.get(profileUrn);
    }

    /**
     * As part of the FastFed Handshake, the Application Provider may include additional information into the
     * Registration Response message depending on which authentication and provisioning profiles are in use.
     * This method returns a table of all the extensions which have been set, keyed by the URN of the profile extension.
     * @return table of extended metadata for all profiles, keyed by profile URNs
     */
    public Map<String,Metadata> getAllRegistrationResponseExtensions() {
        return registrationResponseExtensions;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.APPLICATION_PROVIDER);
        builder.putAll(super.toJson());
        builder.put(JsonMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
        builder.put(JsonMember.FASTFED_HANDSHAKE_FINALIZE_URI, handshakeFinalizeUri);

        if (hasApplicationProviderMetadataExtensions()) {
            JsonObject.Builder extensionBuilder = new JsonObject.Builder(JsonMember.APPLICATION_PROVIDER_METADATA_EXTENSIONS);
            for (Metadata extension : applicationProviderMetadataExtensions.values()) {
                extensionBuilder.putAll(extension.toJson());
            }
            builder.putAll(extensionBuilder.build());
        }

        if (hasRegistrationResponseExtensions()) {
            JsonObject.Builder extensionBuilder = new JsonObject.Builder(JsonMember.REGISTRATION_RESPONSE_EXTENSIONS);
            for (Metadata extension : registrationResponseExtensions.values()) {
                extensionBuilder.putAll(extension.toJson());
            }
            builder.putAll(extensionBuilder.build());
        }

        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.APPLICATION_PROVIDER);
        super.hydrateFromJson(json);

        //Unlike most other metadata classes, this one has 2 points of extension. This is because it aggregates
        //several different artifacts together, and this includes the extended data for both
        //ApplicationProviderMetadata and RegistrationResponse messages.
        //As a result, both entities require hydration.
        hydrateExtensions(
                json.getObject(JsonMember.APPLICATION_PROVIDER_METADATA_EXTENSIONS),
                applicationProviderMetadataExtensions,
                Profile.ExtensionType.ApplicationProviderMetadata);

        hydrateExtensions(
                json.getObject(JsonMember.REGISTRATION_RESPONSE_EXTENSIONS),
                registrationResponseExtensions,
                Profile.ExtensionType.RegistrationResponse);

        //Hydrate the remaining attributes.
        setHandshakeRegisterUri( json.getString(JsonMember.FASTFED_HANDSHAKE_REGISTER_URI));
        setHandshakeFinalizeUri( json.getString(JsonMember.FASTFED_HANDSHAKE_FINALIZE_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JsonMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
        validateOptionalUrl(errorAccumulator, JsonMember.FASTFED_HANDSHAKE_FINALIZE_URI, handshakeFinalizeUri);
    }

    public void validateExtensions(ErrorAccumulator errorAccumulator, EnabledProfiles enabledProfiles) {
        if (enabledProfiles == null)
            return;

        validateExtensions(
                errorAccumulator,
                getAllApplicationProviderMetadataExtensions(),
                enabledProfiles.getAllProfiles(),
                Profile.ExtensionType.ApplicationProviderMetadata);

        validateExtensions(
                errorAccumulator,
                getAllRegistrationResponseExtensions(),
                enabledProfiles.getAllProfiles(),
                Profile.ExtensionType.RegistrationResponse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProvider that = (ApplicationProvider) o;
        return Objects.equals(handshakeRegisterUri, that.handshakeRegisterUri) &&
                Objects.equals(handshakeFinalizeUri, that.handshakeFinalizeUri) &&
                Objects.equals(applicationProviderMetadataExtensions, that.applicationProviderMetadataExtensions) &&
                Objects.equals(registrationResponseExtensions, that.registrationResponseExtensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), handshakeRegisterUri, handshakeFinalizeUri, applicationProviderMetadataExtensions, registrationResponseExtensions);
    }

    // The following methods for handling extended metadata are defined in the base Metadata class.
    // However, since this implementation uses 2 different types of metadata extensions,
    // the default methods are disabled in favor of the more specific versions in this class.
    @Override
    public void addMetadataExtension(String profileUrn, Metadata ext) {
        throw new UnsupportedOperationException(
                "Use either addApplicationProviderMetadataExtension() or addRegistrationResponseExtension()");
    }

    @Override
    public Map<String, Metadata> getAllMetadataExtensions() {
        throw new UnsupportedOperationException(
                "Use either getAllApplicationProviderMetadataExtensions() or getAllRegistrationResponseExtensions()");
    }

    @Override
    public boolean hasMetadataExtension(String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either hasApplicationProviderMetadataExtension() or hasRegistrationResponseExtension()");
    }

    @Override
    public Metadata getMetadataExtension(String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either getApplicationProviderMetadataExtension() or getRegistrationResponseExtension()");
    }

    @Override
    public <T> T getMetadataExtension(Class<T> type, String profileUrn) {
        throw new UnsupportedOperationException(
                "Use either getApplicationProviderMetadataExtension() or getRegistrationResponseExtension()");
    }
}
