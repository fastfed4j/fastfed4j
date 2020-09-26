package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
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
        if (other.authenticationProfiles != null)
            this.authenticationProfiles = new HashSet<>(other.authenticationProfiles);
        if (other.provisioningProfiles != null)
            this.provisioningProfiles = new HashSet<>(other.provisioningProfiles);
        if (other.schemaGrammars != null)
            this.schemaGrammars = new HashSet<>(other.schemaGrammars);
        if (other.signingAlgorithms != null)
            this.signingAlgorithms = new HashSet<>(other.signingAlgorithms);
    }

    public Set<String> getAuthenticationProfiles() {
        return authenticationProfiles;
    }

    public void setAuthenticationProfiles(Set<String> authenticationProfiles) {
        Objects.requireNonNull(authenticationProfiles, "authenticationProfiles must not be null");
        this.authenticationProfiles = authenticationProfiles;
    }

    public Set<String> getProvisioningProfiles() {
        return provisioningProfiles;
    }

    public void setProvisioningProfiles(Set<String> provisioningProfiles) {
        Objects.requireNonNull(provisioningProfiles, "provisioningProfiles must not be null");
        this.provisioningProfiles = provisioningProfiles;
    }

    public Set<String> getSchemaGrammars() {
        return schemaGrammars;
    }

    public void setSchemaGrammars(Set<String> schemaGrammars) {
        Objects.requireNonNull(schemaGrammars, "schemaGrammars must not be null");
        this.schemaGrammars = schemaGrammars;
    }

    public Set<String> getSigningAlgorithms() {
        return signingAlgorithms;
    }

    public void setSigningAlgorithms(Set<String> signingAlgorithms) {
        Objects.requireNonNull(signingAlgorithms, "signingAlgorithms must not be null");
        this.signingAlgorithms = signingAlgorithms;
    }

    /**
     * Convenience method to extract a collection of all the authentication and provisioning profile URNs
     * specified within the Capabilities.
     * @return authentication and provisioning profile URNs
     */
    public Set<String> getAllProfileUrns() {
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
    public Set<String> getAllKnownProfileUrns() {
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        Set<String> result = new HashSet<>();
        for (String profileUrn : getAllProfileUrns()) {
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
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.CAPABILITIES);
        builder.putAll(super.toJson());
        builder.put(JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        builder.put(JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
        builder.put(JsonMember.SCHEMA_GRAMMARS, schemaGrammars);
        builder.put(JsonMember.SIGNING_ALGORITHMS, signingAlgorithms);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.CAPABILITIES);
        super.hydrateFromJson(json);

        authenticationProfiles = json.getNonNullableStringSet(JsonMember.AUTHENTICATION_PROFILES);
        provisioningProfiles = json.getNonNullableStringSet(JsonMember.PROVISIONING_PROFILES);
        schemaGrammars = json.getNonNullableStringSet(JsonMember.SCHEMA_GRAMMARS);
        signingAlgorithms = json.getNonNullableStringSet(JsonMember.SIGNING_ALGORITHMS);

        //Ignore unrecognized schema grammars. They cause problems downstream when validating DesiredAttributes.
        //Schema grammars are not intended to be easily pluggable or changed, because so much logic
        //depends upon a shared consensus on the valid grammar.
        //See more information in org/fastfed4j/core/constants/SchemaGrammar.java
        Set<String> grammarsToRemove = new HashSet<>();
        for (String schemaGrammarUrn : schemaGrammars) {
            if (! SchemaGrammar.isValid(schemaGrammarUrn)) {
                grammarsToRemove.add(schemaGrammarUrn);
            }
        }
        schemaGrammars.removeAll(grammarsToRemove);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateOptionalStringCollection(errorAccumulator, JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
        validateOptionalStringCollection(errorAccumulator, JsonMember.SCHEMA_GRAMMARS, schemaGrammars);
        validateOptionalStringCollection(errorAccumulator, JsonMember.SIGNING_ALGORITHMS, signingAlgorithms);
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
