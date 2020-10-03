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

    private static final String SCIM_CORE_USER_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User:";
    private static final String SCIM_CORE_GROUP_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User:";

    private final Map<SchemaGrammar, ForSchemaGrammar> desiredAttributes= new HashMap<>();
    private final SchemaGrammar preferredSchemaGrammar;

    /**
     * The Desired Attributes metadata is an array containing one or more representations of the
     * desired attributes in a particular schema grammar (SCIM 2.0 being the recommended grammar).
     * This class represents one element of that array, containing the desired attributes for the
     * specific grammar.
     */
    public static class ForSchemaGrammar {
        private SchemaGrammar schemaGrammar;
        private Set<String> requiredUserAttributes = new HashSet<>();
        private Set<String> optionalUserAttributes = new HashSet<>();
        private Set<String> requiredGroupAttributes = new HashSet<>();
        private Set<String> optionalGroupAttributes = new HashSet<>();

        public ForSchemaGrammar(SchemaGrammar schemaGrammar) {
            this.schemaGrammar = schemaGrammar;
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
            Objects.requireNonNull(schemaGrammar, "schemaGrammar must not be null");
            this.schemaGrammar = schemaGrammar;
        }

        public Set<String> getRequiredUserAttributes() {
            return requiredUserAttributes;
        }

        public void setRequiredUserAttributes(Set<String> requiredUserAttributes) {
            Objects.requireNonNull(requiredUserAttributes, "requiredUserAttributes must not be null");
            this.requiredUserAttributes = requiredUserAttributes;
        }

        public Set<String> getOptionalUserAttributes() {
            return optionalUserAttributes;
        }

        public void setOptionalUserAttributes(Set<String> optionalUserAttributes) {
            Objects.requireNonNull(optionalUserAttributes, "optionalUserAttributes must not be null");
            this.optionalUserAttributes = optionalUserAttributes;
        }

        public Set<String> getRequiredGroupAttributes() {
            return requiredGroupAttributes;
        }

        public void setRequiredGroupAttributes(Set<String> requiredGroupAttributes) {
            Objects.requireNonNull(requiredGroupAttributes, "requiredGroupAttributes must not be null");
            this.requiredGroupAttributes = requiredGroupAttributes;
        }

        public Set<String> getOptionalGroupAttributes() {
            return optionalGroupAttributes;
        }

        public void setOptionalGroupAttributes(Set<String> optionalGroupAttributes) {
            Objects.requireNonNull(optionalGroupAttributes, "optionalGroupAttributes must not be null");
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
    public void addAll(DesiredAttributes other) {
        for (SchemaGrammar schemaGrammar : other.desiredAttributes.keySet()) {
            if (! this.desiredAttributes.containsKey(schemaGrammar)) {
                this.desiredAttributes.put(schemaGrammar, new ForSchemaGrammar(schemaGrammar));
            }
            DesiredAttributes.ForSchemaGrammar thisValue = this.desiredAttributes.get(schemaGrammar);
            DesiredAttributes.ForSchemaGrammar otherValue = other.desiredAttributes.get(schemaGrammar);
            thisValue.requiredUserAttributes.addAll( otherValue.getRequiredUserAttributes());
            thisValue.optionalUserAttributes.addAll( otherValue.getOptionalUserAttributes());
            thisValue.requiredGroupAttributes.addAll( otherValue.getRequiredGroupAttributes());
            thisValue.optionalGroupAttributes.addAll( otherValue.getOptionalGroupAttributes());

            removeDuplicateAttributesFromSet(thisValue.requiredUserAttributes, thisValue.optionalUserAttributes);
            removeDuplicateAttributesFromSet(thisValue.requiredGroupAttributes, thisValue.optionalGroupAttributes);
        }
    }

    /**
     * Adds a User Attribute into the Required User Attributes.
     * @param userAttribute the user attribute to add to the set
     */
    public void addRequiredUserAttribute(UserAttribute userAttribute) {
        if (userAttribute == null)
            return;

        for (SchemaGrammar schemaGrammar : userAttribute.getAllSchemaGrammars()) {
            if (! this.desiredAttributes.containsKey(schemaGrammar)) {
                this.desiredAttributes.put(schemaGrammar, new ForSchemaGrammar(schemaGrammar));
            }
            Set<String> requiredUserAttributes = desiredAttributes.get(schemaGrammar).getRequiredUserAttributes();
            Set<String> optionalUserAttributes = desiredAttributes.get(schemaGrammar).getOptionalUserAttributes();
            String newValue = userAttribute.forSchemaGrammar(schemaGrammar);
            requiredUserAttributes.add(newValue);
            removeDuplicateAttributesFromSet(requiredUserAttributes, optionalUserAttributes);
        }
    }

    /**
     * If an attribute exists in both the required & optional collection, the duplicate is
     * removed from the optional category. The contents of the sets are mutated by this method.
     * @param requiredAttributes required attributes
     * @param optionalAttributes optional attributes
     */
    public void removeDuplicateAttributesFromSet(Set<String> requiredAttributes, Set<String> optionalAttributes) {
        Set<String> itemsToRemove = new HashSet<>();
        for (String item : optionalAttributes) {
            if (requiredAttributes.contains(item))
                itemsToRemove.add(item);
        }
        optionalAttributes.removeAll(itemsToRemove);
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
    public ForSchemaGrammar getForSchemaGrammar(SchemaGrammar schemaGrammar) {
        return desiredAttributes.get(schemaGrammar);
    }

    /**
     * Adds a new instance of DesiredAttributes.ForSchemaGrammar into the DesiredAttributes.
     */
    public void setForSchemaGrammar(ForSchemaGrammar forSchemaGrammar) {
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
        return getForSchemaGrammar(preferredSchemaGrammar).getRequiredUserAttributes();
    }

    /**
     * Convenience method to get the optional user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional user attributes
     */
    public Set<String> getOptionalUserAttributes() {
        return getForSchemaGrammar(preferredSchemaGrammar).getOptionalUserAttributes();
    }

    /**
     * Convenience method to get the required group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return required group attributes
     */
    public Set<String> getRequiredGroupAttributes() {
        return getForSchemaGrammar(preferredSchemaGrammar).getRequiredGroupAttributes();
    }

    /**
     * Convenience method to get the optional group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return optional group attributes
     */
    public Set<String> getOptionalGroupAttributes() {
        return getForSchemaGrammar(preferredSchemaGrammar).getOptionalGroupAttributes();
    }

    /**
     * Convenience method to set the required user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @param requiredUserAttributes set of required user attributes. Null values are prohibited. The absence of attributes is represented by using an empty Set.
     */
    public void setRequiredUserAttributes(Set<String> requiredUserAttributes) {
        Objects.requireNonNull(requiredUserAttributes, "requiredUserAttributes must not be null");
        getForSchemaGrammar(preferredSchemaGrammar).setRequiredUserAttributes(requiredUserAttributes);
    }

    /**
     * Convenience method to set the optional user attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @param optionalUserAttributes set of optional user attributes. Null values are prohibited. The absence of attributes is represented by using an empty Set.
     */
    public void setOptionalUserAttributes(Set<String> optionalUserAttributes) {
        Objects.requireNonNull(optionalUserAttributes, "optionalUserAttributes must not be null");
        getForSchemaGrammar(preferredSchemaGrammar).setOptionalUserAttributes(optionalUserAttributes);
    }

    /**
     * Convenience method to set the required group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @param requiredGroupAttributes set of required group attributes. Null values are prohibited. The absence of attributes is represented by using an empty Set.
     */
    public void setRequiredGroupAttributes(Set<String> requiredGroupAttributes) {
        Objects.requireNonNull(requiredGroupAttributes, "requiredGroupAttributes must not be null");
        getForSchemaGrammar(preferredSchemaGrammar).setRequiredGroupAttributes(requiredGroupAttributes);
    }

    /**
     * Convenience method to set the optional group attributes expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @param optionalGroupAttributes set of optional group attributes. Null values are prohibited. The absence of attributes is represented by using an empty Set.
     */
    public void setOptionalGroupAttributes(Set<String> optionalGroupAttributes) {
        Objects.requireNonNull(optionalGroupAttributes, "optionalGroupAttributes must not be null");
        getForSchemaGrammar(preferredSchemaGrammar).setOptionalGroupAttributes(optionalGroupAttributes);
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

            DesiredAttributes.ForSchemaGrammar forSchema = new DesiredAttributes.ForSchemaGrammar(schemaGrammar);
            forSchema.setRequiredUserAttributes( normalize(schemaJson.getStringSet(JsonMember.REQUIRED_USER_ATTRIBUTES)));
            forSchema.setOptionalUserAttributes( normalize(schemaJson.getStringSet(JsonMember.OPTIONAL_USER_ATTRIBUTES)));
            forSchema.setRequiredGroupAttributes( normalize(schemaJson.getStringSet(JsonMember.REQUIRED_GROUP_ATTRIBUTES)));
            forSchema.setOptionalGroupAttributes( normalize(schemaJson.getStringSet(JsonMember.OPTIONAL_GROUP_ATTRIBUTES)));

            removeDuplicateAttributesFromSet(forSchema.getRequiredUserAttributes(), forSchema.getOptionalUserAttributes());
            removeDuplicateAttributesFromSet(forSchema.getRequiredGroupAttributes(), forSchema.getOptionalGroupAttributes());

            this.desiredAttributes.put(schemaGrammar, forSchema);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        for (SchemaGrammar schemaGrammar : desiredAttributes.keySet()) {
            String jsonPath = schemaGrammar.toString() + ".";
            DesiredAttributes.ForSchemaGrammar forSchema = getForSchemaGrammar(schemaGrammar);
            validateRequiredStringCollection(errorAccumulator, jsonPath + JsonMember.REQUIRED_USER_ATTRIBUTES, forSchema.requiredUserAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.OPTIONAL_USER_ATTRIBUTES, forSchema.optionalUserAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.REQUIRED_GROUP_ATTRIBUTES, forSchema.requiredGroupAttributes);
            validateOptionalStringCollection(errorAccumulator, jsonPath + JsonMember.OPTIONAL_GROUP_ATTRIBUTES, forSchema.optionalGroupAttributes);
        }
    }

    /**
     * Normalizes the contents of a Set of attributes.
     * Specifically, converts null values to an empty list (the FastFed spec requires they be treated
     * equivalently) and removes the optional SCIM fully qualified name prefix if included for the the core schema.
     */
    private Set<String> normalize(Set<String> input) {
        Set<String> output = new HashSet<>();
        if (input == null)
            return output;

        for (String element : input) {
            if (element == null) continue;
            element = element.trim();
            element = removePrefix(element, SCIM_CORE_USER_SCHEMA);
            element = removePrefix(element, SCIM_CORE_GROUP_SCHEMA);
            output.add(element);
        }
        return output;
    }

    private String removePrefix(String input, String prefix) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(prefix, "prefix must not be null");
        if (input.startsWith(prefix)) {
            return input.substring(prefix.length());
        }
        else {
            return input;
        }
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
