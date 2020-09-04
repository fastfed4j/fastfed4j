package org.fastfed4j.core.util;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.IncompatibleProvidersException;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.core.metadata.IdentityProviderMetadata;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities to evaluate the compatibility between FastFed providers.
 */
public class CompatibilityUtils {

    /**
     * Get the mutually shared capabilities between two providers.
     * @param identityProviderCapabilities capabilities of the Identity Provider
     * @param applicationProviderCapabilities  capabilities of the Application Provider
     * @return mutually shared capabilities between the providers
     */
    public Capabilities getSharedCapabilities(Capabilities identityProviderCapabilities,
                                              Capabilities applicationProviderCapabilities)
    {
        Objects.requireNonNull(identityProviderCapabilities);
        Objects.requireNonNull(applicationProviderCapabilities);

        Set<String> mergedAuthenticationProfiles = intersect(
                identityProviderCapabilities.getAuthenticationProfiles(),
                applicationProviderCapabilities.getAuthenticationProfiles()
        );
        Set<String> mergedProvisioningProfiles = intersect(
                identityProviderCapabilities.getProvisioningProfiles(),
                applicationProviderCapabilities.getProvisioningProfiles()
        );
        Set<String> mergedSchemaGrammars = intersect(
                identityProviderCapabilities.getSchemaGrammars(),
                applicationProviderCapabilities.getSchemaGrammars()
        );
        Set<String> mergedSigningAlgorithms = intersect(
                identityProviderCapabilities.getSigningAlgorithms(),
                applicationProviderCapabilities.getSigningAlgorithms()
        );

        Capabilities capabilities = new Capabilities(identityProviderCapabilities.getFastFedConfiguration());
        capabilities.setAuthenticationProfiles(mergedAuthenticationProfiles);
        capabilities.setProvisioningProfiles(mergedProvisioningProfiles);
        capabilities.setSchemaGrammars(mergedSchemaGrammars);
        capabilities.setSigningAlgorithms(mergedSigningAlgorithms);

        return capabilities;
    }

    /**
     * Enforce that two providers are mutually compatible and, if so, return the mutually shared capabilities.
     * @param idpMetadata Identity Provider Metadata
     * @param appMetadata Application Provider Metadata
     * @return If compatible, returns the mutually shared capabilities. Else, throws an IncompatibleProvidersException.
     * @throws IncompatibleProvidersException if not compatible
     */
    public Capabilities assertCompatibility(IdentityProviderMetadata idpMetadata,
                                            ApplicationProviderMetadata appMetadata)
            throws IncompatibleProvidersException
    {
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        Optional<Capabilities> sharedCapabilities = evaluateCompatibility(errorAccumulator, idpMetadata, appMetadata);
        if (sharedCapabilities.isPresent()) {
            return sharedCapabilities.get();
        }
        else {
            throw new IncompatibleProvidersException(errorAccumulator.toString());
        }
    }

    /**
     * Evaluate if two providers are mutually compatible and, if so, return the mutually shared capabilities.
     * @param idpMetadata Identity Provider Metadata
     * @param appMetadata Application Provider Metadata
     * @param errorAccumulator If the providers are incompatible, will be populated with the full list of incompatibility error messages
     * @return If compatible, returns the mutually shared capabilities. Else, returns an empty value.
     */
    public Optional<Capabilities> evaluateCompatibility(ErrorAccumulator errorAccumulator,
                                                        IdentityProviderMetadata idpMetadata,
                                                        ApplicationProviderMetadata appMetadata)
    {
        Capabilities idpCapabilities = idpMetadata.getCapabilities();
        Capabilities appCapabilities = appMetadata.getCapabilities();
        Capabilities sharedCapabilities = getSharedCapabilities(idpCapabilities, appCapabilities);
        Set<String> sharedAuthenticationProfiles = sharedCapabilities.getAuthenticationProfiles();
        Set<String> sharedProvisioningProfiles = sharedCapabilities.getProvisioningProfiles();
        Set<String> sharedSchemaGrammars = sharedCapabilities.getSchemaGrammars();
        Set<String> sharedSigningAlgorithms = sharedCapabilities.getSigningAlgorithms();

        boolean appRequiresAuthentication = !appCapabilities.getAuthenticationProfiles().isEmpty();
        boolean appRequiresProvisioning = !appCapabilities.getProvisioningProfiles().isEmpty();

        boolean isCompatible = true;

        if (appRequiresAuthentication && notCompatible(sharedAuthenticationProfiles)) {
            isCompatible = false;
            errorAccumulator.add(
                    "Incompatible authentication profiles. (IdentityProvider='"
                            + idpCapabilities.getAuthenticationProfiles().toString()
                            + "', ApplicationProvider='"
                            + appCapabilities.getAuthenticationProfiles().toString()
                            + "')"
            );
        }

        if (appRequiresProvisioning && notCompatible(sharedProvisioningProfiles)) {
            isCompatible = false;
            errorAccumulator.add(
                    "Incompatible provisioning profiles. (IdentityProvider='"
                            + idpCapabilities.getProvisioningProfiles().toString()
                            + "', ApplicationProvider='"
                            + appCapabilities.getProvisioningProfiles().toString()
                            + "')"
            );
        }

        if (notCompatible(sharedSchemaGrammars)) {
            isCompatible = false;
            errorAccumulator.add(
                    "Incompatible schema grammars. (IdentityProvider='"
                            + idpCapabilities.getSchemaGrammars().toString()
                            + "', ApplicationProvider='"
                            + appCapabilities.getSchemaGrammars().toString()
                            + "')"
            );
        }

        if (notCompatible(sharedSigningAlgorithms)) {
            isCompatible = false;
            errorAccumulator.add(
                    "Incompatible signing algorithms. (IdentityProvider='"
                            + idpCapabilities.getSigningAlgorithms().toString()
                            + "', ApplicationProvider='"
                            + appCapabilities.getSigningAlgorithms().toString()
                            + "')"
            );
        }

        if (isCompatible) {
            return Optional.of(sharedCapabilities);
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Calculate the intersection between two sets
     * @param set1 first set
     * @param set2 second set
     * @return intersection
     */
    private Set<String> intersect(Set<String> set1, Set<String> set2) {
        if (set1 == null || set2 == null) {
            return new HashSet<>();
        }
        return set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Evaluate if two providers are compatible by examining if a shared set of capabilities are empty
     * @param sharedCapabilities mutually shared set of capabilities between two providers
     * @return true if not compatible
     */
    private boolean notCompatible(Set<String> sharedCapabilities) {
        return (sharedCapabilities == null || sharedCapabilities.isEmpty());
    }
}
