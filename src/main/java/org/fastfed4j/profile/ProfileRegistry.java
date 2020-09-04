package org.fastfed4j.profile;

import java.util.*;

/**
 Lookup table for Profiles, keyed by the URN of the Profile.
 */
public class ProfileRegistry {
    private final Map<String, Profile> registry = new HashMap<>();

    /**
     * Constructs an empty instance.
     */
    public ProfileRegistry() {}

    /**
     * Constructs an instance and populates with the list of profiles.
     */
    public ProfileRegistry(List<Profile> profiles) {
        add(profiles);
    }

    /**
     * Adds a list of profiles to the registry.
     * @param profiles new profiles to add
     */
    public void add(List<Profile> profiles) {
        for (Profile p : profiles) {
            add(p);
        }
    }

    /**
     * Adds a single profile to the registry.
     * @param profile new profile to add
     */
    public void add(Profile profile) {
        registry.put(profile.getUrn(), profile);
    }

    /**
     * Tests if the registry contains an entry for a given profile URN.
     * @param urn URN
     * @return true if the URN exists in the registry
     */
    public boolean containsUrn(String urn) {
        return registry.containsKey(urn);
    }

    /**
     * Gets a profile from the registry.
     * @param urn URN of the profile
     * @return profile, or null if the URN doesn't exist in the registry
     */
    public Profile getByUrn(String urn) {
        return registry.get(urn);
    }

    /**
     * Gets all the profiles from the registry.
     * @return collection of all profiles
     */
    public List<Profile> getAllProfiles() {
        return new ArrayList<>(registry.values());
    }

    /**
     * Gets all the profile URNs from the registry.
     * @return collection of all profile URNs
     */
    public Set<String> getAllUrns() {
        return registry.keySet();
    }

}
