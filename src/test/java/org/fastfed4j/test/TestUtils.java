package org.fastfed4j.test;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.AuthenticationProfile;
import org.fastfed4j.core.constants.ProvisioningProfile;
import org.fastfed4j.core.contract.EnabledProfiles;

import java.util.HashSet;
import java.util.Set;

public class TestUtils {

    /**
     * Utility to get an instance of EnabledProfiles with all known profiles.
     * Required by some objects to determine the scope of validation.
     * @return all known profiles
     */
    public static EnabledProfiles getAllEnabledProfiles() {
        Set<String> authenticationProfiles = new HashSet<>();
        for (AuthenticationProfile i : AuthenticationProfile.values()) {
            authenticationProfiles.add(i.getUrn());
        }

        Set<String> provisioningProfiles = new HashSet<>();
        for (ProvisioningProfile i : ProvisioningProfile.values()) {
            provisioningProfiles.add(i.getUrn());
        }

        EnabledProfiles enabledProfiles = new EnabledProfiles(FastFedConfiguration.DEFAULT);
        enabledProfiles.setAuthenticationProfiles((authenticationProfiles));
        enabledProfiles.setProvisioningProfiles(provisioningProfiles);
        return enabledProfiles;
    }
}
