package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.util.FormattingUtils;
import org.fastfed4j.core.util.ValidationUtils;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the Application Provider Metadata defined in section 3.3.8 of the FastFed Core specification.
 */
public class ApplicationProviderMetadata extends CommonProviderMetadata {
    private static final FormattingUtils formattingUtils = new FormattingUtils();
    private static final ValidationUtils validationUtils = new ValidationUtils();

    private String handshakeRegisterUri;

    /**
     * Constructs an empty instance
     */
    public ApplicationProviderMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public ApplicationProviderMetadata(ApplicationProviderMetadata other) {
        super(other);
        this.handshakeRegisterUri = other.handshakeRegisterUri;
    }

    /**
     * Get the Handshake Register URI to perform section 7.2.3.1 of the FastFed Core specification
     * @return uri
     */
    public String getHandshakeRegisterUri() {
        return handshakeRegisterUri;
    }

    /**
     * Set the Handshake Register URI to perform section 7.2.3.1 of the FastFed Core specification
     * @param handshakeRegisterUri uri
     */
    public void setHandshakeRegisterUri(String handshakeRegisterUri) {
        this.handshakeRegisterUri = handshakeRegisterUri;
    }

    /**
     * Convenience method to get the metadata extensions defined by the EnterpriseSAML profile.
     * @return EnterpriseSAML metadata extensions
     */
    public EnterpriseSAML.ApplicationProviderMetadataExtension getEnterpriseSamlExtension() {
        return getMetadataExtension(EnterpriseSAML.ApplicationProviderMetadataExtension.class, AuthenticationProfile.ENTERPRISE_SAML.getUrn());
    }

    /**
     * Convenience method to get the metadata extensions defined by the EnterpriseSCIM profile.
     * @return EnterpriseSCIM metadata extensions
     */
    public EnterpriseSCIM.ApplicationProviderMetadataExtension getEnterpriseScimExtension() {
        return getMetadataExtension(EnterpriseSCIM.ApplicationProviderMetadataExtension.class, ProvisioningProfile.ENTERPRISE_SCIM.getUrn());
    }

    /**
     * Retrieve Application Provider Metadata from a URL endpoint
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param url the endpoint which hosts the metadata
     * @return ApplicationProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     * @throws FastFedSecurityException if the metadata content violates the FastFed security requirements
     */
    public static ApplicationProviderMetadata fromRemoteEndpoint(FastFedConfiguration configuration, String url)
            throws InvalidMetadataException, FastFedSecurityException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(url, "url must not be null");
        String jsonString = ""; //TODO - implement the HTTP query
        ApplicationProviderMetadata metadata = fromJson(configuration, jsonString);
        validationUtils.assertProviderDomainIsValid(url, metadata.getProviderDomain());
        return metadata;
    }

    /**
     * Map a JSON document into an instance of this class
     * @param configuration FastFed Configuration that controls the SDK behavior
     * @param jsonString json document
     * @return ApplicationProviderMetadata
     * @throws InvalidMetadataException if the metadata is malformed
     */
    public static ApplicationProviderMetadata fromJson(FastFedConfiguration configuration, String jsonString)
            throws InvalidMetadataException
    {
        Objects.requireNonNull(configuration, "FastFedConfiguration must not be null");
        Objects.requireNonNull(jsonString, "json must not be null");
        ApplicationProviderMetadata metadata = new ApplicationProviderMetadata(configuration);
        metadata.hydrateAndValidate(jsonString);
        return metadata;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.APPLICATION_PROVIDER);
        builder.putAll(super.toJson());
        builder.put(JsonMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);
        for (Metadata obj : getAllMetadataExtensions().values()) {
            builder.putAll(obj.toJson());
        }
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.APPLICATION_PROVIDER);
        super.hydrateFromJson(json);
        hydrateExtensions(json, Profile.ExtensionType.ApplicationProviderMetadata);
        setHandshakeRegisterUri(json.getString(JsonMember.FASTFED_HANDSHAKE_REGISTER_URI));
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        super.validate(errorAccumulator);
        validateRequiredUrl(errorAccumulator, JsonMember.FASTFED_HANDSHAKE_REGISTER_URI, handshakeRegisterUri);

        if (getCapabilities() != null) {
            validateExtensions(errorAccumulator, getCapabilities().getAllKnownProfileUrns(), Profile.ExtensionType.ApplicationProviderMetadata);

            // Ensure that Desired Attributes contains definitions for each schema grammar within
            // the Capabilities, as required by section 3.3.5 of the FastFed Core specification.
            // Note that this validation can only examine the profiles defined within this SDK. Any externally
            // defined profiles are responsible for performing their own validation.
            String enterpriseSamlUrn = AuthenticationProfile.ENTERPRISE_SAML.getUrn();
            String enterpriseScimUrn = ProvisioningProfile.ENTERPRISE_SCIM.getUrn();
            Set<String> schemaGrammars = getCapabilities().getSchemaGrammars();
            for (String profileUrn : getFastFedConfiguration().getProfileRegistry().getAllUrns()) {
                if (profileUrn.equals(enterpriseSamlUrn)) {
                    String attributeName = JsonMember.APPLICATION_PROVIDER + "." + enterpriseSamlUrn + "." + JsonMember.DESIRED_ATTRIBUTES;
                    DesiredAttributes desiredAttributes = getEnterpriseSamlExtension() == null ? null : getEnterpriseSamlExtension().getDesiredAttributes();
                    ensureDesiredAttributesContainsAllSchemaGrammars(errorAccumulator, attributeName, schemaGrammars, desiredAttributes);
                } else if (profileUrn.equals(enterpriseScimUrn)) {
                    String attributeName = JsonMember.APPLICATION_PROVIDER + "." + enterpriseScimUrn + "." + JsonMember.DESIRED_ATTRIBUTES;
                    DesiredAttributes desiredAttributes = getEnterpriseScimExtension() == null ? null : getEnterpriseScimExtension().getDesiredAttributes();
                    ensureDesiredAttributesContainsAllSchemaGrammars(errorAccumulator, attributeName, schemaGrammars, desiredAttributes);
                }
            }
        }
    }

    private void ensureDesiredAttributesContainsAllSchemaGrammars(ErrorAccumulator errorAccumulator,
                                                                  String attributeName,
                                                                  Set<String> expectedSchemaGrammars,
                                                                  DesiredAttributes desiredAttributes)
    {
        if (desiredAttributes == null)
            return;

        Set<String> actualSchemaGrammars = desiredAttributes.getAllSchemaGrammarUrns();
        Set<String> missingGrammars = new HashSet<>();
        for (String expectedUrn : expectedSchemaGrammars) {
            if (! actualSchemaGrammars.contains(expectedUrn))
                missingGrammars.add(expectedUrn);
        }

        if (! missingGrammars.isEmpty()) {
            errorAccumulator.add(
                    "Invalid value for \"" + attributeName + "\". " +
                    "Must contain entries for all schema_grammars defined in the Capabilities. " +
                    "Missing: " + formattingUtils.joinAndQuote(missingGrammars)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProviderMetadata metadata = (ApplicationProviderMetadata) o;
        return handshakeRegisterUri.equals(metadata.handshakeRegisterUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), handshakeRegisterUri);
    }
}
