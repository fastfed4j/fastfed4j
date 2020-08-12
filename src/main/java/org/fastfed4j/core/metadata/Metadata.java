package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.JSONMember;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JSONObject;
import org.fastfed4j.core.json.JSONParser;
import org.fastfed4j.core.util.ValidationUtils;

import java.util.*;

/**
 * Base class for all FastFed Metadata objects. Provides common methods for JSON serialization and validation,
 * plus extension points for capturing the extended attributes required by some FastFed Profiles.
 */
abstract public class Metadata {
    private static final ValidationUtils validationUtils = new ValidationUtils();

    private final FastFedConfiguration configuration;
    private final Map<String, Metadata> metadataExtensions = new HashMap<>();
    private String jsonPath = ""; //If hydrated from JSON, this is the fully qualified JSON path for the object.

    /**
     * Constructs an empty object with only the FastFedConfiguration
     * @param configuration FastFed Configuration that controls the behavior of the SDK
     */
    public Metadata(FastFedConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        this.configuration = configuration;
    }

    /**
     * Copy Constructor
     * @param other object to copy
     */
    public Metadata(Metadata other) {
        Objects.requireNonNull(other, "copy constructor input must not be null");
        this.configuration = other.configuration; //FastFedConfiguration is immutable. OK to share a reference.
        this.jsonPath = other.jsonPath;

        for (Map.Entry<String, Metadata> entry : other.metadataExtensions.entrySet()) {
            String profileUrn = entry.getKey();
            Metadata originalObj = entry.getValue();
            Metadata copyObj;
            try {
                //Invoke the copy constructor via reflection
                copyObj = originalObj.getClass().getConstructor(originalObj.getClass()).newInstance(originalObj);
                this.metadataExtensions.put(profileUrn, copyObj);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the FastFedConfiguration set on the object
     */
    public FastFedConfiguration getFastFedConfiguration() {
        return configuration;
    }

    /**
     * Capture the extended metadata defined by a profile, keyed by the profile URN.
     * @param profileUrn the urn of the profile
     * @param ext the extended metadata defined by the profile
     */
    public void addMetadataExtension(String profileUrn, Metadata ext) {
        metadataExtensions.put(profileUrn,ext);
    }

    /**
     * Get all the metadata extensions, keyed by profile URN
     * @return collection of extensions, keyed by profile URN
     */
    public Map<String, Metadata> getAllMetadataExtensions() {
        return metadataExtensions;
    }

    /**
     * Test if a metadata extension exists for a particular profile URN
     * @param profileUrn the urn of the profile
     * @return true if a value exists for the profile
     */
    public boolean hasMetadataExtension(String profileUrn) {
        return metadataExtensions.containsKey(profileUrn);
    }

    /**
     * Get the metadata extension exists for a particular profile URN
     * @param profileUrn the urn of the profile
     * @return metadata, or null if no metadata is defined
     */
    public Metadata getMetadataExtension(String profileUrn) {
        return metadataExtensions.get(profileUrn);
    }

    /**
     * Convenience method to get the metadata extension for a particular profile URN,
     * cast into the correct type.
     * @param type the metadata extension class
     * @param profileUrn the urn of the profile
     * @return metadata, or null if no metadata is defined
     */
    //Convenience method to get the extension with the proper class
    public <T> T getMetadataExtension(Class<T> type,
                                      String profileUrn) {
        try {
            return type.cast(metadataExtensions.get(profileUrn));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hydrates the object from a JSON-serialized representation and then validates the contents to ensure it complies
     * with the FastFed specification.
     * @param jsonString JSON representation of the metadata
     * @throws InvalidMetadataException if the JSON is malformed or non-compliant with the FastFed specification
     */
    public void hydrateAndValidate(String jsonString)
            throws InvalidMetadataException
    {
        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        JSONObject json = JSONParser.parse(jsonString, errorAccumulator);
        hydrateFromJson(json);

        // Check for syntactic validation errors; i.e. invalid JSON
        if (errorAccumulator.hasErrors()) {
            throw new InvalidMetadataException(errorAccumulator);
        }

        // Check for semantic validation errors; i.e. non-compliance to the spec
        validate(errorAccumulator);
        if (errorAccumulator.hasErrors()) {
            throw new InvalidMetadataException(errorAccumulator);
        }
    }

    /**
     * Populates the contents of the object from a JSON representation.
     */
    public void hydrateFromJson(JSONObject json) {
        this.jsonPath = json.getJsonPath();
    }

    /**
     * Validates that the contents of the object conform to the FastFed specification.
     * @param errorAccumulator to capture the full list of validation errors
     */
    abstract public void validate(ErrorAccumulator errorAccumulator);

    protected boolean validateRequiredString(ErrorAccumulator errorAccumulator, JSONMember memberName, String value) {
        return validationUtils.validateRequiredString(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateOptionalStringCollection(ErrorAccumulator errorAccumulator, JSONMember memberName, Collection<String> value) {
        return validationUtils.validateOptionalStringCollection(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredStringCollection(ErrorAccumulator errorAccumulator, JSONMember memberName, Collection<String> value) {
        return validationUtils.validateOptionalStringCollection(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredDate(ErrorAccumulator errorAccumulator, JSONMember memberName, Date value) {
        return validationUtils.validateRequiredDate(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredObject(ErrorAccumulator errorAccumulator, JSONMember memberName, Object o) {
        return validationUtils.validateRequiredObject(errorAccumulator, getFullyQualifiedName(memberName), o);
    }

    protected boolean validateRequiredObject(ErrorAccumulator errorAccumulator, String memberName, Object o) {
        return validationUtils.validateRequiredObject(errorAccumulator, getFullyQualifiedName(memberName), o);
    }

    protected boolean validateRequiredUrl(ErrorAccumulator errorAccumulator, JSONMember memberName, String value) {
        return validationUtils.validateRequiredUrl(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateOptionalUrl(ErrorAccumulator errorAccumulator, JSONMember memberName, String value) {
        return validationUtils.validateOptionalUrl(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    public String getFullyQualifiedName(JSONMember memberName) {
        return getFullyQualifiedName(memberName.toString());
    }

    public String getFullyQualifiedName(String memberName) {
        if (jsonPath.isEmpty()) {
            return memberName;
        } else {
            return String.join(JSONObject.PATH_DELIMITER, jsonPath, memberName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return metadataExtensions.equals(metadata.metadataExtensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadataExtensions);
    }
}
