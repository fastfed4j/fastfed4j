package org.fastfed4j.profile.scim.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;

/**
 * Represents the extensions to the Application Provider Metadata defined in section 3.1.2
 * of the FastFed Enterprise SCIM Profile.
 */
class ApplicationProviderMetadataExtension extends Metadata {

    private DesiredAttributes desiredAttributes;
    private boolean canSupportNestedGroups;
    private long maxGroupMembershipChanges;

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
     * Gets the Desired Attributes for the Application Provider
     * @return DesiredAttributes
     */
    public DesiredAttributes getDesiredAttributes() { return desiredAttributes; }

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
    public boolean getCanSupportNestedGroups() {
        return canSupportNestedGroups;
    }

    /**
     * Sets whether the Application Provider can support nested groups.
     * @param canSupportNestedGroups true if the Application Provider supports nested groups
     */
    public void setCanSupportNestedGroups(boolean canSupportNestedGroups) {
        this.canSupportNestedGroups = canSupportNestedGroups;
    }

    /**
     * Gets the maximum number of group membership changes that can be included in a single SCIM request.
     * @return max changes
     */
    public long getMaxGroupMembershipChanges() {
        return maxGroupMembershipChanges;
    }

    /**
     * Sets the maximum number of group membership changes that can be included in a single SCIM request.
     * @param maxGroupMembershipChanges max changes
     */
    public void setMaxGroupMembershipChanges(long maxGroupMembershipChanges) {
        this.maxGroupMembershipChanges = maxGroupMembershipChanges;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        super.hydrateFromJson(json);
        DesiredAttributes desiredAttributes = new DesiredAttributes(getFastFedConfiguration());
        desiredAttributes.hydrateFromJson(json.getObject(JSONMember.DESIRED_ATTRIBUTES));
        setDesiredAttributes(desiredAttributes);

        if (json.containsValueForMember(JSONMember.SCIM_CAN_SUPPORT_NESTED_GROUPS)) {
            setCanSupportNestedGroups( json.getBoolean(JSONMember.SCIM_CAN_SUPPORT_NESTED_GROUPS));
        }
        else {
            setCanSupportNestedGroups( getFastFedConfiguration().SCIM_DEFAULT_VALUE_OF_NESTED_GROUP_SUPPORT);
        }

        if (json.containsValueForMember(JSONMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES)) {
            setMaxGroupMembershipChanges( json.getLong(JSONMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES));
        }
        else {
            setMaxGroupMembershipChanges( getFastFedConfiguration().SCIM_DEFAULT_VALUE_OF_MAX_GROUP_MEMBERSHIP_CHANGES);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        // Validate DesiredAttributes
        validateRequiredObject(errorAccumulator, JSONMember.DESIRED_ATTRIBUTES, desiredAttributes);
        if (desiredAttributes != null) { desiredAttributes.validate(errorAccumulator); }

        // Validate MaxGroupMembershipChanges
        int upperLimit = getFastFedConfiguration().SCIM_UPPER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES;
        int lowerLimit = getFastFedConfiguration().SCIM_LOWER_LIMIT_OF_MAX_ALLOWED_GROUP_MEMBERSHIP_CHANGES;
        if (maxGroupMembershipChanges > upperLimit) {
            errorAccumulator.add("Invalid value of '" + JSONMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES
                    + "', the received value " + maxGroupMembershipChanges
                    + " exceeds the upper limit of " + upperLimit
            );
        }
        else if (maxGroupMembershipChanges < lowerLimit) {
            errorAccumulator.add("Invalid value of '" + JSONMember.SCIM_MAX_GROUP_MEMBERSHIP_CHANGES
                    + "', the received value " + maxGroupMembershipChanges
                    + " is below the lower limit of " + lowerLimit
            );
        }
    }
}
