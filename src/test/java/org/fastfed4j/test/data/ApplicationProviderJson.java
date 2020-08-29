package org.fastfed4j.test.data;

import java.util.Arrays;
import java.util.List;

/**
 * JSON representations of Application Provider Metadata
 */
public class ApplicationProviderJson extends JsonSource {

    /**
     * All attributes are populated with valid data
     */
    public static String FULLY_POPULATED =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "       \"icon_uri\": \"https://app.example.com/images/icon.png\",\n" +
            "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"authentication_profiles\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "       ],\n" +
            "       \"provisioning_profiles\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "       ],\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"ES512\",\n" +
            "         \"RS256\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "       \"desired_attributes\": {\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "           \"required_user_attributes\": [\n" +
            "            \"externalId\",\n" +
            "             \"userName\",\n" +
            "             \"emails[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"optional_user_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"phoneNumbers[primary eq true].value\"\n" +
            "           ]\n" +
            "         }\n" +
            "       }\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "       \"can_support_nested_groups\": true,\n" +
            "       \"max_group_membership_changes\": 500,\n" +
            "       \"desired_attributes\": {\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "           \"required_user_attributes\": [\n" +
            "             \"externalId\",\n" +
            "             \"userName\",\n" +
            "             \"active\",\n" +
            "             \"emails[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"optional_user_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"phoneNumbers[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"required_group_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"externalId\"\n" +
            "           ],\n" +
            "           \"optional_group_attributes\": [\n" +
            "             \"members\"\n" +
            "           ]" +
            "         }\n" +
            "       }\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * Only the subset of required attributes are populated with valid data
     */
    public static String MINIMALLY_POPULATED =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"RS256\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * Same as the minimally populated value, but using nulls instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_NULLS =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"logo_uri\": null,\n" +
            "       \"icon_uri\": null,\n" +
            "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"authentication_profiles\": null,\n" +
            "       \"provisioning_profiles\": null,\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"ES512\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": null,\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": null,\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * Same as the minimally populated value, but using empty values instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_EMPTY_VALUES =
            "{\n" +
                    "   \"application_provider\": {\n" +
                    "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
                    "     \"provider_domain\": \"app.example.com\",\n" +
                    "     \"provider_contact_information\": {\n" +
                    "       \"organization\": \"Example Inc.\",\n" +
                    "       \"phone\": \"+1-800-555-5555\",\n" +
                    "       \"email\": \"support@example.com\"\n" +
                    "     },\n" +
                    "     \"display_settings\": {\n" +
                    "       \"display_name\": \"Example Application Provider\",\n" +
                    "       \"logo_uri\": \"\",\n" +
                    "       \"icon_uri\": \"   \",\n" +
                    "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
                    "     },\n" +
                    "     \"capabilities\": {\n" +
                    "       \"authentication_profiles\": [],\n" +
                    "       \"provisioning_profiles\": [],\n" +
                    "       \"schema_grammars\": [\n" +
                    "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
                    "       ],\n" +
                    "       \"signing_algorithms\": [\n" +
                    "         \"ES512\"\n" +
                    "       ]\n" +
                    "     },\n" +
                    "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {},\n" +
                    "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {},\n" +
                    "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
                    "     }\n" +
                    " }";

    /**
     * Only SAML authentication is configured. No provisioning.
     */
    public static String ONLY_ENTERPRISE_SAML =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "       \"icon_uri\": \"https://app.example.com/images/icon.png\",\n" +
            "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"authentication_profiles\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "       ],\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"ES512\",\n" +
            "         \"RS256\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "       \"desired_attributes\": {\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "           \"required_user_attributes\": [\n" +
            "            \"externalId\",\n" +
            "             \"userName\",\n" +
            "             \"emails[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"optional_user_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"phoneNumbers[primary eq true].value\"\n" +
            "           ]\n" +
            "         }\n" +
            "       }\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * Only SCIM provisioning is configured. No authentication.
     */
    public static String ONLY_ENTERPRISE_SCIM =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "       \"icon_uri\": \"https://app.example.com/images/icon.png\",\n" +
            "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"provisioning_profiles\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "       ],\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"ES512\",\n" +
            "         \"RS256\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "      \"can_support_nested_groups\": true,\n" +
            "      \"max_group_membership_changes\": 500,\n" +
            "       \"desired_attributes\": {\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "           \"required_user_attributes\": [\n" +
            "             \"externalId\",\n" +
            "             \"userName\",\n" +
            "            \"active\",\n" +
            "             \"emails[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"optional_user_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"phoneNumbers[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"required_group_attributes\": [\n" +
            "             \"displayName\",\n" +
            "             \"externalId\"\n" +
            "           ],\n" +
            "           \"optional_group_attributes\": [\n" +
            "             \"members\"\n" +
            "           ]" +
            "         }\n" +
            "       }\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * Invalid Types for each attribute
     */
    public static String INVALID_TYPES =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": 12345,\n" +
            "     \"provider_domain\": true,\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": -12345,\n" +
            "       \"phone\": false,\n" +
            "       \"email\": 12345\n" +
            "     },\n" +
            "     \"display_settings\": [{\n" +
            "       \"display_name\": 12345,\n" +
            "       \"logo_uri\": -12345,\n" +
            "       \"icon_uri\": true,\n" +
            "       \"license\": false\n" +
            "     }],\n" +
            "     \"capabilities\": {\n" +
            "       \"authentication_profiles\": \"string\"," +
            "       \"schema_grammars\": [\n" +
            "         12345" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         true,\n" +
            "         12345" +
            "       ]\n" +
            "     },\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "       \"desired_attributes\": {\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "           \"required_user_attributes\": [\n" +
            "            null,\n" +
            "             \"userName\",\n" +
            "             \"emails[primary eq true].value\"\n" +
            "           ],\n" +
            "           \"optional_user_attributes\": [\n" +
            "             null,\n" +
            "             null\n" +
            "           ]\n" +
            "         }\n" +
            "       }\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": {\"key\": \"https://tenant-67890.app.example.com/fastfed/register\"}\n" +
            "     }\n" +
            " }";

    /**
     * Invalid URLs for each attribute
     */
    public static String INVALID_URLS =
            "{\n" +
            "   \"application_provider\": {\n" +
            "     \"entity_id\": \"http://tenant-67890.app.example.com/\",\n" +
            "     \"provider_domain\": \"app.example.com\",\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"display_settings\": {\n" +
            "       \"display_name\": \"Example Application Provider\",\n" +
            "       \"logo_uri\": \"telnet://app.example.com/images/logo.png\",\n" +
            "       \"icon_uri\": \"https://app.ex<>ample.com/images/icon.png\",\n" +
            "       \"license\": \"https://openid.net/int     ellectual-property/licenses/fastfed/1.0/\"\n" +
            "     },\n" +
            "     \"capabilities\": {\n" +
            "       \"schema_grammars\": [\n" +
            "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
            "       ],\n" +
            "       \"signing_algorithms\": [\n" +
            "         \"ES512\",\n" +
            "         \"RS256\"\n" +
            "       ]\n" +
            "     },\n" +
            "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
            "     }\n" +
            " }";

    /**
     * All JSON variations defined in this package.
     */
    public static List<String> ALL_VALID_VARIATIONS = Arrays.asList(
            FULLY_POPULATED,
            MINIMALLY_POPULATED,
            MINIMALLY_POPULATED_WITH_NULLS,
            MINIMALLY_POPULATED_WITH_EMPTY_VALUES,
            ONLY_ENTERPRISE_SAML,
            ONLY_ENTERPRISE_SCIM
    );
}
