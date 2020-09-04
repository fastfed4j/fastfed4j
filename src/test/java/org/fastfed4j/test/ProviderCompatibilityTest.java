package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.exception.IncompatibleProvidersException;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.core.metadata.IdentityProviderMetadata;
import org.fastfed4j.core.util.CompatibilityUtils;
import org.fastfed4j.test.data.ApplicationProviderJson;
import org.fastfed4j.test.data.IdentityProviderJson;
import org.junit.*;

import java.util.HashSet;
import java.util.Set;

/**
 * The following tests evaluate the compatibility checks.
 * The tests are segmented into different groups for the authentication/provisioning profiles (which can be empty)
 * and the SchemaGrammars/SigningAlgorithms (in which empty values are treated as incompatibilities).
 */
public class ProviderCompatibilityTest {

    private final FastFedConfiguration config = FastFedConfiguration.DEFAULT;
    private final CompatibilityUtils compatibilityUtils = new CompatibilityUtils();

    private enum Result {
        Compatible,
        Incompatible
    }

    // COMPATIBILITY TESTS FOR AUTHENTICATION AND PROVISIONING PROFILES

    @Test
    public void test_AppHasProfiles_IdPHasSameProfiles() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B","C");
        testProfiles(Result.Compatible, 3, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasProfiles_IdPHasMoreProfiles() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B","C","D");
        testProfiles(Result.Compatible, 3, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasProfiles_IdPHasLessProfiles() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B");
        testProfiles(Result.Compatible, 2, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasProfiles_IdPHasNoMatchingProfiles() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("D","E","F");
        testProfiles(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasProfiles_IdPHasNoProfiles() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = new HashSet<>();
        testProfiles(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasNoProfiles_IdPHasProfiles() {
        Set<String> appCapabilities = new HashSet<>();
        Set<String> idpCapabilities = Set.of("A","B","C");
        testProfiles(Result.Compatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasNoProfiles_IdPHasNoProfiles() {
        Set<String> appCapabilities = new HashSet<>();
        Set<String> idpCapabilities = new HashSet<>();
        testProfiles(Result.Compatible, 0, appCapabilities, idpCapabilities);
    }

    // COMPATIBILITY TESTS FOR SCHEMA GRAMMARS and SIGNING ALGORITHMS

    @Test
    public void test_AppHasSchemasAndAlgorithms_IdPHasSameSchemasAndAlgorithms() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B","C");
        testSchemasAndAlgorithms(Result.Compatible, 3, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasSchemasAndAlgorithms_IdPHasMoreSchemasAndAlgorithms() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B","C","D");
        testSchemasAndAlgorithms(Result.Compatible, 3, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasSchemasAndAlgorithms_IdPHasLessSchemasAndAlgorithms() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("A","B");
        testSchemasAndAlgorithms(Result.Compatible, 2, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasSchemasAndAlgorithms_IdPHasNoMatchingSchemasAndAlgorithms() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = Set.of("D","E","F");
        testSchemasAndAlgorithms(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasSchemasAndAlgorithms_IdPHasNoSchemasAndAlgorithms() {
        Set<String> appCapabilities = Set.of("A","B","C");
        Set<String> idpCapabilities = new HashSet<>();
        testSchemasAndAlgorithms(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasNoSchemasAndAlgorithms_IdPHasSchemasAndAlgorithms() {
        Set<String> appCapabilities = new HashSet<>();
        Set<String> idpCapabilities = Set.of("A","B","C");
        testSchemasAndAlgorithms(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    @Test
    public void test_AppHasNoSchemasAndAlgorithms_IdPHasNoSchemasAndAlgorithms() {
        Set<String> appCapabilities = new HashSet<>();
        Set<String> idpCapabilities = new HashSet<>();
        testSchemasAndAlgorithms(Result.Incompatible, 0, appCapabilities, idpCapabilities);
    }

    private void testProfiles(Result expectedResult,
                              int expectedNumberOfSharedCapabilities,
                              Set<String> appCapabilities,
                              Set<String> idpCapabilities)
    {
        ApplicationProviderMetadata app = ApplicationProviderMetadata.fromJson(config, ApplicationProviderJson.FULLY_POPULATED);
        IdentityProviderMetadata idp = IdentityProviderMetadata.fromJson(config, IdentityProviderJson.FULLY_POPULATED);

        app.getCapabilities().setAuthenticationProfiles(appCapabilities);
        app.getCapabilities().setProvisioningProfiles(appCapabilities);

        idp.getCapabilities().setAuthenticationProfiles(idpCapabilities);
        idp.getCapabilities().setProvisioningProfiles(idpCapabilities);

        testCompatibility(expectedResult, app, idp);

        // Test the number of shared capabilities
        Capabilities sharedCapabilities = compatibilityUtils.getSharedCapabilities(idp.getCapabilities(), app.getCapabilities());
        Assert.assertEquals(expectedNumberOfSharedCapabilities, sharedCapabilities.getAuthenticationProfiles().size());
        Assert.assertEquals(expectedNumberOfSharedCapabilities, sharedCapabilities.getProvisioningProfiles().size());
    }

    private void testSchemasAndAlgorithms(Result expectedResult,
                                          int expectedNumberOfSharedCapabilities,
                                          Set<String> appCapabilities,
                                          Set<String> idpCapabilities) {
        ApplicationProviderMetadata app = ApplicationProviderMetadata.fromJson(config, ApplicationProviderJson.FULLY_POPULATED);
        IdentityProviderMetadata idp = IdentityProviderMetadata.fromJson(config, IdentityProviderJson.FULLY_POPULATED);

        app.getCapabilities().setSchemaGrammars(appCapabilities);
        app.getCapabilities().setSigningAlgorithms(appCapabilities);

        idp.getCapabilities().setSchemaGrammars(idpCapabilities);
        idp.getCapabilities().setSigningAlgorithms(idpCapabilities);

        testCompatibility(expectedResult, app, idp);

        // Test the number of shared capabilities
        Capabilities sharedCapabilities = compatibilityUtils.getSharedCapabilities(idp.getCapabilities(), app.getCapabilities());
        Assert.assertEquals(expectedNumberOfSharedCapabilities, sharedCapabilities.getSchemaGrammars().size());
        Assert.assertEquals(expectedNumberOfSharedCapabilities, sharedCapabilities.getSigningAlgorithms().size());
    }

    private void testCompatibility(Result expectedResult,
                                   ApplicationProviderMetadata app,
                                   IdentityProviderMetadata idp)
    {
        // Test the Compatibility Assertion
        try {
            compatibilityUtils.assertCompatibility(idp, app);
        }
        catch (IncompatibleProvidersException ex) {
            if (expectedResult == Result.Compatible) {
                throw new AssertionError( getErrorMessage(Result.Compatible, Result.Incompatible, app, idp, ex.getMessage()));
            } else {
                return;
            }
        }

        if (expectedResult == Result.Incompatible) {
            throw new AssertionError( getErrorMessage(Result.Incompatible, Result.Compatible, app, idp, null));
        }
    }

    private String getErrorMessage(Result expectedResult,
                                   Result actualResult,
                                   ApplicationProviderMetadata app,
                                   IdentityProviderMetadata idp,
                                   String exceptionMessage)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Expected: ").append(expectedResult).append(", ");
        builder.append("Received: ").append(actualResult).append("\n");
        builder.append("for App Capabilities=").append(app.getCapabilities().getAuthenticationProfiles()).append("\n");
        builder.append("and IdP Capabilities=").append(idp.getCapabilities().getAuthenticationProfiles());
        if (exceptionMessage != null) {
            builder.append("\nReported Errors:\n").append(exceptionMessage);
        }
        return builder.toString();
    }

}
