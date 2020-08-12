package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.*;

/**
 * Represents the Common Provider Metadata, as defined in section 3.3.6 of the FastFed Core specification.
 */
public abstract class CommonProviderMetadata extends Metadata {
    private String entityId;
    private String providerDomain;
    private ProviderContactInformation providerContactInformation;
    private DisplaySettings displaySettings;
    private Capabilities capabilities;

    /**
     * Constructs an empty instance
     */
    public CommonProviderMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public CommonProviderMetadata(CommonProviderMetadata other) {
        super(other);
        this.entityId = other.entityId;
        this.providerDomain = other.providerDomain;
        this.providerContactInformation = new ProviderContactInformation(other.providerContactInformation);
        this.displaySettings = new DisplaySettings(other.displaySettings);
        this.capabilities = new Capabilities(other.capabilities);
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProviderDomain() {
        return providerDomain;
    }

    public void setProviderDomain(String providerDomain) {
        if (providerDomain != null) {
            this.providerDomain = providerDomain.toLowerCase();
        }
    }

    public ProviderContactInformation getProviderContactInformation() {
        return providerContactInformation;
    }

    public void setProviderContactInformation(ProviderContactInformation providerContactInformation) {
        this.providerContactInformation = providerContactInformation;
    }

    public DisplaySettings getDisplaySettings() {
        return displaySettings;
    }

    public void setDisplaySettings(DisplaySettings displaySettings) {
        this.displaySettings = displaySettings;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public void hydrateFromJson(JSONObject jsonObj) {
        if (jsonObj == null) return;
        super.hydrateFromJson(jsonObj);
        
        this.setEntityId(jsonObj.getString(JSONMember.ENTITY_ID));
        this.setProviderDomain(jsonObj.getString(JSONMember.PROVIDER_DOMAIN));

        ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
        providerContactInformation.hydrateFromJson(jsonObj.getObject(JSONMember.PROVIDER_CONTACT_INFORMATION));
        this.setProviderContactInformation(providerContactInformation);

        DisplaySettings displaySettings = new DisplaySettings(getFastFedConfiguration());
        displaySettings.hydrateFromJson(jsonObj.getObject(JSONMember.DISPLAY_SETTINGS));
        this.setDisplaySettings(displaySettings);

        Capabilities capabilities = new Capabilities(getFastFedConfiguration());
        capabilities.hydrateFromJson(jsonObj.getObject(JSONMember.CAPABILITIES));
        this.setCapabilities(capabilities);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        // Ensure required members are non-null
        validateRequiredString(errorAccumulator, JSONMember.ENTITY_ID, entityId);
        validateRequiredString(errorAccumulator, JSONMember.PROVIDER_DOMAIN, providerDomain);
        validateRequiredObject(errorAccumulator, JSONMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JSONMember.DISPLAY_SETTINGS, displaySettings);
        validateRequiredObject(errorAccumulator, JSONMember.CAPABILITIES, capabilities);

        // Validate the contents of each object
        if (providerContactInformation != null) { providerContactInformation.validate(errorAccumulator); }
        if (displaySettings != null) { displaySettings.validate(errorAccumulator); }
        if (capabilities != null) { capabilities.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommonProviderMetadata that = (CommonProviderMetadata) o;
        return entityId.equals(that.entityId) &&
                providerDomain.equals(that.providerDomain) &&
                providerContactInformation.equals(that.providerContactInformation) &&
                displaySettings.equals(that.displaySettings) &&
                capabilities.equals(that.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entityId, providerDomain, providerContactInformation, displaySettings, capabilities);
    }
}
