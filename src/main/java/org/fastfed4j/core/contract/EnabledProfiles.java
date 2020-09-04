package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.core.metadata.Metadata;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Defines the authentication and provisioning profiles that a contract will enable for end-users.
 */
public class EnabledProfiles extends Metadata {
    Set<String> authenticationProfiles = new HashSet<>();
    Set<String> provisioningProfiles = new HashSet<>();

    /**
     * Constructs an empty object.
     */
    public EnabledProfiles(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Constructs an instance from a pre-determined set of shared capabilities
     * @param sharedCapabilities shared, mutually compatible capabilities between two providers
     */
    public EnabledProfiles(Capabilities sharedCapabilities) {
        super(sharedCapabilities.getFastFedConfiguration());
        this.authenticationProfiles = sharedCapabilities.getAuthenticationProfiles();
        this.provisioningProfiles = sharedCapabilities.getProvisioningProfiles();
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public EnabledProfiles(EnabledProfiles other) {
        super(other);
        if (other.authenticationProfiles != null)
            this.authenticationProfiles = new HashSet<>(other.authenticationProfiles);
        if (other.provisioningProfiles != null)
            this.provisioningProfiles = new HashSet<>(other.provisioningProfiles);
    }

    /**
     * Gets the collection of authentication profiles that should be enabled for end-users.
     * @return authentication profile URNs
     */
    public Set<String> getAuthenticationProfiles() {
        return authenticationProfiles;
    }

    /**
     * Sets the collection of authentication profiles that should be enabled for end-users.
     * @param authenticationProfiles authentication profile URNs
     */
    public void setAuthenticationProfiles(Set<String> authenticationProfiles) {
        Objects.requireNonNull(authenticationProfiles, "authenticationProfiles must not be null");
        this.authenticationProfiles = authenticationProfiles;
    }

    /**
     * Gets the collection of provisioning profiles that should be enabled for end-users.
     * @return provisioning profile URNs
     */
    public Set<String> getProvisioningProfiles() {
        return provisioningProfiles;
    }

    /**
     * Sets the collection of provisioning profiles that should be enabled for end-users.
     * @param provisioningProfiles provisioning profile URNs
     */
    public void setProvisioningProfiles(Set<String> provisioningProfiles) {
        Objects.requireNonNull(provisioningProfiles, "provisioningProfiles must not be null");
        this.provisioningProfiles = provisioningProfiles;
    }

    /**
     * Convenience method to extract a collection of all the authentication and provisioning profile URNs
     * specified within the Capabilities.
     * @return authentication and provisioning profile URNs
     */
    public Set<String> getAllProfiles() {
        Set<String> allProfiles = new HashSet<>();
        if (authenticationProfiles != null) {
            allProfiles.addAll(authenticationProfiles);
        }
        if (provisioningProfiles != null) {
            allProfiles.addAll(provisioningProfiles);
        }
        return allProfiles;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.ENABLED_PROFILES);
        builder.putAll(super.toJson());
        builder.put(JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        builder.put(JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.ENABLED_PROFILES);
        super.hydrateFromJson(json);
        setAuthenticationProfiles( json.getNonNullableStringSet(JsonMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getNonNullableStringSet(JsonMember.PROVISIONING_PROFILES));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateOptionalStringCollection(errorAccumulator, JsonMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateOptionalStringCollection(errorAccumulator, JsonMember.PROVISIONING_PROFILES, provisioningProfiles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnabledProfiles that = (EnabledProfiles) o;
        return Objects.equals(authenticationProfiles, that.authenticationProfiles) &&
                Objects.equals(provisioningProfiles, that.provisioningProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), authenticationProfiles, provisioningProfiles);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(JsonMember.AUTHENTICATION_PROFILES.toString());
        builder.append("=");
        builder.append(authenticationProfiles.toString());
        builder.append(", ");
        builder.append(JsonMember.PROVISIONING_PROFILES);
        builder.append("=");
        builder.append(provisioningProfiles.toString());
        return builder.toString();
    }
}
