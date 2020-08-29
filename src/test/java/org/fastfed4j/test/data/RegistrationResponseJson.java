package org.fastfed4j.test.data;

import java.util.Arrays;
import java.util.List;

public class RegistrationResponseJson extends JsonSource {

    /**
     * All attributes are populated with valid data
     */
    public static String FULLY_POPULATED =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"https://tenant-56789.app.example.com/saml-metadata.xml\"\n" +
            "   },   \n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"scim_service_uri\": \"https://tenant-56789.app.example.com/scim\",\n" +
            "     \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\",\n" +
            "     \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "       \"token_endpoint\": \"https://tenant-56789.app.example.com/oauth\",\n" +
            "       \"scope\": \"scim\"\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Only the subset of required attributes are populated with valid data
     */
    public static String MINIMALLY_POPULATED =
            " {\n" +
            " }";

    /**
     * Same as the minimally populated value, but using nulls instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_NULLS =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": null,\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": null,\n" +
            " }";

    /**
     * Same as the minimally populated value, but using empty values instead of omitting the attributes.
     */
    public static String MINIMALLY_POPULATED_WITH_EMPTY_VALUES =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {},\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {},\n" +
            " }";

    /**
     * Only SAML authentication is configured. No provisioning.
     */
    public static String ONLY_ENTERPRISE_SAML =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"https://tenant-56789.app.example.com/saml-metadata.xml\"\n" +
            "   }\n" +
            " }";

    /**
     * Only SCIM provisioning is configured. No authentication.
     */
    public static String ONLY_ENTERPRISE_SCIM =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"scim_service_uri\": \"https://tenant-56789.app.example.com/scim\",\n" +
            "     \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\",\n" +
            "     \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "       \"token_endpoint\": \"https://tenant-56789.app.example.com/oauth\",\n" +
            "       \"scope\": \"scim\"\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Invalid Types for all attributes
     */
    public static String INVALID_TYPES =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": 12345\n" +
            "   },   \n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"scim_service_uri\": true,\n" +
            "     \"provider_authentication_method\": 12345,\n" +
            "     \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "       \"token_endpoint\": [\"https://tenant-56789.app.example.com/oauth\"],\n" +
            "       \"scope\": {\"key\": \"scim\"}\n" +
            "     }\n" +
            "   }\n" +
            " }";

    /**
     * Invalid URLs
     */
    public static String INVALID_URLS =
            " {\n" +
            "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
            "     \"saml_metadata_uri\": \"http://tenant-56789.app.example.com/saml-metadata.xml\"\n" +
            "   },   \n" +
            "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
            "     \"scim_service_uri\": \"telnet://tenant-56789.app.example.com/scim\",\n" +
            "     \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\",\n" +
            "     \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
            "       \"token_endpoint\": \"https://tenant-56789.|||||app.example.com/oauth\",\n" +
            "       \"scope\": \"scim\"\n" +
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
