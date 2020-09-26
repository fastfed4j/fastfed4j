package org.fastfed4j.profile.saml.enterprise;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.metadata.DesiredAttributes;
import org.fastfed4j.core.metadata.Metadata;
import org.fastfed4j.core.metadata.UserAttribute;
import org.fastfed4j.core.util.FormattingUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the extensions to the Application Provider Metadata defined in section 3.1.2
 * of the FastFed Enterprise SAML Profile.
 */
class ApplicationProviderMetadataExtension extends Metadata {

    // As per Section 4.1.1 of the Enterprise SAML Profile
    private static final Set<String> VALID_SAML_SUBJECTS = Set.of(
            "externalId",
            "emails[primary eq true].value",
            "userName"
    );

    // As per Section 4.1.2 of the Enterprise SAML Profile
    private static final Set<String> VALID_SAML_ATTRIBUTES = Set.of(
            "externalId",
            "userName",
            "displayName",
            "name.givenName",
            "name.familyName",
            "name.middleName",
            "emails[primary eq true].value",
            "phoneNumbers[primary eq true].value"
    );

    private static final FormattingUtils formattingUtils = new FormattingUtils();

    private UserAttribute samlSubject;
    private DesiredAttributes desiredAttributes;

    /**
     * Constructs an empty instance
     */
    public ApplicationProviderMetadataExtension(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     * @param other object to copy
     */
    public ApplicationProviderMetadataExtension(ApplicationProviderMetadataExtension other) {
        super(other);
        if (desiredAttributes != null)
            this.desiredAttributes = new DesiredAttributes(other.getDesiredAttributes());
    }

    /**
     * Gets the SAML Subject to use with the Application Provider.
     * @return UserAttribute indicating the attribute to use as the SAML Subject
     */
    public UserAttribute getSamlSubject() { return samlSubject; }

    /**
     * Sets the SAML Subject to use with the Application Provider.
     * @param userAttribute the attribute to use as the SAML Subject
     */
    public void setSamlSubject(UserAttribute userAttribute) {
        this.samlSubject = userAttribute;
    }

    /**
     * Gets the Desired Attributes for the Application Provider
     * @return DesiredAttributes
     */
    public DesiredAttributes getDesiredAttributes() { return desiredAttributes; }

    /**
     * Sets the Desired Attributes for the Application Provider
     * @param desiredAttributes DesiredAttributes
     */
    public void setDesiredAttributes(DesiredAttributes desiredAttributes) {
        this.desiredAttributes = desiredAttributes;
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(AuthenticationProfile.ENTERPRISE_SAML.getUrn());
        builder.putAll(super.toJson());
        if (samlSubject != null)
            builder.putAll(samlSubject.toJson());
        if (desiredAttributes != null)
            builder.putAll(desiredAttributes.toJson());
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        super.hydrateFromJson(json);

        JsonObject samlSubjectJson = json.getObject(JsonMember.SAML_SUBJECT);
        if (samlSubjectJson != null) {
            UserAttribute samlSubject = new UserAttribute(getFastFedConfiguration(), JsonMember.SAML_SUBJECT);
            samlSubject.hydrateFromJson(samlSubjectJson);
            setSamlSubject(samlSubject);
        }

        JsonObject desiredAttributesJson = json.getObject(JsonMember.DESIRED_ATTRIBUTES);
        if (desiredAttributesJson != null) {
            DesiredAttributes desiredAttributes = new DesiredAttributes(getFastFedConfiguration());
            desiredAttributes.hydrateFromJson(desiredAttributesJson);
            setDesiredAttributes(desiredAttributes);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        validateRequiredObject(errorAccumulator, JsonMember.SAML_SUBJECT, samlSubject);
        validateRequiredObject(errorAccumulator, JsonMember.DESIRED_ATTRIBUTES, desiredAttributes);

        if (samlSubject != null) {
            samlSubject.validate(errorAccumulator);
            ensureSubjectIsValid(errorAccumulator, samlSubject);
        }

        if (desiredAttributes != null) {
            desiredAttributes.validate(errorAccumulator);
            ensureUserAttributesAreValid(errorAccumulator, desiredAttributes);
            ensureNoGroupAttributesAreSet(errorAccumulator, desiredAttributes);
        }
    }

    private void ensureSubjectIsValid(ErrorAccumulator errorAccumulator, UserAttribute samlSubject) {
        for (SchemaGrammar schemaGrammar : samlSubject.getAllSchemaGrammars()) {
            if (!schemaGrammar.equals(SchemaGrammar.SCIM)) {
                // If a new Schema Grammar is added in the future, this will alert that the
                // validation needs updating.
                throw new RuntimeException("Missing handler for schema grammar " + schemaGrammar);
            }

            inspectForInvalidAttributes(
                    errorAccumulator,
                    schemaGrammar,
                    JsonMember.SAML_SUBJECT,
                    VALID_SAML_SUBJECTS,
                    Set.of(samlSubject.forSchemaGrammar(schemaGrammar))
            );
        }
    }

    private void ensureUserAttributesAreValid(ErrorAccumulator errorAccumulator, DesiredAttributes desiredAttributes) {
        for (SchemaGrammar schemaGrammar : desiredAttributes.getAllSchemaGrammars()) {
            if (!schemaGrammar.equals(SchemaGrammar.SCIM)) {
                // If a new Schema Grammar is added in the future, this will alert that the
                // validation needs updating.
                throw new RuntimeException("Missing handler for schema grammar " + schemaGrammar);
            }

            inspectForInvalidAttributes(
                    errorAccumulator,
                    schemaGrammar,
                    JsonMember.REQUIRED_USER_ATTRIBUTES,
                    VALID_SAML_ATTRIBUTES,
                    desiredAttributes.forSchemaGrammar(schemaGrammar).getRequiredUserAttributes()
            );

            inspectForInvalidAttributes(
                    errorAccumulator,
                    schemaGrammar,
                    JsonMember.OPTIONAL_USER_ATTRIBUTES,
                    VALID_SAML_ATTRIBUTES,
                    desiredAttributes.forSchemaGrammar(schemaGrammar).getOptionalUserAttributes()
            );
        }
    }

    private void inspectForInvalidAttributes(ErrorAccumulator errorAccumulator,
                                             SchemaGrammar schemaGrammar,
                                             String memberName,
                                             Set<String> allowedAttributes,
                                             Set<String> actualAttributes)
    {
        for (String attribute : actualAttributes) {
            if (!allowedAttributes.contains(attribute)) {
                String fullyQualifiedName = getFullyQualifiedName(memberName + "." + schemaGrammar);
                errorAccumulator.add(
                        "Illegal value in \"" + fullyQualifiedName + "\". " +
                        "Received: \"" + attribute + "\"" + ". " +
                        "Must be one of " + formattingUtils.joinAndQuote(allowedAttributes) + "."
                );
            }
        }
    }

    /**
     *  As per section 3.1.2 of the FastFed Enterprise SAML Profile, the DesiredAttributes must NOT contain
     *  any group attributes. This method verifies compliance.
     */
    private void ensureNoGroupAttributesAreSet(ErrorAccumulator errorAccumulator, DesiredAttributes desiredAttributes) {
        for (SchemaGrammar schemaGrammar : desiredAttributes.getAllSchemaGrammars()) {
            Set<String> requiredGroupAttributes = desiredAttributes.forSchemaGrammar(schemaGrammar).getRequiredGroupAttributes();
            Set<String> optionalGroupAttributes = desiredAttributes.forSchemaGrammar(schemaGrammar).getOptionalGroupAttributes();
            if (containsValues(requiredGroupAttributes)) {
                errorAccumulator.add(
                        "Illegal value for \"" +
                        getFullyQualifiedName(JsonMember.DESIRED_ATTRIBUTES + "." + schemaGrammar + "." + JsonMember.REQUIRED_GROUP_ATTRIBUTES) +
                        "\". The FastFed Enterprise SAML Profile requires that group attributes be omitted."
                );
            }
            if (containsValues(optionalGroupAttributes)) {
                errorAccumulator.add(
                        "Illegal value for \"" +
                        getFullyQualifiedName(JsonMember.DESIRED_ATTRIBUTES + "." + schemaGrammar + "." + JsonMember.OPTIONAL_GROUP_ATTRIBUTES) +
                        "\". The FastFed Enterprise SAML Profile requires that group attributes be omitted."
                );
            }
        }
    }

    /**
     * Local helper for validating desired attribute
     */
    private boolean containsValues(Collection<String> attributes) {
        return !(attributes == null || attributes.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProviderMetadataExtension that = (ApplicationProviderMetadataExtension) o;
        return Objects.equals(desiredAttributes, that.desiredAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), desiredAttributes);
    }
}
