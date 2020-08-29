package org.fastfed4j.test.data;

import java.util.Arrays;
import java.util.List;

public class ContractJson extends JsonSource {

    /**
     * All attributes are populated with valid data
     */
    public static String FULLY_POPULATED =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-12345.idp.example.com/\",\n" +
            "      \"provider_domain\": \"example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://idp.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Identity Provider\",\n" +
            "        \"icon_uri\": \"https://idp.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"jwks_uri\": \"https://idp.example.com/keys\",\n" +
            "      \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.example.com/fastfed/start\",\n" +
            "      \"registration_request_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"provider_contact_information\": {\n" +
            "            \"phone\": \"+1-800-555-6666\",\n" +
            "            \"organization\": \"Example Inc.\",\n" +
            "            \"email\": \"provisioning@example.com\"\n" +
            "          },\n" +
            "          \"provider_authentication_methods\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "              \"jwks_uri\": \"https://provisioning.example.com/keys\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "      \"provider_domain\": \"app.example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"icon_uri\": \"https://app.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\",\n" +
            "      \"fastfed_handshake_finalize_uri\": \"https://tenant-67890.app.example.com/fastfed/finalize\",\n" +
            "      \"application_provider_metadata_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"username\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ]\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"max_group_membership_changes\": 100,\n" +
            "          \"can_support_nested_groups\": false,\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"externalId\",\n" +
            "                \"userName\",\n" +
            "                \"active\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"required_group_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"externalId\"\n" +
            "              ],\n" +
            "              \"optional_group_attributes\": [\n" +
            "                \"members\"\n" +
            "              ]" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"registration_response_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-67890.app.example.com/saml-metadata.xml\"\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"scim_service_uri\": \"https://tenant-67890.app.example.com/scim\",\n" +
            "          \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "            \"scope\": \"scim_provisioning\",\n" +
            "            \"token_endpoint\": \"https://tenant-67890.app.example.com/oauth\"\n" +
            "          },\n" +
            "          \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "      \"provisioning_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "      ],\n" +
            "      \"authentication_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      \"RS256\",\n" +
            "      \"ES512\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * Only the subset of required attributes are populated with valid data
     */
    public static String MINIMALLY_POPULATED =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-12345.idp.example.com/,\",\n" +
            "      \"provider_domain\": \"example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "      },\n" +
            "      \"jwks_uri\": \"https://idp.example.com/keys\",\n" +
            "      \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.example.com/fastfed/start\"\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "      \"provider_domain\": \"app.example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\",\n" +
            "      \"fastfed_handshake_finalize_uri\": \"https://tenant-67890.app.example.com/fastfed/finalize\"\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      \"ES512\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * Only SAML authentication is configured. No provisioning.
     */
    public static String ONLY_ENTERPRISE_SAML =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-12345.idp.example.com/,\",\n" +
            "      \"provider_domain\": \"example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://idp.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Identity Provider\",\n" +
            "        \"icon_uri\": \"https://idp.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"jwks_uri\": \"https://idp.example.com/keys\",\n" +
            "      \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.example.com/fastfed/start\",\n" +
            "      \"registration_request_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "      \"provider_domain\": \"app.example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"icon_uri\": \"https://app.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\",\n" +
            "      \"fastfed_handshake_finalize_uri\": \"https://tenant-67890.app.example.com/fastfed/finalize\",\n" +
            "      \"application_provider_metadata_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"username\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"registration_response_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-67890.app.example.com/saml-metadata.xml\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "      \"authentication_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      \"RS256\",\n" +
            "      \"ES512\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * Only SCIM provisioning is configured. No authentication.
     */
    public static String ONLY_ENTERPRISE_SCIM =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-12345.idp.example.com/,\",\n" +
            "      \"provider_domain\": \"example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://idp.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Identity Provider\",\n" +
            "        \"icon_uri\": \"https://idp.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"jwks_uri\": \"https://idp.example.com/keys\",\n" +
            "      \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.example.com/fastfed/start\",\n" +
            "      \"registration_request_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"provider_contact_information\": {\n" +
            "            \"phone\": \"+1-800-555-6666\",\n" +
            "            \"organization\": \"Example Inc.\",\n" +
            "            \"email\": \"provisioning@example.com\"\n" +
            "          },\n" +
            "          \"provider_authentication_methods\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "              \"jwks_uri\": \"https://provisioning.example.com/keys\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
            "      \"provider_domain\": \"app.example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"icon_uri\": \"https://app.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\",\n" +
            "      \"fastfed_handshake_finalize_uri\": \"https://tenant-67890.app.example.com/fastfed/finalize\",\n" +
            "      \"application_provider_metadata_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"max_group_membership_changes\": 100,\n" +
            "          \"can_support_nested_groups\": false,\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"externalId\",\n" +
            "                \"userName\",\n" +
            "                \"active\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"required_group_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"externalId\"\n" +
            "              ],\n" +
            "              \"optional_group_attributes\": [\n" +
            "                \"members\"\n" +
            "              ]" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"registration_response_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"scim_service_uri\": \"https://tenant-67890.app.example.com/scim\",\n" +
            "          \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "            \"scope\": \"scim_provisioning\",\n" +
            "            \"token_endpoint\": \"https://tenant-67890.app.example.com/oauth\"\n" +
            "          },\n" +
            "          \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "      \"provisioning_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      \"RS256\",\n" +
            "      \"ES512\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * Invalid Types for all attributes
     */
    public static String INVALID_TYPES =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": 2344532342,\n" +
            "      \"provider_domain\": true,\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": false,\n" +
            "        \"organization\": [\"Example Inc.\"],\n" +
            "        \"email\": 6756756\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": 1234,\n" +
            "        \"logo_uri\": 5678,\n" +
            "        \"display_name\": true,\n" +
            "        \"icon_uri\": false\n" +
            "      },\n" +
            "      \"jwks_uri\": [\"https://idp.example.com/keys\"],\n" +
            "      \"fastfed_handshake_start_uri\": {\"key\": \"https://tenant-12345.idp.example.com/fastfed/start\"},\n" +
            "      \"registration_request_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": \"string\",\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"provider_contact_information\": {\n" +
            "            \"phone\": 55555555,\n" +
            "            \"organization\": 66666,\n" +
            "            \"email\": true\n" +
            "          },\n" +
            "          \"provider_authentication_methods\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "              \"jwks_uri\": 1234\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": 11111,\n" +
            "      \"provider_domain\": 11111,\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": 11111,\n" +
            "        \"organization\": 11111,\n" +
            "        \"email\": 11111\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": 11111,\n" +
            "        \"logo_uri\": 11111,\n" +
            "        \"display_name\": 11111,\n" +
            "        \"icon_uri\": 11111\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": 11111,\n" +
            "      \"fastfed_handshake_finalize_uri\": 11111,\n" +
            "      \"application_provider_metadata_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"desired_attributes\": {\n" +
            "            \"potato\": {}\n" +
            "          }\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"max_group_membership_changes\": \"100\",\n" +
            "          \"can_support_nested_groups\": \"false\",\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                1111,\n" +
            "                true\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                null\n" +
            "              ],\n" +
            "              \"required_group_attributes\": [\n" +
            "                1111,\n" +
            "                1111\n" +
            "              ],\n" +
            "              \"optional_group_attributes\": [\n" +
            "                true\n" +
            "              ]" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"registration_response_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": 1111\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"scim_service_uri\": 1111,\n" +
            "          \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "            \"scope\": 1111,\n" +
            "            \"token_endpoint\": 1111\n" +
            "          },\n" +
            "          \"provider_authentication_method\": 1111\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "      \"provisioning_profiles\": [\n" +
            "        [\"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"]\n" +
            "      ],\n" +
            "      \"authentication_profiles\": [\n" +
            "        {\"key\": \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"}\n" +
            "      ]\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      256,\n" +
            "      true\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * Invalid URLs
     */
    public static String INVALID_URLS =
            "{\n" +
            "  \"contract\": {\n" +
            "    \"identity_provider\": {\n" +
            "      \"entity_id\": \"http://tenant-12345.idp.example.com/\",\n" +
            "      \"provider_domain\": \"example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"telnet://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"whatever://idp.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Identity Provider\",\n" +
            "        \"icon_uri\": \"https:idp.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"jwks_uri\": \"https://idp.ex<>ample.com/keys\",\n" +
            "      \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.ex||||ample.com/fastfed/start\",\n" +
            "      \"registration_request_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-12345.idp.examp    le.com/saml-metadata.xml\"\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"provider_contact_information\": {\n" +
            "            \"phone\": \"+1-800-555-6666\",\n" +
            "            \"organization\": \"Example Inc.\",\n" +
            "            \"email\": \"provisioning@example.com\"\n" +
            "          },\n" +
            "          \"provider_authentication_methods\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "              \"jwks_uri\": \"https://provi    sioning.example.com/keys\"\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"application_provider\": {\n" +
            "      \"entity_id\": \"https://tenant-67890   .app.example.com/\",\n" +
            "      \"provider_domain\": \"app.example.com\",\n" +
            "      \"provider_contact_information\": {\n" +
            "        \"phone\": \"+1-800-555-5555\",\n" +
            "        \"organization\": \"Example Inc.\",\n" +
            "        \"email\": \"support@example.com\"\n" +
            "      },\n" +
            "      \"display_settings\": {\n" +
            "        \"license\": \"http://openid.net/intellectual-property/licenses/fastfed/1.0/\",\n" +
            "        \"logo_uri\": \"http://app.example.com/images/logo.png\",\n" +
            "        \"display_name\": \"Example Application Provider\",\n" +
            "        \"icon_uri\": \"http://app.example.com/images/icon.png\"\n" +
            "      },\n" +
            "      \"fastfed_handshake_register_uri\": \"https:::://tenant-67890.app.example.com/fastfed/register\",\n" +
            "      \"fastfed_handshake_finalize_uri\": \"https:::://tenant-67890.app.example.com/fastfed/finalize\",\n" +
            "      \"application_provider_metadata_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"username\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ]\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"max_group_membership_changes\": 100,\n" +
            "          \"can_support_nested_groups\": false,\n" +
            "          \"desired_attributes\": {\n" +
            "            \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
            "              \"required_user_attributes\": [\n" +
            "                \"externalId\",\n" +
            "                \"userName\",\n" +
            "                \"active\",\n" +
            "                \"emails[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"optional_user_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"phoneNumbers[primary eq true].value\"\n" +
            "              ],\n" +
            "              \"required_group_attributes\": [\n" +
            "                \"displayName\",\n" +
            "                \"externalId\"\n" +
            "              ],\n" +
            "              \"optional_group_attributes\": [\n" +
            "                \"members\"\n" +
            "              ]" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"registration_response_extensions\": {\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "          \"saml_metadata_uri\": \"https://tenant-67890.app.example.com/  saml-metadata.xml\"\n" +
            "        },\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "          \"scim_service_uri\": \"telnet://tenant-67890.app.example.com/scim\",\n" +
            "          \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "            \"scope\": \"scim_provisioning\",\n" +
            "            \"token_endpoint\": \"https://ten<>ant-67890.app.example.com/oauth\"\n" +
            "          },\n" +
            "          \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"enabled_profiles\": {\n" +
            "      \"provisioning_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "      ],\n" +
            "      \"authentication_profiles\": [\n" +
            "        \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"signing_algorithms\": [\n" +
            "      \"RS256\",\n" +
            "      \"ES512\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    /**
     * All JSON variations defined in this package.
     */
    public static List<String> ALL_VALID_VARIATIONS = Arrays.asList(
            FULLY_POPULATED,
            MINIMALLY_POPULATED,
            ONLY_ENTERPRISE_SAML,
            ONLY_ENTERPRISE_SCIM
    );
}
