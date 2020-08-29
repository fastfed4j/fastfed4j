package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.json.JsonParser;
import org.fastfed4j.core.util.ReflectionUtils;
import org.fastfed4j.core.util.ValidationUtils;
import org.fastfed4j.profile.Profile;
import org.fastfed4j.profile.ProfileRegistry;

import java.util.*;

/**
 * Base class for all FastFed Metadata objects. Provides common methods for JSON serialization and validation,
 * plus extension points for capturing the extended attributes required by some FastFed Profiles.
 */
abstract public class Metadata {
    private static final ValidationUtils validationUtils = new ValidationUtils();

    private final FastFedConfiguration configuration;
    private Map<String, Metadata> metadataExtensions = new HashMap<>();
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
        this.metadataExtensions = cloneExtensions(other.metadataExtensions);
    }

    /**
     * Gets the FastFedConfiguration set on the object.
     */
    public FastFedConfiguration getFastFedConfiguration() {
        return configuration;
    }

    /**
     * Captures the extended metadata defined by a profile, keyed by the profile URN.
     * @param profileUrn the urn of the profile
     * @param ext the extended metadata defined by the profile
     */
    public void addMetadataExtension(String profileUrn, Metadata ext) {
        metadataExtensions.put(profileUrn,ext);
    }

    /**
     * Gets all the metadata extensions, keyed by profile URN.
     * @return collection of extensions, keyed by profile URN
     */
    public Map<String, Metadata> getAllMetadataExtensions() {
        return metadataExtensions;
    }

    /**
     * Tests if any metadata extensions have been set.
     * @return true if values exist
     */
    public boolean hasMetadataExtensions() {
        return !metadataExtensions.isEmpty();
    }

    /**
     * Tests if a metadata extension exists for a particular profile URN.
     * @param profileUrn the urn of the profile
     * @return true if a value exists for the profile
     */
    public boolean hasMetadataExtension(String profileUrn) {
        return metadataExtensions.containsKey(profileUrn);
    }

    /**
     * Gets the metadata extension exists for a particular profile URN.
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
    public <T> T getMetadataExtension(Class<T> type, String profileUrn) {
        try {
            return type.cast(metadataExtensions.get(profileUrn));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clones a deep copy of Metadata extensions
     * @param other source
     * @return clone
     */
    public Map<String,Metadata> cloneExtensions(Map<String,Metadata> other) {
        Map<String,Metadata> clone = new HashMap<>();
        for (Map.Entry<String, Metadata> entry : other.entrySet()) {
            String profileUrn = entry.getKey();
            Metadata originalEntry = entry.getValue();
            Metadata clonedEntry = (Metadata) ReflectionUtils.copy(originalEntry);
            clone.put(profileUrn, clonedEntry);
        }
        return clone;
    }

    /**
     * Serializes into JSON.
     * @return JsonObject
     */
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        if (hasMetadataExtensions()) {
            for (Metadata obj : getAllMetadataExtensions().values()) {
                builder.putAll(obj.toJson());
            }
        }
        return builder.build();
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
        Objects.requireNonNull(jsonString, "json must not be null");

        ErrorAccumulator errorAccumulator = new ErrorAccumulator();
        JsonObject json = JsonParser.parse(jsonString, errorAccumulator);
        hydrateFromJson(json);

        // Check for syntactic validation errors; i.e. invalid JSON
        if (errorAccumulator.hasErrors()) {
            throw new InvalidMetadataException(errorAccumulator, jsonString);
        }

        // Check for semantic validation errors; i.e. non-compliance to the spec
        validate(errorAccumulator);
        if (errorAccumulator.hasErrors()) {
            throw new InvalidMetadataException(errorAccumulator, jsonString);
        }
    }

    /**
     * Populates the contents of the object from a JSON representation.
     */
    public void hydrateFromJson(JsonObject json) {
        if (json == null) return;
        this.jsonPath = json.getJsonPath();
    }

    /**
     * Populates the contents of the object from a JSON representation,
     * including any Metadata extensions.
     * @param json JSON source used for hydration
     * @param extensionType If specified, extensions of the given type will be hydrated.
     */
    public void hydrateExtensions(JsonObject json, Profile.ExtensionType extensionType) {
        hydrateExtensions(json, metadataExtensions, extensionType);
    }

    /**
     * Populates the contents of the object from a JSON representation,
     * including any Metadata extensions. This variation allows the hydrated objects
     * to be added to a specific location rather than the default metadata table, which
     * can be necessary for implementations that use multiple extension points.
     * @param json JSON source used for hydration
     * @param extensionTable the location where the hydrated metadata should be added
     * @param extensionType If specified, extensions of the given type will be hydrated.
     */
    public void hydrateExtensions(JsonObject json,
                                  Map<String,Metadata> extensionTable,
                                  Profile.ExtensionType extensionType)
    {
        if (json == null) return;

        // Iterate through all the known profiles
        ProfileRegistry registry = getFastFedConfiguration().getProfileRegistry();
        for (String urn : registry.getAllUrns()) {

            // Confirm JSON exists for the profile
            JsonObject extendedJson = json.getObject(urn);
            if (extendedJson == null)
                continue;

            // Fetch the implementation of the profile
            Profile profile = registry.getByUrn(urn);
            Optional<Metadata> impl;
            switch (extensionType) {
                case ApplicationProviderMetadata:
                    impl = profile.newApplicationProviderMetadataExtension(getFastFedConfiguration());
                    break;
                case IdentityProviderMetadata:
                    impl = profile.newIdentityProviderMetadataExtension(getFastFedConfiguration());
                    break;
                case RegistrationRequest:
                    impl = profile.newRegistrationRequestExtension(getFastFedConfiguration());
                    break;
                case RegistrationResponse:
                    impl = profile.newRegistrationResponseExtension(getFastFedConfiguration());
                    break;
                default:
                    throw new RuntimeException("Unrecognized extension type: " + extensionType);
            }

            if (impl.isEmpty()) {
                continue;
            }

            // Hydrate the profile and capture in the metadata table
            impl.get().hydrateFromJson(extendedJson);
            extensionTable.put(urn, impl.get());
        }
    }

    /**
     * Validates that the contents of the object conform to the FastFed specification.
     * @param errorAccumulator to capture the full list of validation errors
     */
    abstract public void validate(ErrorAccumulator errorAccumulator);

    /**
     * Validates that the contents of the object conform to the FastFed specification.
     * @param errorAccumulator to capture the full list of validation errors
     * @param profileUrns the urns of the extended profiles that should be validated
     * @param extensionType extension type, used to determine the validation rules
     */
    public void validateExtensions(ErrorAccumulator errorAccumulator,
                                   Set<String> profileUrns,
                                   Profile.ExtensionType extensionType)

    {
        validateExtensions(errorAccumulator, metadataExtensions, profileUrns, extensionType);
    }

    /**
     * Validates that the contents of the object conform to the FastFed specification.
     * @param errorAccumulator to capture the full list of validation errors
     * @param extensionTable table of all the extensions that have been set, keyed by profile URN
     * @param profileUrns the urns of the extended profiles that should be validated
     * @param extensionType extension type, used to determine the validation rules
     */
    public void validateExtensions(ErrorAccumulator errorAccumulator,
                                   Map<String, Metadata> extensionTable,
                                   Set<String> profileUrns,
                                   Profile.ExtensionType extensionType)

    {
        for (String profileUrn : profileUrns) {
            Profile profile = getFastFedConfiguration().getProfileRegistry().getByUrn(profileUrn);
            if (profile == null) {
                throw new RuntimeException("No profile is registered for " + profileUrn);
            }

            boolean isRequired = false;
            switch (extensionType) {
                case ApplicationProviderMetadata:
                    isRequired = profile.requiresApplicationProviderMetadataExtension();
                    break;
                case IdentityProviderMetadata:
                    isRequired = profile.requiresIdentityProviderMetadataExtension();
                    break;
                case RegistrationRequest:
                    isRequired = profile.requiresRegistrationRequestExtension();
                    break;
                case RegistrationResponse:
                    isRequired = profile.requiresRegistrationResponseExtension();
                    break;
                default:
                    throw new RuntimeException("Unrecognized extension type: " + extensionType);
            }

            // If a party includes a certain profile in their list of capabilities, and the profile
            // mandates the existence of extended attributes, ensure the extension is defined.
            if (isRequired && !extensionTable.containsKey(profileUrn)) {
                errorAccumulator.add("Missing extended values for profile \"" + profileUrn + "\"");
                return;
            }

            // If an extension is defined, ensure it is valid.
            if (extensionTable.containsKey(profileUrn)) {
                extensionTable.get(profileUrn).validate(errorAccumulator);
            }
        }
    }

    protected boolean validateRequiredString(ErrorAccumulator errorAccumulator, String memberName, String value) {
        return validationUtils.validateRequiredString(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateOptionalStringCollection(ErrorAccumulator errorAccumulator, String memberName, Collection<String> value) {
        return validationUtils.validateOptionalStringCollection(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredStringCollection(ErrorAccumulator errorAccumulator, String memberName, Collection<String> value) {
        return validationUtils.validateRequiredStringCollection(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredDate(ErrorAccumulator errorAccumulator, String memberName, Date value) {
        return validationUtils.validateRequiredDate(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateRequiredObject(ErrorAccumulator errorAccumulator, String memberName, Object o) {
        return validationUtils.validateRequiredObject(errorAccumulator, getFullyQualifiedName(memberName), o);
    }

    protected boolean validateRequiredUrl(ErrorAccumulator errorAccumulator, String memberName, String value) {
        return validationUtils.validateRequiredUrl(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    protected boolean validateOptionalUrl(ErrorAccumulator errorAccumulator, String memberName, String value) {
        return validationUtils.validateOptionalUrl(errorAccumulator, getFullyQualifiedName(memberName), value);
    }

    public String getFullyQualifiedName(String memberName) {
        if (jsonPath.isEmpty()) {
            return memberName;
        } else {
            return String.join(JsonObject.JSON_PATH_DELIMITER, jsonPath, memberName);
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

    @Override
    public String toString() {
        return toJson().toString();
    }
}
