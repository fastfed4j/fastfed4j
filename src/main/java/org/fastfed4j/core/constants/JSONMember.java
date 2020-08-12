package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of JSON member names
 */
public enum JSONMember {

    APPLICATION_PROVIDER("application_provider"),
    IDENTITY_PROVIDER("identity_provider"),
    ENTITY_ID("entity_id"),
    PROVIDER_DOMAIN("provider_domain"),
    PROVIDER_CONTACT_INFORMATION("provider_contact_information"),
    ORGANIZATION("organization"),
    PHONE("phone"),
    EMAIL("email"),
    DISPLAY_SETTINGS("display_settings"),
    DISPLAY_NAME("display_name"),
    LOGO_URI("logo_uri"),
    ICON_URI("icon_uri"),
    LICENSE("license"),
    CAPABILITIES("capabilities"),
    PROVISIONING_PROFILES("provisioning_profiles"),
    AUTHENTICATION_PROFILES("authentication_profiles"),
    SCHEMA_GRAMMAR("schema_grammar"),
    SCHEMA_GRAMMARS("schema_grammars"),
    SIGNING_ALGORITHMS("signing_algorithms"),
    JWKS_URI("jwks_uri"),
    FASTFED_HANDSHAKE_START_URI("fastfed_handshake_start_uri"),
    FASTFED_HANDSHAKE_REGISTER_URI("fastfed_handshake_register_uri"),
    FASTFED_HANDSHAKE_FINALIZE_URI("fastfed_handshake_finalize_uri"),
    DESIRED_ATTRIBUTES("desired_attributes"),
    REQUIRED_USER_ATTRIBUTES("required_user_attributes"),
    OPTIONAL_USER_ATTRIBUTES("optional_user_attributes"),
    REQUIRED_GROUP_ATTRIBUTES("required_group_attributes"),
    OPTIONAL_GROUP_ATTRIBUTES("optional_group_attributes"),
    JWT_ISSUER("iss"),
    JWT_AUDIENCE("aud"),
    JWT_EXPIRATION("exp"),
    SAML_METADATA_URI("saml_metadata_uri"),
    SAML_RESPONSE_ATTRIBUTES("saml_response_attributes"),
    SCIM_SERVICE_URI("scim_service_uri"),
    SCIM_CAN_SUPPORT_NESTED_GROUPS("can_support_nested_groups"),
    SCIM_MAX_GROUP_MEMBERSHIP_CHANGES("max_group_membership_changes"),
    PROVIDER_AUTHENTICATION_METHOD("provider_authentication_method"),
    PROVIDER_AUTHENTICATION_METHODS("provider_authentication_methods"),
    OAUTH2_TOKEN_ENDPOINT("token_endpoint"),
    OAUTH2_SCOPE("scope"),
    CONTRACT("contract"),
    CONTRACT_PROPOSAL("contract_proposal"),
    CONTRACT_PROPOSAL_EXPIRATION("expiration_date"),
    CONTRACT_PROPOSAL_STATUS("status"),
    ENABLED_PROFILES("enabled_profiles");

    private static Map<String, JSONMember> reverseLookup = new ConcurrentHashMap<>();
    public String jsonName;

    JSONMember(String jsonName) {
        this.jsonName = jsonName;
    }

    @Override
    public String toString() {
        return jsonName;
    }

    public static JSONMember fromString(String urnString) {
        initializeReverseLookupIfNeeded();
        if (urnString != null && reverseLookup.containsKey(urnString)){
            return reverseLookup.get(urnString);
        } else {
            throw new RuntimeException("Unrecognized JSONMember: \"" + urnString + "\"");
        }
    }

    public static boolean isValid(String urnString) {
        initializeReverseLookupIfNeeded();
        return (reverseLookup.containsKey(urnString));
    }

    private static void initializeReverseLookupIfNeeded() {
        if (reverseLookup.isEmpty()) {
            for (JSONMember v : values()) {
                reverseLookup.put(v.jsonName, v);
            }
        }
    }
}
