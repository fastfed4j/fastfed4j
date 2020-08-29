package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;

import java.util.Objects;

/**
 * Represents the extensions to the Application Provider Metadata defined in section 3.1.2
 * of the FastFed Enterprise SCIM Profile.
 */
class ApplicationProviderMetadataExtension extends Metadata {

    private DesiredAttributes desiredAttributes;
    private Boolean canSupportNestedGroups;
    private Integer maxGroupMembershipChanges;

    public ApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
        super(configuration);

        // Setting the following as convenient defaults. When hydrating from JSON
        // the values will be overridden since the JSON settings take precedence.
        // For example, the JSON structure might represent the capabilities of
        // another external provider, not this local provider who is invoking the SDK,
        // and hence the values will differ from the local FastFedConfiguration.
        canSupportNestedGroups = configuration.canSupportNestedGroupsInScim();
        maxGroupMembershipChanges = configuration.getMaxGroupMembershipChangesinScim();
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public ApplicationProviderMetadataExtension(ApplicationProviderMetadataExtension other) {
        super(other);
        this.maxGroupMembershipChanges = other.maxGroupMembershipChanges;
        this.canSupportNestedGroups = other.canSupportNestedGroups;
        if (other.desiredAttributes != null)
            this.desiredAttributes = new DesiredAttributes(other.desiredAttributes);
    }

    /**
     * Gets the Desired Attributes for the Application Provider
     * @return DesiredAttributes
     */
    public DesiredAttributes getDesiredAttributes() {
        return desiredAttributes;
    }

    /**
     * Sets the Desired Attributes for the Application Provider
     * @param desiredAttributes DesiredAttributes
     */
    public void setDesiredAttributes(DesiredAttributes desiredAttributes) {
        this.desiredAttributes = desiredAttributes;
    }

    /**
     * Gets whether the Application Provider can support nested groups.
     * @return true if the Application Provider supports nested groups
     */
    public Boolean getCanSupportNestedGroups() {
        return canSupportNestedGroups;
    }

    /**
     * Sets whether the Application Provider can support nested groups.
     * @param canSupportNestedGroups true if the Application Provider supports nested groups
     */
    public void setCanSupportNestedGroups(Boolean canSupportNestedGroups) {
        Objects.requireNonNull(canSupportNestedGroups, "canSupportNestedGroups must not be null");
        this.canSupportNestedGroups = canSupportNestedGroups;
    }

    /**
     * Gets the maximum number of group membership changes that can be included in a single SCIM request.
     * @return max changes
     */
    public Integer getMaxGroupMembershipChanges() {
        return maxGroupMembershipChanges;
    }

    /**
     * Sets the maximum number of group membership changes that can be included in a single SCIM request.
     * @param maxGroupMembershipChanges max changes
     */
    public void setMaxGroupMembershipChanges(Integer maxGroupMembershipChanges) {
        Objects.requireNonNull(maxGroupMembershipChanges, "maxGroupMembershipChanges must not be null");
        this.maxGroupMembershipChanges = maxGroupMembershipChanges;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
        builder.putAll(super.toJson());
        builder.put(JsonMember.SCIM_CAN_SUPPORT_NESTED_GROUPS, canSupportNestedGroups);
        builder.put(JsonMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES, maxGroupMembershipChanges);
        if (desiredAttributes != null)
            builder.putAll(desiredAttributes.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        super.hydrateFromJson(json);

        JsonObject desiredAttributesJson = json.getObject(JsonMember.DESIRED_ATTRIBUTES);
        if (desiredAttributesJson != null) {
            DesiredAttributes desiredAttributes = new DesiredAttributes(getFastFedConfiguration());
            desiredAttributes.hydrateFromJson(desiredAttributesJson);
            setDesiredAttributes(desiredAttributes);
        }

        Boolean canSupportNestedGroups = json.getBoolean(JsonMember.SCIM_CAN_SUPPORT_NESTED_GROUPS);
        if (canSupportNestedGroups == null) {
            canSupportNestedGroups = getFastFedConfiguration().SCIM_DEFAULT_VALUE_OF_NESTED_GROUP_SUPPORT;
        }
        setCanSupportNestedGroups(canSupportNestedGroups);

        Integer maxGroupMembershipChanges = json.getInteger(JsonMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES);
        if (maxGroupMembershipChanges == null) {
            maxGroupMembershipChanges = getFastFedConfiguration().SCIM_DEFAULT_VALUE_OF_MAX_GROUP_MEMBERSHIP_CHANGES;
        }
        setMaxGroupMembershipChanges(maxGroupMembershipChanges);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        // Validate DesiredAttributes
        validateRequiredObject(errorAccumulator, JsonMember.DESIRED_ATTRIBUTES, desiredAttributes);
        if (desiredAttributes != null) { desiredAttributes.validate(errorAccumulator); }

        // Validate MaxGroupMembershipChanges
        int upperLimit = getFastFedConfiguration().SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES;
        int lowerLimit = getFastFedConfiguration().SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES;
        if (maxGroupMembershipChanges > upperLimit) {
            errorAccumulator.add("Invalid value of '" + JsonMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES
                    + "', the received value " + maxGroupMembershipChanges
                    + " exceeds the upper limit of " + upperLimit
            );
        }
        else if (maxGroupMembershipChanges < lowerLimit) {
            errorAccumulator.add("Invalid value of '" + JsonMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES
                    + "', the received value " + maxGroupMembershipChanges
                    + " is below the lower limit of " + lowerLimit
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProviderMetadataExtension that = (ApplicationProviderMetadataExtension) o;
        return Objects.equals(desiredAttributes, that.desiredAttributes) &&
                Objects.equals(canSupportNestedGroups, that.canSupportNestedGroups) &&
                Objects.equals(maxGroupMembershipChanges, that.maxGroupMembershipChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), desiredAttributes, canSupportNestedGroups, maxGroupMembershipChanges);
    }
}
