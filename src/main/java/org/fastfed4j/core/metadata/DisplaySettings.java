package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;

import java.util.Objects;

/**
 * Represents the Display Settings metadata, as defined in section 3.3.2 of the FastFed Core specification.
 */
public class DisplaySettings extends Metadata {
    private String displayName;
    private String logoUri;
    private String iconUri;
    private String license;

    /**
     * Constructs an empty instance
     */
    public DisplaySettings(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public DisplaySettings(DisplaySettings other) {
        super(other);
        this.displayName = other.displayName;
        this.logoUri = other.logoUri;
        this.iconUri = other.iconUri;
        this.license = other.license;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.DISPLAY_SETTINGS);
        super.hydrateFromJson(json);
        this.setDisplayName( json.getString(JSONMember.DISPLAY_NAME));
        this.setIconUri( json.getString(JSONMember.ICON_URI));
        this.setLogoUri( json.getString(JSONMember.LOGO_URI));
        this.setLicense( json.getString(JSONMember.LICENSE));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
         validateRequiredString(errorAccumulator, JSONMember.DISPLAY_NAME, this.displayName);
         validateRequiredUrl(errorAccumulator, JSONMember.LICENSE, this.license);
         validateOptionalUrl(errorAccumulator, JSONMember.LOGO_URI, this.logoUri);
         validateOptionalUrl(errorAccumulator, JSONMember.ICON_URI, this.iconUri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplaySettings that = (DisplaySettings) o;
        return displayName.equals(that.displayName) &&
                Objects.equals(logoUri, that.logoUri) &&
                Objects.equals(iconUri, that.iconUri) &&
                license.equals(that.license);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, logoUri, iconUri, license);
    }
}
