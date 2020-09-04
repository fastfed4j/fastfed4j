package org.fastfed4j.core.configuration;

import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.profile.KnownProfiles;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.Objects;

/**
 * Allows customization of the SDK behavior.
 */
public class FastFedConfiguration {

    /**
     * Default ProfileRegistry
     */
    // Implementors Note: If this is changed, also update the public javadocs below.
    private static final ProfileRegistry DEFAULT_PROFILE_REGISTRY = KnownProfiles.ALL;

    /**
     * Default value of schema grammar to prefer when accessing Desire Attributes metadata.
     */
    // Implementors Note: If this is changed, also update the public javadocs below.
    private static final SchemaGrammar DEFAULT_SCHEMA_GRAMMAR = SchemaGrammar.SCIM;

    /**
     * Indicates whether nested groups are supported for SCIM provisioning.
     * Default=false, as per section 3.1.2 of FastFed Enterprise SCIM Profile.
     */
    // Implementors Note: If this is changed, also update the public javadocs below.
    public static final boolean SCIM_DEFAULT_VALUE_OF_NESTED_GROUP_SUPPORT = false;

     /**
     * Upper limit for the maximum allowed group membership changes in a single SCIM request.
     * Value=1000, as per section 3.1.2 of FastFed Enterprise SCIM Profile.
     */
     // Implementors Note: If this is changed, also update the public javadocs below.
    public static final int SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES = 1000;

    /**
     * Lower limit for the maximum allowed group membership changes in a single SCIM request.
     * Value=100, as per section 3.1.2 of FastFed Enterprise SCIM Profile.
     */
    // Implementors Note: If this is changed, also update the public javadocs below.
    public static final int SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES = 100;

    /**
     * Default value for the maximum allowed group membership changes in a single SCIM request.
     * Value=100, as per section 3.1.2 of FastFed Enterprise SCIM Profile.
     */
    // Implementors Note: If this is changed, also update the public javadocs below.
    public static final int SCIM_DEFAULT_VALUE_OF_MAX_GROUP_MEMBERSHIP_CHANGES = 100;

    private final ProfileRegistry profileRegistry;
    private final SchemaGrammar preferredSchemaGrammar;
    private final boolean scimCanSupportNestedGroups;
    private final int scimMaxGroupMembershipChanges;


    /**
     * Instance of FastFedConfiguration containing the default values for all settings.
     *
     * <p>The system behavior matches the defaults as specified in:
     *  <ul>
     *      <li>FastFed Core 1.0</li>
     *      <li>FastFed Enterprise SAML Profile 1.0</li>
     *      <li>FastFed Enterprise SCIM Profile 1.0</li>
     *  </ul>
     * </p>
     * <p>For the majority of usage, the default settings may be sufficient. The defaults can be
     * insufficient, for example, if the Provider implements additional FastFed Profiles that
     * are not defined by this SDK, or deviates from the defaults of the FastFed specification
     * such as by supporting nested groups in SCIM provisioning.</p>
     */
    public static final FastFedConfiguration DEFAULT  = new FastFedConfiguration( new Builder());

    /**
     * Construct from a Builder
     */
    public FastFedConfiguration(Builder builder) {
        this.profileRegistry = builder.profileRegistry;
        this.preferredSchemaGrammar = builder.preferredSchemaGrammar;
        this.scimCanSupportNestedGroups = builder.scimCanSupportNestedGroups;
        this.scimMaxGroupMembershipChanges = builder.scimMaxGroupMembershipChanges;
    }

    /**
     * Gets the profile implementations known to the SDK.
     * By default, the registry contains all profiles natively implemented by this SDK. Users of
     * the SDK may change the list or register additional custom extensions.
     * @return ProfileRegistry
     */
    public ProfileRegistry getProfileRegistry() {
        return profileRegistry;
    }

    /**
     * Gets the preferred schema grammar to use when interacting with Desired Attributes.
     * @return ProfileRegistry
     */
    public SchemaGrammar getPreferredSchemaGrammar() {return preferredSchemaGrammar;}

    /**
     * Determine if the SCIM implementation supports nested groups.
     * @return true if the SCIM implementation supports nested groups, otherwise false
     */
    public boolean canSupportNestedGroupsInScim() {
        return scimCanSupportNestedGroups;
    }

    /**
     * Gets the maximum number of group membership changes that can be made in a single SCIM request.
     * @return max number
     */
    public int getMaxGroupMembershipChangesinScim() {
        return scimMaxGroupMembershipChanges;
    }

    /**
     * Builder for FastFedConfiguration
     */
    public static class Builder {
        private ProfileRegistry profileRegistry = DEFAULT_PROFILE_REGISTRY;
        private SchemaGrammar preferredSchemaGrammar = DEFAULT_SCHEMA_GRAMMAR;
        private boolean scimCanSupportNestedGroups = SCIM_DEFAULT_VALUE_OF_NESTED_GROUP_SUPPORT;
        private int scimMaxGroupMembershipChanges = SCIM_DEFAULT_VALUE_OF_MAX_GROUP_MEMBERSHIP_CHANGES;

        /**
         * Construct a new Builder with default values for all settings
         */
        public Builder() {}

        /**
         * Construct a new Builder using an existing FastFedConfiguration to initialize the config values.
         * @param initialConfig Existing FastFedConfiguration used to initialize the config values.
         */
        public Builder(FastFedConfiguration initialConfig) {
            this.profileRegistry = new ProfileRegistry(initialConfig.profileRegistry.getAllProfiles());
            this.preferredSchemaGrammar = initialConfig.preferredSchemaGrammar;
            this.scimCanSupportNestedGroups = initialConfig.scimCanSupportNestedGroups;
            this.scimMaxGroupMembershipChanges = initialConfig.scimMaxGroupMembershipChanges;
        }

        /**
         * Generates an immutable instance of FastFedConfiguration from the Builder settings.
         * @return FastFedConfiguration
         */
        public FastFedConfiguration build() {
            return new FastFedConfiguration(this);
        }

        /**
         * By default, the registry contains all known profiles implemented by this SDK.
         * This overwrites the known profiles with a new ProfileRegistry.
         * @return Builder
         */
        public Builder setProfileRegistry(ProfileRegistry profileRegistry) {
            Objects.requireNonNull(profileRegistry, "profileRegistry must not be null");
            this.profileRegistry = profileRegistry;
            return this;
        }

        /**
         * By default, the registry contains all known profiles implemented by this SDK.
         * This method adds a new profile to the registry and can be used to plug-in additional profile
         * implementations which have been defined outside this SDK.
         * @param profile new profile to be added to the registry
         * @return Builder
         */
        public Builder addProfile(Profile profile) {
            Objects.requireNonNull(profileRegistry, "profile must not be null");
            profileRegistry.add(profile);
            return this;
        }

        /**
         * Sets the preferred schema grammar to be used when interacting with Desired Attributes.
         * Default value is "urn:ietf:params:fastfed:1.0:schemas:scim:2.0".
         * @return Builder
         */
        public Builder setPreferredSchemaGrammar(SchemaGrammar schemaGrammar) {
            Objects.requireNonNull(schemaGrammar, "schemaGrammar must not be null");
            this.preferredSchemaGrammar = schemaGrammar;
            return this;
        }

        /**
         * Sets whether nested groups are supported in SCIM provisioning. Default value is false.
         * @param scimCanSupportNestedGroups true if nested groups are supported, otherwise false
         * @return Builder
         */
        public Builder setCanSupportNestedGroupsInScim(boolean scimCanSupportNestedGroups) {
            this.scimCanSupportNestedGroups = scimCanSupportNestedGroups;
            return this;
        }

        /**
         * Sets the maximum number of group membership changes that can be made in a single SCIM request.
         * Maximum value is 1000 and minimum value is 100. Default value is 100.
         * @param scimMaxGroupMembershipChanges max number
         * @return Builder
         */
        public Builder setMaxGroupMembershipChangesInScim(int scimMaxGroupMembershipChanges) {
            if (scimMaxGroupMembershipChanges > SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES
                || scimMaxGroupMembershipChanges < SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES)
            {
                throw new IllegalArgumentException(
                            "scimMaxGroupMembershipChanges must be between "
                            + SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES + " and "
                            + SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES
                            + " (received: " + scimMaxGroupMembershipChanges + ")");
            }
            this.scimMaxGroupMembershipChanges = scimMaxGroupMembershipChanges;
            return this;
        }
    }

}
