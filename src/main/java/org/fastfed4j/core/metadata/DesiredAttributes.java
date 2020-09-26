package org.fastfed4j.core.metadata;

import java.util.*;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;

/**
 * Represents the Desired Attributes metadata, as defined in section 3.3.5 of the FastFed Core specification.
 */
public class DesiredAttributes extends Metadata {

    private final Map<SchemaGrammar, ForSchemaGrammar> desiredAttributes= new HashMap<>();
    private final SchemaGrammar preferredSchemaGrammar;

    /**
     * The Desired Attributes metadata is an array containing one or more representations of the
     * desired attributes in a particular schema grammar (SCIM 2.0 being the recommended grammar).
     * This class represents one element of that array, containing the desired attributes for the
     * specific grammar.
     */
    public class ForSchemaGrammar {
        private SchemaGrammar schemaGrammar;
        private Set<String> requiredUserAttributes;
        private Set<String> optionalUserAttributes;
        private Set<String> requiredGroupAttributes;
        private Set<String> optionalGroupAttributes;

        public ForSchemaGrammar() {
        }

        public ForSchemaGrammar(ForSchemaGrammar other) {
            this.schemaGrammar = other.schemaGrammar;
            this.requiredUserAttributes = other.requiredUserAttributes;
            this.optionalUserAttributes = other.optionalUserAttributes;
            this.requiredGroupAttributes = other.requiredGroupAttributes;
            this.optionalGroupAttributes = other.optionalGroupAttributes;
        }

        public SchemaGrammar getSchemaGrammar() { return schemaGrammar; }

        public void setSchemaGrammar(SchemaGrammar schemaGrammar) {
            this.schemaGrammar = schemaGrammar;
        }

        public Set<String> getRequiredUserAttributes() {
            return requiredUserAttributes;
        }

        public void setRequiredUserAttributes(Set<String> requiredUserAttributes) {
            this.requiredUserAttributes = requiredUserAttributes;
        }

        public Set<String> getOptionalUserAttributes() {
            return optionalUserAttributes;
        }

        public void setOptionalUserAttributes(Set<String> optionalUserAttributes) {
            this.optionalUserAttributes = optionalUserAttributes;
        }

        public Set<String> getRequiredGroupAttributes() {
            return requiredGroupAttributes;
        }

        public void setRequiredGroupAttributes(Set<String> requiredGroupAttributes) {
            this.requiredGroupAttributes = requiredGroupAttributes;
        }

        public Set<String> getOptionalGroupAttributes() {
            return optionalGroupAttributes;
        }

        public void setOptionalGroupAttributes(Set<String> optionalGroupAttributes) {
            this.optionalGroupAttributes = optionalGroupAttributes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ForSchemaGrammar that = (ForSchemaGrammar) o;
            return schemaGrammar == that.schemaGrammar &&
                    requiredUserAttributes.equals(that.requiredUserAttributes) &&
                    Objects.equals(optionalUserAttributes, that.optionalUserAttributes) &&
                    Objects.equals(requiredGroupAttributes, that.requiredGroupAttributes) &&
                    Objects.equals(optionalGroupAttributes, that.optionalGroupAttributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(schemaGrammar, requiredUserAttributes, optionalUserAttributes, requiredGroupAttributes, optionalGroupAttributes);
        }
    }

    /**
     * Constructs an empty instance
     */
    public DesiredAttributes(FastFedConfiguration configuration) {
        super(configuration);
        this.preferredSchemaGrammar = configuration.getPreferredSchemaGrammar();
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public DesiredAttributes(DesiredAttributes other) {
        super(other);
        this.preferredSchemaGrammar = other.preferredSchemaGrammar;
        if (other.desiredAttributes != null) {
            for (Map.Entry<SchemaGrammar, ForSchemaGrammar> entry : other.desiredAttributes.entrySet()) {
                desiredAttributes.put(entry.getKey(), new ForSchemaGrammar(entry.getValue()));
            }
        }
    }

    /**
     * Adds the contents of another object into the current object. If the resulting merge causes an attribute
     * to appear in both the required & optional settings, the duplicate is removed from the optional settings.
     * @param other object to merge
     */
    public void merge(DesiredAttributes other) {
        for (SchemaGrammar schemaGrammar : desiredAttributes.keySet()) {
            DesiredAttributes.ForSchemaGrammar thisValue = this.desiredAttributes.get(schemaGrammar);
            DesiredAttributes.ForSchemaGrammar otherValue = other.desiredAttributes.get(schemaGrammar);
            thisValue.requiredUserAttributes.addAll( other.getRequiredUserAttributes());
            thisValue.optionalUserAttributes.addAll( other.getOptionalUserAttributes());
            thisValue.requiredGroupAttributes.addAll( other.getRequiredGroupAttributes());
            thisValue.optionalGroupAttributes.addAll( other.getOptionalGroupAttributes());

            for (String attribute : thisValue.optionalUserAttributes) {
                if (thisValue.requiredUserAttributes.contains(attribute))
                    thisValue.optionalUserAttributes.remove(attribute);
            }
            for (String attribute : thisValue.optionalGroupAttributes) {
                if (thisValue.requiredGroupAttributes.contains(attribute))
                    thisValue.optionalGroupAttributes.remove(attribute);
            }
        }
    }

    /**
     * Adds a User Attribute into the Required User Attributes.
     * @param userAttribute
     */
    public void addRequiredUserAttribute(UserAttribute userAttribute) {
        if (userAttribute == null)
            return;

        for (SchemaGrammar schemaGrammar : userAttribute.getAllSchemaGrammars()) {
            String value = userAttribute.forSchemaGrammar(schemaGrammar);
            if (desiredAttributes.containsKey(schemaGrammar)) {
                desiredAttributes.get(schemaGrammar).requiredUserAttributes.add(value);
            }
        }
    }

    /**
     * Gets all the available schema grammars.
     * @return collection of schema grammars
     */
    public Set<SchemaGrammar> getAllSchemaGrammars() {
        return desiredAttributes.keySet();
    }

    /**
     * Gets the URNs of all the available schema grammars
     * @return collection of schema grammar URNs
     */
    public Set<String> getAllSchemaGrammarUrns() {
        Set<String> returnVal = new HashSet<>();
        for (SchemaGrammar schemaGrammar : getAllSchemaGrammars()) {
            returnVal.add(schemaGrammar.getUrn());
        }
        return returnVal;
    }

    /**
     * Gets the desired attributes as represented in a particular schema grammar.
     * @return desired attributes as represented in the schema grammar
     */
    public ForSchemaGrammar forSchemaGrammar(SchemaGrammar schemaGrammar) {
        return desiredAttributes.get(schemaGrammar);
    }

    /**
     * Adds a new instance of DesiredAttributes.ForSchemaGrammar into the DesiredAttributes.
     * @return desired attributes as represented in the schema grammar
     */
    public void put(ForSchemaGrammar forSchemaGrammar) {
        desiredAttributes.put(forSchemaGrammar.getSchemaGrammar(), forSchemaGrammar);
    }

    /**
     * Removes an instance of DesiredAttributes.ForSchemaGrammar from the DesiredAttributes.
     */
    public void remove(SchemaGrammar schemaGrammar) {
        desiredAttributes.remove(schemaGrammar);
    }

    /**
     * Convenience method to get the required user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return required user attributes
     */
    public Set<String> getRequiredUserAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getRequiredUserAttributes();
    }

    /**
     * Convenience method to get the optional user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional user attributes
     */
    public Set<String> getOptionalUserAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getOptionalUserAttributes();
    }

    /**
     * Convenience method to get the required group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return required group attributes
     */
    public Set<String> getRequiredGroupAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getRequiredGroupAttributes();
    }

    /**
     * Convenience method to get the optional group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional group attributes
     */
    public Set<String> getOptionalGroupAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getOptionalGroupAttributes();
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(JsonMember.DESIRED_ATTRIBUTES);
        for (Map.Entry<SchemaGrammar, ForSchemaGrammar> entry : desiredAttributes.entrySet()) {
            String schemaGrammar = entry.getKey().getUrn();
            ForSchemaGrammar value = entry.getValue();
            JsonObject.Builder builderForSchema = new JsonObject.Builder(schemaGrammar);
            builderForSchema.put(JsonMember.REQUIRED_USER_ATTRIBUTES, value.getRequiredUserAttributes());
            builderForSchema.put(JsonMember.OPTIONAL_USER_ATTRIBUTES, value.getOptionalUserAttributes());
            builderForSchema.put(JsonMember.REQUIRED_GROUP_ATTRIBUTES, value.getRequiredGroupAttributes());
            builderForSchema.put(JsonMember.OPTIONAL_GROUP_ATTRIBUTES, value.getOptionalGroupAttributes());
            builder.putAll(builderForSchema.build());
        }
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JsonMember.DESIRED_ATTRIBUTES);
        super.hydrateFromJson(json);

        if (json.keySet().isEmpty()) {
            return;
        }

        for (String schemaGrammarString : json.keySet()) {
            if (! SchemaGrammar.isValid(schemaGrammarString))
                continue;

            SchemaGrammar schemaGrammar = SchemaGrammar.fromString(schemaGrammarString);
            JsonObject schemaJson = json.getObject(schemaGrammarString);

            DesiredAttributes.ForSchemaGrammar forSchema = new DesiredAttributes.ForSchemaGrammar();
            forSchema.setSchemaGrammar(schemaGrammar);
            forSchema.setRequiredUserAttributes( schemaJson.getStringSet(JsonMember.REQUIRED_USER_ATTRIBUTES));
            forSchema.setOptionalUserAttributes( schemaJson.getStringSet(JsonMember.OPTIONAL_USER_ATTRIBUTES));
            forSchema.setRequiredGroupAttributes( schemaJson.getStringSet(JsonMember.REQUIRED_GROUP_ATTRIBUTES));
            forSchema.setOptionalGroupAttributes( schemaJson.getStringSet(JsonMember.OPTIONAL_GROUP_ATTRIBUTES));
            this.desiredAttributes.put(schemaGrammar, forSchema);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        for (SchemaGrammar schemaGrammar : desiredAttributes.keySet()) {
            String jsonPath = schemaGrammar.toString() + ".";
            DesiredAttributes.ForSchemaGrammar forSchema = forSchemaGrammar(schemaGrammar);
            validateRequiredStringCollection(errorAccumulator, jsonPath + JsonMember.REQUIRED_USER_ATTRIBUTES, forSchema.requiredUserAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.OPTIONAL_USER_ATTRIBUTES, forSchema.optionalUserAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.REQUIRED_GROUP_ATTRIBUTES, forSchema.requiredGroupAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.OPTIONAL_GROUP_ATTRIBUTES, forSchema.optionalGroupAttributes);
        }
    }

    private boolean validateSchemaGrammar(ErrorAccumulator errorAccumulator, String schemaGrammarString) {
        if (SchemaGrammar.isValid(schemaGrammarString)) {
            return true;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Invalid member of \"");
        builder.append(getFullyQualifiedName(JsonMember.DESIRED_ATTRIBUTES));
        builder.append("\". Unrecognized schema grammar: \"");
        builder.append(schemaGrammarString);
        builder.append("\". ");
        List<SchemaGrammar> schemaGrammars = Arrays.asList(SchemaGrammar.values());
        if (schemaGrammars.size() == 1) {
            builder.append("Expected: \"");
            builder.append(schemaGrammars.get(0).toString());
            builder.append("\"");
        } else {
            builder.append("Expected one of: ");
            for (int i = 0; i < schemaGrammars.size(); i++) {
                builder.append("\"");
                builder.append(schemaGrammars.get(i).toString());
                builder.append("\"");
                if (i < (schemaGrammars.size() - 1)) {
                    builder.append(", ");
                }
            }
        }
        errorAccumulator.add(builder.toString());
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DesiredAttributes that = (DesiredAttributes) o;

        return desiredAttributes.equals(that.desiredAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desiredAttributes);
    }
}
