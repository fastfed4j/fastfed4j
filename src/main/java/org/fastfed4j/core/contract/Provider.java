package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.CommonProviderMetadata;
import org.fastfed4j.core.metadata.DisplaySettings;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.ProviderContactInformation;

import java.util.Objects;

/**
 * Base class for the IdentityProvider and ApplicationProvider, implementing the features common across
 * both types of providers.
 */
public class Provider extends Metadata {

    private String entityId;
    private String providerDomain;
    private ProviderContactInformation providerContactInformation;
    private DisplaySettings displaySettings;

    /**
     * Constructs empty object
     */
    public Provider(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Constructs an instance from the source Common Provider Metadata, extracting the subset of information
     * necessary for long-term retention in a contract.
     * @param metadata the CommonProviderMetadata to extract from
     */
    public Provider(CommonProviderMetadata metadata) {
        this(metadata.getFastFedConfiguration());
        this.entityId = metadata.getEntityId();
        this.providerDomain = metadata.getProviderDomain();
        this.providerContactInformation = metadata.getProviderContactInformation();
        this.displaySettings = metadata.getDisplaySettings();
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public Provider(Provider other) {
        super(other);
        this.entityId = other.entityId;
        this.providerDomain = other.providerDomain;
        if (other.providerContactInformation != null)
            this.providerContactInformation = new ProviderContactInformation(other.providerContactInformation);
        if (other.displaySettings != null)
            this.displaySettings = new DisplaySettings(other.displaySettings);
    }

    /**
     * Get the entityId of the provider, as captured from the Provider Metadata
     * @return entityId
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Set the entityId of the provider, captured from the Provider Metadata
     * @param entityId
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Get the providerDomain, as captured from the Provider Metadata
     * @return providerDomain
     */
    public String getProviderDomain() {
        return providerDomain;
    }

    /**
     * Set the providerDomain of the provider, captured from the Provider Metadata
     * @param providerDomain
     */
    public void setProviderDomain(String providerDomain) {
        this.providerDomain = providerDomain;
    }

    /**
     * Get the contact information of the provider, as captured from the Provider Metadata
     * @return providerContactInformation
     */
    public ProviderContactInformation getProviderContactInformation() {
        return providerContactInformation;
    }

    /**
     * Set the contact information of the provider, captured from the Provider Metadata
     * @param providerContactInformation
     */
    public void setProviderContactInformation(ProviderContactInformation providerContactInformation) {
        this.providerContactInformation = providerContactInformation;
    }

    /**
     * Get the display settings of the provider, as captured from the Provider Metadata
     * @return displaySettings
     */
    public DisplaySettings getDisplaySettings() {
        return displaySettings;
    }

    /**
     * Set the display settings of the provider, captured from the Provider Metadata
     * @param displaySettings
     */
    public void setDisplaySettings(DisplaySettings displaySettings) {
        this.displaySettings = displaySettings;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        builder.putAll(super.toJson());
        builder.put(JsonMember.ENTITY_ID, entityId);
        builder.put(JsonMember.PROVIDER_DOMAIN, providerDomain);
        if (providerContactInformation != null)
            builder.putAll(providerContactInformation.toJson());
        if (displaySettings != null)
           builder.putAll(displaySettings.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);
        setEntityId( json.getString(JsonMember.ENTITY_ID));
        setProviderDomain( json.getString(JsonMember.PROVIDER_DOMAIN));

        JsonObject providerContactInformationJson = json.getObject(JsonMember.PROVIDER_CONTACT_INFORMATION);
        if (providerContactInformationJson != null) {
            ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
            providerContactInformation.hydrateFromJson(providerContactInformationJson);
            setProviderContactInformation(providerContactInformation);
        }

        JsonObject displaySettingsJson = json.getObject(JsonMember.DISPLAY_SETTINGS);
        if (displaySettingsJson != null) {
            DisplaySettings displaySettings = new DisplaySettings(getFastFedConfiguration());
            displaySettings.hydrateFromJson(displaySettingsJson);
            setDisplaySettings(displaySettings);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JsonMember.ENTITY_ID, entityId);
        validateRequiredString(errorAccumulator, JsonMember.PROVIDER_DOMAIN, providerDomain);
        validateRequiredObject(errorAccumulator, JsonMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JsonMember.DISPLAY_SETTINGS, displaySettings);

        // Validate the contents of each object
        if (providerContactInformation != null) { providerContactInformation.validate(errorAccumulator); }
        if (displaySettings != null) { displaySettings.validate(errorAccumulator); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return entityId.equals(provider.entityId) &&
                providerDomain.equals(provider.providerDomain) &&
                providerContactInformation.equals(provider.providerContactInformation) &&
                displaySettings.equals(provider.displaySettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, providerDomain, providerContactInformation, displaySettings);
    }
}
