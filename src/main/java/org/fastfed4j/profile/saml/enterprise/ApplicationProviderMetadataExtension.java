package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;

import java.util.Objects;

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
     * Copy constructor
     * @param other object to copy
     */
    public ApplicationProviderMetadataExtension(ApplicationProviderMetadataExtension other) {
        super(other);
        if (desiredAttributes != null)
            this.desiredAttributes = new DesiredAttributes(other.getDesiredAttributes());
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
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
        builder.putAll(super.toJson());
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
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JsonMember.DESIRED_ATTRIBUTES, desiredAttributes);
        if (desiredAttributes != null) { desiredAttributes.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProviderMetadataExtension that = (ApplicationProviderMetadataExtension) o;
        return Objects.equals(desiredAttributes, that.desiredAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), desiredAttributes);
    }
}
