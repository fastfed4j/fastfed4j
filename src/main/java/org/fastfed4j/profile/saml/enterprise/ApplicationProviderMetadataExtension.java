package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;

/**
 * Represents the extensions to the Application Provider Metadata defined in section 3.1.2
 * of the FastFed Enterprise SAML Profile.
 */
class ApplicationProviderMetadataExtension extends Metadata {

    private DesiredAttributes desiredAttributes;

    /**
     * Constructs an empty instance
     */
    public ApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
        super(configuration);
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

    @Override
    public JSONObject toJson() {
        JSONObject.Builder builder = new JSONObject.Builder(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
        builder.putAll(super.toJson());
        builder.putAll(desiredAttributes.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        super.hydrateFromJson(json);
        DesiredAttributes desiredAttributes = new DesiredAttributes(getFastFedConfiguration());
        desiredAttributes.hydrateFromJson(json.getObject(JSONMember.DESIRED_ATTRIBUTES));
        setDesiredAttributes(desiredAttributes);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JSONMember.DESIRED_ATTRIBUTES, desiredAttributes);
        if (desiredAttributes != null) { desiredAttributes.validate(errorAccumulator); }
    }
}
