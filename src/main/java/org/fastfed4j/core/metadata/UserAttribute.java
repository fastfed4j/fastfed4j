package org.fastfed4j.core.metadata;

import java.util.*;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.util.ValidationUtils;

/**
 * Represents the User Attribute metadata, as defined in section 3.3.6 of the FastFed Core specification.
 */
public class UserAttribute extends Metadata {

    private static final ValidationUtils validationUtils = new ValidationUtils();

    private final String jsonObjectName;
    private final Map<SchemaGrammar, String> userAttribute = new HashMap<>();
    private final SchemaGrammar preferredSchemaGrammar;

    /**
     * Constructs an empty instance
     */
    public UserAttribute(FastFedConfiguration configuration, String jsonObjectName) {
        super(configuration);
        Objects.requireNonNull(jsonObjectName, "jsonObjectName must not be null");
        this.preferredSchemaGrammar = configuration.getPreferredSchemaGrammar();
        this.jsonObjectName = jsonObjectName;
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public UserAttribute(UserAttribute other) {
        super(other);
        this.preferredSchemaGrammar = other.preferredSchemaGrammar;
        this.jsonObjectName = other.jsonObjectName;
        if (other.userAttribute != null) {
            for (Map.Entry<SchemaGrammar, String> entry : other.userAttribute.entrySet()) {
                userAttribute.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Gets all the available schema grammars.
     * @return collection of schema grammars
     */
    public Set<SchemaGrammar> getAllSchemaGrammars() {
        return userAttribute.keySet();
    }

    /**
     * Gets the user attribute as represented in a particular schema grammar.
     * @return user attribute as represented in the schema grammar
     */
    public String forSchemaGrammar(SchemaGrammar schemaGrammar) {
        return userAttribute.get(schemaGrammar);
    }

    /**
     * Convenience method to get the user attribute expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @return user attribute as represented in the preferred schema grammar
     */
    public String get() {
        return userAttribute.get(preferredSchemaGrammar);
    }

    /**
     * Convenience method to set the user attribute expressed in the
     * preferred schema grammar, as specified in the FastFedConfiguration.
     * @param value of the attribute
     */
    public void set(String value) {
        userAttribute.put(preferredSchemaGrammar, value);
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder(jsonObjectName);
        for (Map.Entry<SchemaGrammar, String> entry : userAttribute.entrySet()) {
            String schemaGrammar = entry.getKey().getUrn();
            String attributeName = entry.getValue();
            builder.put(schemaGrammar, attributeName);
        }
        return builder.build();
    }

    @Override
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        json = json.unwrapObjectIfNeeded(jsonObjectName);
        super.hydrateFromJson(json);

        if (json.keySet().isEmpty()) {
            return;
        }

        for (String schemaGrammarString : json.keySet()) {
            if (! SchemaGrammar.isValid(schemaGrammarString))
                continue;

            SchemaGrammar schemaGrammar = SchemaGrammar.fromString(schemaGrammarString);
            String attributeName = json.getString(schemaGrammarString);
            this.userAttribute.put(schemaGrammar, attributeName);
        }
    }

    @Override
    public void validate(ErrorAccumulator errorAccumulator) {
        if (userAttribute.size() == 0) {
            errorAccumulator.add("Missing value for \"" + jsonObjectName + "\"");
            return;
        }

        for (SchemaGrammar schemaGrammar : userAttribute.keySet()) {
            String memberName = schemaGrammar.toString();
            String value = userAttribute.get(schemaGrammar);
            validateRequiredString(errorAccumulator, memberName, value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserAttribute that = (UserAttribute) o;
        return Objects.equals(userAttribute, that.userAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userAttribute);
    }
}
