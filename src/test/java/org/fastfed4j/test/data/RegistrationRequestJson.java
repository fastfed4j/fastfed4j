package org.fastfed4j.test.data;

import java.util.Arrays;
import java.util.List;

public class RegistrationRequestJson extends JsonSource {

    /**
     * All attributes are populated with valid data
     */
    public static String FULLY_POPULATED =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"authentication_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "   },\n" +
            "   \"provisioning_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"provider_authentication_methods\": {\n" +
            "       \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "         \"jwks_uri\": \"https://provisioning.example.com/keys\"\n" +
            "       }\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Only the subset of required attributes are populated with valid data
     */
    public static String MINIMALLY_POPULATED =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890\n" +
            " }";

    /**
     * Same as the minimally populated value, but using nulls instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_NULLS =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"authentication_profiles\": null,\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": null,\n" +
            "   \"provisioning_profiles\": null,\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": null,\n" +
            " }";

    /**
     * Same as the minimally populated value, but using empty values instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_EMPTY_VALUES =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"authentication_profiles\": [],\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {},\n" +
            "   \"provisioning_profiles\": [],\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {},\n" +
            " }";

    /**
     * Only SAML authentication is configured. No provisioning.
     */
    public static String ONLY_ENTERPRISE_SAML =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"authentication_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "   }\n" +
            " }";

    /**
     * Only SCIM provisioning is configured. No authentication.
     */
    public static String ONLY_ENTERPRISE_SCIM =
            " {\n" +
            "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"provisioning_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"provider_authentication_methods\": {\n" +
            "       \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "         \"jwks_uri\": \"https://provisioning.example.com/keys\"\n" +
            "       }\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Invalid Types for all attributes
     */
    public static String INVALID_TYPES =
            " {\n" +
            "   \"iss\": 1234,\n" +
            "   \"aud\": true,\n" +
            "   \"exp\": \"1234567890\",\n" +
            "   \"authentication_profiles\": [\n" +
            "     12345\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": [{\n" +
            "     \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "   }],\n" +
            "   \"provisioning_profiles\": \n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "   ,\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": 12345,\n" +
            "       \"phone\": -12345,\n" +
            "       \"email\": false\n" +
            "     },\n" +
            "     \"provider_authentication_methods\": {\n" +
            "       \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "         \"jwks_uri\": [\"https://provisioning.example.com/keys\"]\n" +
            "       }\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Invalid URLs
     */
    public static String INVALID_URLS =
            " {\n" +
            "   \"iss\": \"http://tenant-12345.idp.example.com\",\n" +
            "   \"aud\": \"telnet://tenant-67890.app.example.com\",\n" +
            "   \"exp\": 1234567890,\n" +
            "   \"authentication_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"unknown://tenant-12345.idp.example.com/saml-metadata.xml\"\n" +
            "   },\n" +
            "   \"provisioning_profiles\": [\n" +
            "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
            "   ],\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"provider_contact_information\": {\n" +
            "       \"organization\": \"Example Inc.\",\n" +
            "       \"phone\": \"+1-800-555-5555\",\n" +
            "       \"email\": \"support@example.com\"\n" +
            "     },\n" +
            "     \"provider_authentication_methods\": {\n" +
            "       \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "         \"jwks_uri\": \"https://provisio<>ning.example.com/keys\"\n" +
            "       }\n" +
            "     }\n" +
            "   }\n" +
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
