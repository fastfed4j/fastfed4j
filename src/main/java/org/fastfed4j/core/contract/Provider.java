package org.fastfed4j.core.contract;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.metadata.CommonProviderMetadata;
import org.fastfed4j.core.metadata.DisplaySettings;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.ProviderContactInformation;

import java.util.Map;
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
        for (Map.Entry<String, Metadata> entry : metadata.getAllMetadataExtensions().entrySet()) {
            this.addMetadataExtension(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public Provider(Provider other) {
        super(other);
        this.entityId = other.entityId;
        this.providerDomain = other.providerDomain;
        this.providerContactInformation = new ProviderContactInformation(other.providerContactInformation);
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
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        super.hydrateFromJson(json);

        setEntityId( json.getString(JSONMember.ENTITY_ID));
        setProviderDomain( json.getString(JSONMember.PROVIDER_DOMAIN));

        ProviderContactInformation providerContactInformation = new ProviderContactInformation(getFastFedConfiguration());
        providerContactInformation.hydrateFromJson( json.getObject(JSONMember.PROVIDER_CONTACT_INFORMATION));
        setProviderContactInformation(providerContactInformation);

        DisplaySettings displaySettings = new DisplaySettings(getFastFedConfiguration());
        displaySettings.hydrateFromJson( json.getObject(JSONMember.DISPLAY_SETTINGS));
        setDisplaySettings(displaySettings);
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JSONMember.ENTITY_ID, entityId);
        validateRequiredString(errorAccumulator, JSONMember.PROVIDER_DOMAIN, providerDomain);
        validateRequiredObject(errorAccumulator, JSONMember.PROVIDER_CONTACT_INFORMATION, providerContactInformation);
        validateRequiredObject(errorAccumulator, JSONMember.DISPLAY_SETTINGS, displaySettings);

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
