package org.fastfed4j.test.data;

import java.util.Arrays;
import java.util.List;

public class ContractProposalJson extends JsonSource {

    /**
     * All attributes are populated with valid data
     */
    public static String FULLY_POPULATED =
            "{\n" +
            "  \"contract_proposal\": {\n" +
            "      \"status\": \"Pending\",\n" +
            "      \"expiration_date\": " + oneHourFromNow() + ",\n" +
                   removeOuterBrackets(ContractJson.FULLY_POPULATED) +
            "   }" +
            "}";

    /**
     * Only the subset of required attributes are populated with valid data
     */
    public static String MINIMALLY_POPULATED =
            "{\n" +
            "  \"contract_proposal\": {\n" +
            "      \"status\": \"Pending\",\n" +
            "      \"expiration_date\": " + oneHourFromNow() + ",\n" +
                   removeOuterBrackets(ContractJson.MINIMALLY_POPULATED) +
            "   }" +
            "}";

    /**
     * Only SAML authentication is configured. No provisioning.
     */
    public static String ONLY_ENTERPRISE_SAML =
            "{\n" +
            "  \"contract_proposal\": {\n" +
            "      \"status\": \"Pending\",\n" +
            "      \"expiration_date\": " + oneHourFromNow() + ",\n" +
                   removeOuterBrackets(ContractJson.ONLY_ENTERPRISE_SAML) +
            "   }" +
            "}";

    /**
     * Only SCIM provisioning is configured. No authentication.
     */
    public static String ONLY_ENTERPRISE_SCIM =
            "{\n" +
            "  \"contract_proposal\": {\n" +
            "      \"status\": \"Pending\",\n" +
            "      \"expiration_date\": " + oneHourFromNow() + ",\n" +
                   removeOuterBrackets(ContractJson.ONLY_ENTERPRISE_SCIM) +
            "   }" +
            "}";

    /**
     * Invalid Types
     */
    public static String INVALID_TYPES =
            "{\n" +
            "  \"contract_proposal\": {\n" +
            "      \"status\": 12345,\n" +
            "      \"expiration_date\": true,\n" +
                   removeOuterBrackets(ContractJson.INVALID_TYPES) +
            "   }" +
            "}";

    /**
     * Invalid URLs
     */
    public static String INVALID_URLS =
            "{\n" +
                    "  \"contract_proposal\": {\n" +
                    "      \"status\": \"Pending\",\n" +
                    "      \"expiration_date\": " + oneHourFromNow() + ",\n" +
                           removeOuterBrackets(ContractJson.INVALID_URLS) +
                    "   }" +
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
