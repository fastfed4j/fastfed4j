package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.IncompatibleProvidersException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.*;
import org.fastfed4j.core.util.CompatibilityUtils;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Contract is an implementation artifact that is not formally defined in the FastFed Specification. It represents
 * the end result of the FastFed Handshake negotiation, including capabilities that were enabled between the two
 * providers and a subset of each party's metadata that requires long-term storage.
 */
public class Contract extends Metadata {
    private static final CompatibilityUtils compatibilityUtils = new CompatibilityUtils();

    private ProfileRegistry knownProfiles;
    private IdentityProvider identityProvider;
    private ApplicationProvider applicationProvider;
    private EnabledProfiles enabledProfiles;
    private Set<String> signingAlgorithms;

    /**
     * Constructs an empty contract
     */
    public Contract(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Constructs a contract from the mutual capabilities offered by both the Identity Provider and Application Provider.
     * During the FastFed Handshake, each party has an opportunity to amend the contract before finalization.
     * @param idpMetadata FastFed Metadata for the Identity Provider
     * @param appMetadata FastFed Metadata for the Application Provider
     * @throws IncompatibleProvidersException if the providers are not compatible, as defined in Section 5 of the
     *                                        FastFed Core specification ("Provider Compatibility Evaluation")
     */
    public Contract(IdentityProviderMetadata idpMetadata,
                    ApplicationProviderMetadata appMetadata)
        throws IncompatibleProvidersException
    {
        this(idpMetadata.getFastFedConfiguration());

        Capabilities sharedCapabilities = compatibilityUtils.assertCompatibility(idpMetadata, appMetadata);

        this.identityProvider = new IdentityProvider(idpMetadata);
        this.applicationProvider = new ApplicationProvider(appMetadata);
        this.enabledProfiles = new EnabledProfiles(sharedCapabilities);
        this.signingAlgorithms = sharedCapabilities.getSigningAlgorithms();
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public Contract(Contract other) {
        super(other);
        this.knownProfiles = other.knownProfiles;
        this.identityProvider = new IdentityProvider(other.identityProvider);
        this.applicationProvider = new ApplicationProvider(other.applicationProvider);
        this.enabledProfiles = new EnabledProfiles(other.enabledProfiles);
        this.signingAlgorithms = new HashSet<>(other.signingAlgorithms);
    }

    /**
     * Get the Identity Provider information that is stored within the contract.
     * @return IdentityProvider
     */
    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    /**
     * Set the Identity Provider information that is stored within the contract.
     * @param identityProvider
     */
    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    /**
     * Get the Application Provider information that is stored within the contract.
     * @return ApplicationProvider
     */
    public ApplicationProvider getApplicationProvider() {
        return applicationProvider;
    }

    /**
     * Set the Application Provider information that is stored within the contract.
     * @param applicationProvider
     */
    public void setApplicationProvider(ApplicationProvider applicationProvider) {
        this.applicationProvider = applicationProvider;
    }

    /**
     * Get the Authentication and Provisioning profiles to be enabled between the Providers.
     * @return EnabledProfiles
     */
    public EnabledProfiles getEnabledProfiles() {
        return enabledProfiles;
    }

    /**
     * Set the Authentication and Provisioning profiles to be enabled between the Providers.
     * @param enabledProfiles
     */
    public void setEnabledProfiles(EnabledProfiles enabledProfiles) {
        this.enabledProfiles = enabledProfiles;
    }

    /**
     * Get the signing algorithms that are mutually supported by both Providers.
     * @return collection of mutually supported signing algorithms
     */
    public Set<String> getSigningAlgorithms() {
        return signingAlgorithms;
    }

    /**
     * Set the signing algorithms that are mutually supported by both Providers.
     * @param signingAlgorithms collection of mutually supported signing algorithms
     */
    public void setSigningAlgorithms(Set<String> signingAlgorithms) {
        this.signingAlgorithms = signingAlgorithms;
    }

    /**
     * Validates a Registration Request JWT which an Identity Provider sends to an Application Provider
     * during the FastFed Handshake. If valid, amends the contract based upon the contents of the message.
     * @param jwt RegistrationRequest in JWT compact serialization format
     * @throws InvalidMetadataException if JWT is invalid
     * @throws FastFedSecurityException if contents of the RegistrationRequest violate the security assertions defined by the FastFed specification
     */
    public void validateAndOverlayRegistrationRequest(String jwt)
            throws InvalidMetadataException, FastFedSecurityException
    {
        RegistrationRequest regRequest = RegistrationRequest.fromJwt(getFastFedConfiguration(), jwt);
        overlayRegistrationRequest(regRequest);
    }

    /**
     * Amends the contract based upon the contents of a Registration Request message which the Identity Provider
     * sends to the Application Provider during the FastFed Handshake.
     * @param registrationRequest a registration request from the Identity provider
     * @throws FastFedSecurityException if contents of the RegistrationRequest violate the security assertions defined by the FastFed specification
     */
    public void overlayRegistrationRequest(RegistrationRequest registrationRequest)
            throws FastFedSecurityException
    {
        setScopedDownProfiles(registrationRequest);
        for (Map.Entry<String, Metadata> entry : registrationRequest.getAllMetadataExtensions().entrySet()) {
            identityProvider.addRegistrationRequestExtension(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Validates a Registration Response message which an Application Provider returns to an Identity Provider
     * during the FastFed Handshake. If valid, amends the contract based upon the contents of the message.
     * @param jsonString
     * @throws InvalidMetadataException if JSON is invalid
     * @throws FastFedSecurityException if contents of the RegistrationResponse violate the security assertions defined by the FastFed specification
     */
    public void validateAndOverlayRegistrationResponse(String jsonString)
            throws InvalidMetadataException, FastFedSecurityException
    {
        RegistrationResponse regResponse = RegistrationResponse.fromJson(getFastFedConfiguration(), jsonString, enabledProfiles);
        overlayRegistrationResponse(regResponse);
    }

    /**
     * Amends the contract based upon the contents of a Registration Response message which an Identity Provider
     * returns to the Application Provider during the FastFed Handshake.
     * @param registrationResponse a registration response from the Application Provider
     * @throws FastFedSecurityException if contents of the RegistrationResponse violate the security assertions defined by the FastFed specification
     */
    public void overlayRegistrationResponse(RegistrationResponse registrationResponse)
        throws FastFedSecurityException
    {
        getApplicationProvider().setHandshakeFinalizeUri(registrationResponse.getHandshakeFinalizeUri().get());
        for (Map.Entry<String, Metadata> entry : registrationResponse.getAllMetadataExtensions().entrySet()) {
            applicationProvider.addRegistrationResponseExtension(entry.getKey(), entry.getValue());
        }
    }

    /**
     * The Registration Request can scope down the enabled profiles to a subset of the mutually compatible
     * abilities. This method determines the resulting scoped down profiles, and also enforces that
     * the Registration Request doesn't attempt to add profiles that were not in the original list of compatible
     * options.
     * @param registrationRequest a registration request from the Identity provider
     */
    private void setScopedDownProfiles(RegistrationRequest registrationRequest)
        throws FastFedSecurityException
    {
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();

        if (invalidScopeDown(registrationRequest.getAuthenticationProfiles(), enabledProfiles.getAuthenticationProfiles())) {
            errorAccumulator.add(
                    "Registration request contains incompatible authentication profiles " +
                    "(requestedProfiles='" + registrationRequest.getAuthenticationProfiles() +
                    "', allowedProfiles='" + enabledProfiles.getAuthenticationProfiles() +
                    "')");
        }

        if (invalidScopeDown(registrationRequest.getProvisioningProfiles(), enabledProfiles.getProvisioningProfiles())) {
            errorAccumulator.add(
                    "Registration request contains incompatible provisioning profiles " +
                    "(requestedProfiles='" + registrationRequest.getProvisioningProfiles() +
                    "', allowedProfiles='" + enabledProfiles.getProvisioningProfiles() +
                    "')");
        }

        if (errorAccumulator.hasErrors()) {
            throw new FastFedSecurityException(errorAccumulator.toString());
        }

        this.getEnabledProfiles().setAuthenticationProfiles( registrationRequest.getAuthenticationProfiles());
        this.getEnabledProfiles().setProvisioningProfiles( registrationRequest.getProvisioningProfiles());
    }

    /**
     * Tests if a requested set of capabilities exists in the allowed set. Signals a violation if the constraint
     * is not met.
     * @param requested requested set of capabilities
     * @param allowed allowed set of capabilities
     * @return true if invalid
     */
    private boolean invalidScopeDown(Set<String> requested, Set<String> allowed) {
        for (String value : requested) {
            if (! allowed.contains(value))
                return true;
        }
        return false;
    }

    /**
     * Constructs a Contract from a JSON-serialized representation
     * @param configuration FastFed Configuration that controls the behavior of parsing and validation
     * @param jsonString JSON-serialized representation of the Contract
     * @return Contract
     */
    public static Contract fromJson(FastFedConfiguration configuration, String jsonString) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        Contract contract = new Contract(configuration);
        contract.hydrateAndValidate(jsonString);
        return contract;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.CONTRACT);
        super.hydrateFromJson(json);

        IdentityProvider identityProvider = new IdentityProvider(getFastFedConfiguration());
        identityProvider.hydrateFromJson( json.getObject(JSONMember.IDENTITY_PROVIDER));
        setIdentityProvider(identityProvider);

        ApplicationProvider applicationProvider = new ApplicationProvider(getFastFedConfiguration());
        applicationProvider.hydrateFromJson( json.getObject(JSONMember.APPLICATION_PROVIDER));
        setApplicationProvider(applicationProvider);

        EnabledProfiles enabledProfiles = new EnabledProfiles(getFastFedConfiguration());
        enabledProfiles.hydrateFromJson( json.getObject(JSONMember.ENABLED_PROFILES));
        setEnabledProfiles(enabledProfiles);

        setSigningAlgorithms( json.getStringSet(JSONMember.SIGNING_ALGORITHMS));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JSONMember.IDENTITY_PROVIDER, identityProvider);
        validateRequiredObject(errorAccumulator, JSONMember.APPLICATION_PROVIDER, applicationProvider);
        validateRequiredObject(errorAccumulator, JSONMember.ENABLED_PROFILES, enabledProfiles);
        validateRequiredStringCollection(errorAccumulator, JSONMember.SIGNING_ALGORITHMS, signingAlgorithms);

        // Validate the contents of each object
        if (identityProvider != null) { identityProvider.validate(errorAccumulator); }
        if (applicationProvider != null) { applicationProvider.validate(errorAccumulator); }
        if (enabledProfiles != null) { enabledProfiles.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return identityProvider.equals(contract.identityProvider) &&
                applicationProvider.equals(contract.applicationProvider) &&
                enabledProfiles.equals(contract.enabledProfiles) &&
                signingAlgorithms.equals(contract.signingAlgorithms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityProvider, applicationProvider, enabledProfiles, signingAlgorithms);
    }


}
