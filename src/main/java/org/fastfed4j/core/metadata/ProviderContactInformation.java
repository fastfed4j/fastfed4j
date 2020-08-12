package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;

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
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.PROVIDER_CONTACT_INFORMATION);
        super.hydrateFromJson(json);
        this.setOrganization( json.getString(JSONMember.ORGANIZATION));
        this.setEmail( json.getString(JSONMember.EMAIL));
        this.setPhone( json.getString(JSONMember.PHONE));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredString(errorAccumulator, JSONMember.ORGANIZATION, this.organization);
        validateRequiredString(errorAccumulator, JSONMember.PHONE, this.phone);
        validateRequiredString(errorAccumulator, JSONMember.EMAIL, this.email);
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