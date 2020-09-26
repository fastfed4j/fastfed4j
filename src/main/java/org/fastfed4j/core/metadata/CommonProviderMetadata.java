package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;

import java.util.*;

/**
 * Represents the Common Provider Metadata, as defined in section 3.3.6 of the FastFed Core specification.
 */
abstract public class CommonProviderMetadata extends Metadata {
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
        if (other.providerContactInformation != null)
            this.providerContactInformation = new ProviderContactInformation(other.providerContactInformation);
        if (other.displaySettings != null)
            this.displaySettings = new DisplaySettings(other.displaySettings);
        if (other.capabilities != null)
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
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        builder.putAll(super.toJson());
        builder.put(JsonMember.ENTITY_ID, entityId);
        builder.put(JsonMember.PROVIDER_DOMAIN, providerDomain);
        if (capabilities != null)
            builder.putAll(capabilities.toJson());
        if (displaySettings != null)
            builder.putAll(displaySettings.toJson());
        if (providerContactInformation != null)
            builder.putAll(providerContactInformation.toJson());

        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject jsonObj) {
        if (jsonObj == null) return;
        super.hydrateFromJson(jsonObj);
        this.setEntityId(jsonObj.getString(JsonMember.ENTITY_ID));
        this.setProviderDomain(jsonObj.getString(JsonMember.PROVIDER_DOMAIN));

        JsonObject providerContactInformationJson = jsonObj.getObject(JsonMember.PROVIDER_CONTACT_INFORMATION);
        if (providerContactInformationJson != null) {
            ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
            providerContactInformation.hydrateFromJson(providerContactInformationJson);
            setProviderContactInformation(providerContactInformation);
        }

        JsonObject displaySettingsJson = jsonObj.getObject(JsonMember.DISPLAY_SETTINGS);
        if (displaySettingsJson != null) {
            DisplaySettings displaySettings = new DisplaySettings(getFastFedConfiguration());
            displaySettings.hydrateFromJson(displaySettingsJson);
            setDisplaySettings(displaySettings);
        }

        JsonObject capabilitiesJson = jsonObj.getObject(JsonMember.CAPABILITIES);
        if (capabilitiesJson != null) {
            Capabilities capabilities = new Capabilities(getFastFedConfiguration());
            capabilities.hydrateFromJson(capabilitiesJson);
            setCapabilities(capabilities);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        // Ensure required members are non-null
        validateRequiredString(errorAccumulator, JsonMember.ENTITY_ID, entityId);
        validateRequiredString(errorAccumulator, JsonMember.PROVIDER_DOMAIN, providerDomain);
        validateRequiredObject(errorAccumulator, JsonMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JsonMember.DISPLAY_SETTINGS, displaySettings);
        validateRequiredObject(errorAccumulator, JsonMember.CAPABILITIES, capabilities);

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
