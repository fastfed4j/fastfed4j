package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.json.Jwt;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.ProviderContactInformation;

import java.util.Objects;

/**
 * Represents the extensions to the Registration Request messages defined by
 * section 3.2.1 of the FastFed Enterprise SCIM Profile.
 */
public class RegistrationRequestExtension extends Jwt {
    private ProviderContactInformation providerContactInformation;
    private ProviderAuthenticationMethods providerAuthenticationMethods;

    /**
     * Constructs an empty instance
     */
    public RegistrationRequestExtension(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public RegistrationRequestExtension(RegistrationRequestExtension other) {
        super(other);
        if (other.providerContactInformation != null)
            this.providerContactInformation = new ProviderContactInformation(other.providerContactInformation);
        if (other.providerAuthenticationMethods != null)
            this.providerAuthenticationMethods = new ProviderAuthenticationMethods(other.providerAuthenticationMethods);
    }

    /**
     * Get the Provider Contact Information for the SCIM client. This may be the same contact information as for
     * the Identity Provider, but can also vary if SCIM provisioning is delegated to a distinct subsystem or
     * external system.
     * @return ProviderContactInformation
     */
    public ProviderContactInformation getProviderContactInformation() {
        return providerContactInformation;
    }

    /**
     * Set the Provider Contact Information for the SCIM client.
     * @param providerContactInformation ProviderContactInformation
     */
    public void setProviderContactInformation(ProviderContactInformation providerContactInformation) {
        this.providerContactInformation = providerContactInformation;
    }

    /**
     * Get the SCIM authentication methods supported by the provider who will be acting as the SCIM client.
     * @return ProviderAuthenticationMethods
     */
    public ProviderAuthenticationMethods getProviderAuthenticationMethods() {
        return providerAuthenticationMethods;
    }

    /**
     * Set the SCIM authentication methods supported by the provider who will be acting as the SCIM client.
     * @param providerAuthenticationMethods ProviderAuthenticationMethods
     */
    public void setProviderAuthenticationMethods(ProviderAuthenticationMethods providerAuthenticationMethods) {
        this.providerAuthenticationMethods = providerAuthenticationMethods;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
        builder.putAll(super.toJson());
        if (providerContactInformation != null)
            builder.putAll(providerContactInformation.toJson());
        if (providerAuthenticationMethods != null)
            builder.putAll(providerAuthenticationMethods.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        JsonObject providerContactInformationJson = json.getObject(JsonMember.PROVIDER_CONTACT_INFORMATION);
        if (providerContactInformationJson != null) {
            ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
            providerContactInformation.hydrateFromJson(providerContactInformationJson);
            setProviderContactInformation(providerContactInformation);
        }

        JsonObject providerAuthenticationMethodsJson = json.getObject(JsonMember.PROVIDER_AUTHENTICATION_METHODS);
        if (providerAuthenticationMethodsJson != null) {
            ProviderAuthenticationMethods providerAuthenticationMethods = new ProviderAuthenticationMethods(getFastFedConfiguration());
            providerAuthenticationMethods.hydrateFromJson(providerAuthenticationMethodsJson);
            setProviderAuthenticationMethods(providerAuthenticationMethods);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JsonMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JsonMember.PROVIDER_AUTHENTICATION_METHODS, providerAuthenticationMethods);

        if (providerContactInformation != null) {
            providerContactInformation.validate(errorAccumulator);
        }

        if (providerAuthenticationMethods != null) {
            providerAuthenticationMethods.validate(errorAccumulator);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegistrationRequestExtension that = (RegistrationRequestExtension) o;
        return Objects.equals(providerContactInformation, that.providerContactInformation) &&
                Objects.equals(providerAuthenticationMethods, that.providerAuthenticationMethods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), providerContactInformation, providerAuthenticationMethods);
    }
}
