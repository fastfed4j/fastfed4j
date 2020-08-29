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
 * Represents the extensions to the RegistrationRequest and RegistrationResponse messages, as defined by
 * section 3.2 of the FastFed Enterprise SAML Profile.
 */
class RegistrationExtension extends Metadata {
    private String samlMetadataUri;

    /**
     * Constructs an empty instance
     */
    public RegistrationExtension(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public RegistrationExtension(RegistrationExtension other) {
        super(other);
        this.samlMetadataUri = other.samlMetadataUri;
    }

    /**
     * Gets the SAML Metadata URI for the Provider
     * @return saml metadata uri
     */
    public String getSamlMetadataUri() {
        return samlMetadataUri;
    }

    /**
     * Sets the SAML Metadata URI for the Provider
     * @param samlMetadataUri saml metadata uri
     */
    public void setSamlMetadataUri(String samlMetadataUri) {
        this.samlMetadataUri = samlMetadataUri;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
        builder.putAll(super.toJson());
        builder.put(JsonMember.SAML_METADATA_URI, samlMetadataUri);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        setSamlMetadataUri(json.getString(JsonMember.SAML_METADATA_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JsonMember.SAML_METADATA_URI, getSamlMetadataUri());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegistrationExtension that = (RegistrationExtension) o;
        return Objects.equals(samlMetadataUri, that.samlMetadataUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), samlMetadataUri);
    }
}
