package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;

import java.util.Objects;
import java.util.Set;

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
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.DISPLAY_SETTINGS);
        builder.put(JsonMember.DISPLAY_NAME, displayName);
        builder.put(JsonMember.LOGO_URI, logoUri);
        builder.put(JsonMember.ICON_URI, iconUri);
        builder.put(JsonMember.LICENSE, license);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.DISPLAY_SETTINGS);
        super.hydrateFromJson(json);
        this.setDisplayName( json.getString(JsonMember.DISPLAY_NAME));
        this.setIconUri( json.getString(JsonMember.ICON_URI));
        this.setLogoUri( json.getString(JsonMember.LOGO_URI));
        this.setLicense( json.getString(JsonMember.LICENSE));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
         validateRequiredString(errorAccumulator, JsonMember.DISPLAY_NAME, displayName);
         validateRequiredUrl(errorAccumulator, JsonMember.LICENSE, license);
         validateOptionalUrl(errorAccumulator, JsonMember.LOGO_URI, logoUri);
         validateOptionalUrl(errorAccumulator, JsonMember.ICON_URI, iconUri);

         //Verify the license is supported
        Set<String> supportedLicenses = getFastFedConfiguration().getSupportedLicenses();
        if (! supportedLicenses.contains(license)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Unsupported value for \"");
            builder.append(getFullyQualifiedName(JsonMember.LICENSE));
            builder.append("\". Must be ");
            if (supportedLicenses.size() == 1) {
                builder.append("\"");
                builder.append(supportedLicenses.iterator().next());
                builder.append("\"");
            } else {
                builder.append(" one of ");
                builder.append( String.join(",", supportedLicenses));
            }
            builder.append(" (received: \"");
            builder.append(license);
            builder.append("\")");
            errorAccumulator.add(builder.toString());
        }
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
