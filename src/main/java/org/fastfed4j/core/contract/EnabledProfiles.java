package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.Capabilities;
import org.fastfed4j.core.metadata.Metadata;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Defines the authentication and provisioning profiles that a contract will enable for end-users.
 */
public class EnabledProfiles extends Metadata {
    Set<String> authenticationProfiles;
    Set<String> provisioningProfiles;

    /**
     * Constructs an empty object.
     */
    public EnabledProfiles(FastFedConfiguration configuration) {
        super(configuration);
        this.authenticationProfiles = new HashSet<>();
        this.provisioningProfiles = new HashSet<>();
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
        this.authenticationProfiles = new HashSet<String>(other.authenticationProfiles);
        this.provisioningProfiles = new HashSet<String>(other.provisioningProfiles);
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
        this.provisioningProfiles = provisioningProfiles;
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

    @Override
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder(JSONMember.ENABLED_PROFILES);
        builder.putAll(super.toJson());
        builder.put(JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        builder.put(JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.ENABLED_PROFILES);
        super.hydrateFromJson(json);
        setAuthenticationProfiles( json.getStringSet(JSONMember.AUTHENTICATION_PROFILES));
        setProvisioningProfiles( json.getStringSet(JSONMember.PROVISIONING_PROFILES));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredStringCollection(errorAccumulator, JSONMember.AUTHENTICATION_PROFILES, authenticationProfiles);
        validateRequiredStringCollection(errorAccumulator, JSONMember.PROVISIONING_PROFILES, provisioningProfiles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnabledProfiles that = (EnabledProfiles) o;
        return authenticationProfiles.equals(that.authenticationProfiles) &&
                provisioningProfiles.equals(that.provisioningProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authenticationProfiles, provisioningProfiles);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(JSONMember.AUTHENTICATION_PROFILES.toString());
        builder.append("=");
        builder.append(authenticationProfiles.toString());
        builder.append(", ");
        builder.append(JSONMember.PROVISIONING_PROFILES);
        builder.append("=");
        builder.append(provisioningProfiles.toString());
        return builder.toString();
    }
}
