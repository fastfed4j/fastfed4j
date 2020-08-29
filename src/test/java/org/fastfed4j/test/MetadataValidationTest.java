package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.EnabledProfiles;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.test.data.*;

import org.junit.Test;


/**
 * These tests exercise the scenarios that generate InvalidMetadataException. This includes:
 * <ul>
 *     <li>Malformed JSON</li>
 *     <li>Missing attributes</li>
 *     <li>Invalid values for attributes</li>
 * </ul>
 */
public class MetadataValidationTest {

    private static final FastFedConfiguration config = FastFedConfiguration.DEFAULT;
    private static final EnabledProfiles emptyProfiles = new EnabledProfiles(config);
    private static final EnabledProfiles allProfiles = TestUtils.getAllEnabledProfiles();

    @Test
    public void testMissingJson() {
        String json = "";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 1);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 1);
        assertErrorCount(new RegistrationRequest(config),                 json, 1);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 1);
        assertErrorCount(new Contract(config),                            json, 1);
        assertErrorCount(new ContractProposal(config),                    json, 1);
    }

    @Test
    public void testMalformedJson() {
        String json = "{malformed";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 1);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 1);
        assertErrorCount(new RegistrationRequest(config),                 json, 1);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 1);
        assertErrorCount(new Contract(config),                            json, 1);
        assertErrorCount(new ContractProposal(config),                    json, 1);
    }

    @Test
    public void testEmptyJson() {
        String json = "{}";
        assertErrorCount(new IdentityProviderMetadata(config),            json, 7);
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 6);
        assertErrorCount(new RegistrationRequest(config),                 json, 3);
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 0);
        assertErrorCount(new Contract(config),                            json, 4);
        assertErrorCount(new ContractProposal(config),                    json, 3);
    }

    @Test
    public void testInvalidTypesInJson() {
        String json;
        json = IdentityProviderJson.INVALID_TYPES;
        assertErrorCount(new IdentityProviderMetadata(config),            json, 11);

        json = ApplicationProviderJson.INVALID_TYPES;
        assertErrorCount(new ApplicationProviderMetadata(config),         json, 10);

        json = RegistrationRequestJson.INVALID_TYPES;
        assertErrorCount(new RegistrationRequest(config),                 json, 10);

        json = RegistrationResponseJson.INVALID_TYPES;
        assertErrorCount(new RegistrationResponse(config, emptyProfiles), json, 3);

        json = ContractJson.INVALID_TYPES;
        assertErrorCount(new Contract(config),                            json, 39);

        json = ContractProposalJson.INVALID_TYPES;
        assertErrorCount(new ContractProposal(config),                    json, 41);
    }

    private void assertErrorCount(Metadata metadata, String json, int expectedErrorCount) {
        try {
            metadata.hydrateAndValidate(json);
        }
        catch (InvalidMetadataException ex) {
            int actualErrorCount = ex.getErrorAccumulator().getErrors().size();
            if (actualErrorCount != expectedErrorCount) {
                StringBuilder builder = new StringBuilder();
                builder.append("Unexpected number of validation errors for ").append(metadata.getClass().getName());
                builder.append(" (expected ").append(expectedErrorCount).append(", ");
                builder.append("received ").append(actualErrorCount).append(")\n");
                ex.getErrorAccumulator().getErrors().stream().forEach(error -> builder.append("  ").append(error).append("\n"));
                throw new AssertionError(builder.toString());
            }
        }
    }
}
