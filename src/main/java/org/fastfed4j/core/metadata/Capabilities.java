package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the Capabilities metadata, as defined in section 3.3.1 of the FastFed Core specification.
 */
public class Capabilities extends Metadata {
    private Set<String> authenticationProfiles = new HashSet<>();
    private Set<String> provisioningProfiles = new HashSet<>();
    private Set<String> schemaGrammars = new HashSet<>();
    private Set<String> signingAlgorithms = new HashSet<>();

    /**
     * Constructs an empty instance
     */
    public Capabilities(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public Capabilities(Capabilities other) {
        super(other);
        this.authenticationProfiles = new HashSet<>(other.authenticationProfiles);
        this.provisioningProfiles = new HashSet<>(other.provisioningProfiles);
        this.schemaGrammars = new HashSet<>(other.schemaGrammars);
        this.signingAlgorithms = new HashSet<>(other.signingAlgorithms);
    }

    public Set<String> getAuthenticationProfiles() {
        return authenticationProfiles;
    }

    public void setAuthenticationProfiles(Set<String> authenticationProfiles) {
        if (authenticationProfiles != null)
            this.authenticationProfiles = authenticationProfiles;
    }

    public Set<String> getProvisioningProfiles() {
        return provisioningProfiles;
    }

    public void setProvisioningProfiles(Set<String> provisioningProfiles) {
        if (provisioningProfiles != null)
           this.provisioningProfiles = provisioningProfiles;
    }

    public Set<String> getSchemaGrammars() {
        return schemaGrammars;
    }

    public void setSchemaGrammars(Set<String> schemaGrammars) {
        if (schemaGrammars != null)
           this.schemaGrammars = schemaGrammars;
    }

    public Set<String> getSigningAlgorithms() {
        return signingAlgorithms;
    }

    public void setSigningAlgorithms(Set<String> signingAlgorithms) {
        if (signingAlgorithms != null)
           this.signingAlgorithms = signingAlgorithms;
    }

    /**
     * Convenience method to extract a collection of all the authentication and provisioning profile URNs
     * specified within the Capabilities.
     * @return authentication and provisioning profile URNs
     */
    public Set<String> getAllProfiles() {
        Set<String> allProfiles = new HashSet<>();
        allProfiles.addAll(authenticationProfiles);
        allProfiles.addAll(provisioningProfiles);
        return allProfiles;
    }

    /**
     * Convenience method to extract a collection of all the authentication and provisioning profile URNs
     * specified within the Capabilities, filtered to only the known profiles as determined by the
     * consulting the ProfileRegistry in the FastFedConfiguration.
     * @return authentication and provisioning profile URNs
     */
    public Set<String> filterToKnownProfiles() {
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        Set<String> result = new HashSet<>();
        for (String profileUrn : getAllProfiles()) {
            if (registry.containsUrn(profileUrn)) {
                result.add(profileUrn);
            }
            else {
                // TODO - add an informational log if we ignore an unrecognized profile
            }
        }
        return result;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.CAPABILITIES);
        super.hydrateFromJson(json);
        setAuthenticationProfiles( json.getStringSet(JSONMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getStringSet(JSONMember.PROVISIONING_PROFILES));
        setSchemaGrammars( json.getStringSet(JSONMember.SCHEMA_GRAMMARS));
        setSigningAlgorithms( json.getStringSet(JSONMember.SIGNING_ALGORITHMS));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateOptionalStringCollection(errorAccumulator, JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
        validateOptionalStringCollection(errorAccumulator, JSONMember.SCHEMA_GRAMMARS, schemaGrammars);
        validateOptionalStringCollection(errorAccumulator, JSONMember.SIGNING_ALGORITHMS, signingAlgorithms);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Capabilities that = (Capabilities) o;
        return Objects.equals(authenticationProfiles, that.authenticationProfiles) &&
                Objects.equals(provisioningProfiles, that.provisioningProfiles) &&
                schemaGrammars.equals(that.schemaGrammars) &&
                signingAlgorithms.equals(that.signingAlgorithms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authenticationProfiles, provisioningProfiles, schemaGrammars, signingAlgorithms);
    }
}
