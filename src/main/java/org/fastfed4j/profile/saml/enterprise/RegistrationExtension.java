package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.Metadata;

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
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        setSamlMetadataUri(json.getString(JSONMember.SAML_METADATA_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredUrl(errorAccumulator, JSONMember.SAML_METADATA_URI, getSamlMetadataUri());
    }
}
