package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;

import java.util.Objects;

/**
 * Represents the Provider Contact Information metadata, as defined in section 3.3.3 of the FastFed Core specification.
 */
public class ProviderContactInformation extends Metadata {
    private String organization;
    private String phone;
    private String email;

    /**
     * Constructs an empty instance
     */
    public ProviderContactInformation(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public ProviderContactInformation(ProviderContactInformation other) {
        super(other);
        this.organization = other.organization;
        this.phone = other.phone;
        this.email = other.email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.PROVIDER_CONTACT_INFORMATION);
        builder.putAll(super.toJson());
        builder.put(JsonMember.ORGANIZATION, organization);
        builder.put(JsonMember.PHONE, phone);
        builder.put(JsonMember.EMAIL, email);
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.PROVIDER_CONTACT_INFORMATION);
        super.hydrateFromJson(json);
        this.setOrganization( json.getString(JsonMember.ORGANIZATION));
        this.setEmail( json.getString(JsonMember.EMAIL));
        this.setPhone( json.getString(JsonMember.PHONE));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JsonMember.ORGANIZATION, this.organization);
        validateRequiredString(errorAccumulator, JsonMember.PHONE, this.phone);
        validateRequiredString(errorAccumulator, JsonMember.EMAIL, this.email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderContactInformation that = (ProviderContactInformation) o;
        return organization.equals(that.organization) &&
                phone.equals(that.phone) &&
                email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organization, phone, email);
    }
}