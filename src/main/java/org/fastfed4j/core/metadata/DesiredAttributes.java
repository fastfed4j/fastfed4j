package org.fastfed4j.core.metadata;

import java.util.*;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JSONObject;

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
        private List<String> requiredUserAttributes;
        private List<String> optionalUserAttributes;
        private List<String> requiredGroupAttributes;
        private List<String> optionalGroupAttributes;

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

        public List<String> getRequiredUserAttributes() {
            return requiredUserAttributes;
        }

        public void setRequiredUserAttributes(List<String> requiredUserAttributes) {
            this.requiredUserAttributes = requiredUserAttributes;
        }

        public List<String> getOptionalUserAttributes() {
            return optionalUserAttributes;
        }

        public void setOptionalUserAttributes(List<String> optionalUserAttributes) {
            this.optionalUserAttributes = optionalUserAttributes;
        }

        public List<String> getRequiredGroupAttributes() {
            return requiredGroupAttributes;
        }

        public void setRequiredGroupAttributes(List<String> requiredGroupAttributes) {
            this.requiredGroupAttributes = requiredGroupAttributes;
        }

        public List<String> getOptionalGroupAttributes() {
            return optionalGroupAttributes;
        }

        public void setOptionalGroupAttributes(List<String> optionalGroupAttributes) {
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
        for (Map.Entry<SchemaGrammar, ForSchemaGrammar> entry : other.desiredAttributes.entrySet()) {
            desiredAttributes.put(entry.getKey(), new ForSchemaGrammar(entry.getValue()));
        }
    }

    /**
     * Get the desired attributes as represented in a particular schema grammar.
     * @return desired attributes as represented in the schema grammar
     */
    public ForSchemaGrammar forSchemaGrammar(SchemaGrammar schemaGrammar) {
        return desiredAttributes.get(schemaGrammar);
    }

    /**
     * Convenience method to get the required user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return required user attributes
     */
    public List<String> getRequiredUserAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getRequiredUserAttributes();
    }

    /**
     * Convenience method to get the optional user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional user attributes
     */
    public List<String> getOptionalUserAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getOptionalUserAttributes();
    }

    /**
     * Convenience method to get the required group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return required group attributes
     */
    public List<String> getRequiredGroupAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getRequiredGroupAttributes();
    }

    /**
     * Convenience method to get the optional group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional group attributes
     */
    public List<String> getOptionalGroupAttributes() {
        return forSchemaGrammar(preferredSchemaGrammar).getOptionalGroupAttributes();
    }

    @Override
    public void hydrateFromJson(JSONObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(JSONMember.DESIRED_ATTRIBUTES);
        super.hydrateFromJson(json);

        if (json.keySet().isEmpty()) {
            return;
        }

        for (String schemaGrammarString : json.keySet()) {
            // The following block smuggles a bit of semantic validation into the JSON deserialization.
            // The reason is that a common error mode is anticipated to be that users forget
            // to encapsulate the desired attributes under a particular schema grammar.
            // This would cause the JSON deserialization to generate an opaque type mismatch error.
            // To improve usability, this check produces a helpful, descriptive error message
            // and ceases any further deserialization if the contents aren't properly nested under a
            // recognized schema grammar.
            if (! validateSchemaGrammar(json.getErrorAccumulator(), schemaGrammarString)) {
                break;
            }

            SchemaGrammar schemaGrammar = SchemaGrammar.fromString(schemaGrammarString);
            JSONObject schemaJson = json.getObject(schemaGrammarString);

            DesiredAttributes.ForSchemaGrammar forSchema = new DesiredAttributes.ForSchemaGrammar();
            forSchema.setSchemaGrammar(schemaGrammar);
            forSchema.setRequiredUserAttributes( schemaJson.getStringList(JSONMember.REQUIRED_USER_ATTRIBUTES));
            forSchema.setOptionalUserAttributes( schemaJson.getStringList(JSONMember.OPTIONAL_USER_ATTRIBUTES));
            forSchema.setRequiredGroupAttributes( schemaJson.getStringList(JSONMember.REQUIRED_GROUP_ATTRIBUTES));
            forSchema.setOptionalGroupAttributes( schemaJson.getStringList(JSONMember.OPTIONAL_GROUP_ATTRIBUTES));
            this.desiredAttributes.put(schemaGrammar, forSchema);
        }


    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        if (desiredAttributes.size() == 0) {
            errorAccumulator.add("Missing value for \"" + JSONMember.DESIRED_ATTRIBUTES + "\"");
        }

        for (SchemaGrammar schemaGrammar : desiredAttributes.keySet()) {
            DesiredAttributes.ForSchemaGrammar forSchema = forSchemaGrammar(schemaGrammar);
            validateRequiredStringCollection(errorAccumulator, JSONMember.REQUIRED_USER_ATTRIBUTES, forSchema.requiredUserAttributes);
            validateOptionalStringCollection(errorAccumulator, JSONMember.OPTIONAL_USER_ATTRIBUTES, forSchema.optionalUserAttributes);
            validateOptionalStringCollection(errorAccumulator, JSONMember.REQUIRED_GROUP_ATTRIBUTES, forSchema.requiredGroupAttributes);
            validateOptionalStringCollection(errorAccumulator, JSONMember.OPTIONAL_GROUP_ATTRIBUTES, forSchema.optionalGroupAttributes);
        }
    }

    private boolean validateSchemaGrammar(ErrorAccumulator errorAccumulator, String schemaGrammarString) {
        if (SchemaGrammar.isValid(schemaGrammarString)) {
            return true;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Invalid member of \"");
        builder.append(JSONMember.DESIRED_ATTRIBUTES);
        builder.append("\". Unrecognized schema grammar (receivedValue=\"");
        builder.append(schemaGrammarString);
        builder.append("\"). \"");
        List<SchemaGrammar> schemaGrammars = Arrays.asList(SchemaGrammar.values());
        if (schemaGrammars.size() == 1) {
            builder.append("Expected \"");
            builder.append(schemaGrammars.get(0).toString());
            builder.append("\"");
        } else {
            builder.append("\". Expected one of ");
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
