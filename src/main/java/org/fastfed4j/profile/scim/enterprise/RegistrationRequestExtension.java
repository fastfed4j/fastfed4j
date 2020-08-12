package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.json.JWT;
import org.fastfed4j.core.metadata.ProviderContactInformation;

/**
 * Represents the extensions to the Registration Request messages defined by
 * section 3.2.1 of the FastFed Enterprise SCIM Profile.
 */
public class RegistrationRequestExtension extends JWT {
    private ProviderContactInformation providerContactInformation;
    private ProviderAuthenticationMethods providerAuthenticationMethods;

    /**
     * Constructs an empty instance
     */
    public RegistrationRequestExtension(FastFedConfiguration configuration) {
        super(configuration);
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
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
        providerContactInformation.hydrateFromJson(json.getObject(JSONMember.PROVIDER_CONTACT_INFORMATION));
        setProviderContactInformation(providerContactInformation);

        ProviderAuthenticationMethods providerAuthenticationMethods = new ProviderAuthenticationMethods(getFastFedConfiguration());
        providerAuthenticationMethods.hydrateFromJson(json.getObject(JSONMember.PROVIDER_AUTHENTICATION_METHODS));
        setProviderAuthenticationMethods(providerAuthenticationMethods);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JSONMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JSONMember.PROVIDER_AUTHENTICATION_METHODS, providerAuthenticationMethods);

        if (providerContactInformation != null) {
            providerContactInformation.validate(errorAccumulator);
        }

        if (providerAuthenticationMethods != null) {
            providerAuthenticationMethods.validate(errorAccumulator);
        }
    }
}
