package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of FastFed Provisioning Profile URNs
 */
public enum ProvisioningProfile {

    ENTERPRISE_SCIM("urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise");

    private static Map<String, ProvisioningProfile> reverseLookup = new ConcurrentHashMap<>();
    private String urn;

    ProvisioningProfile(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    @Override
    public String toString() {
        return getUrn();
    }

    public static ProvisioningProfile fromString(String urn) {
        initializeReverseLookupIfNeeded();
        if (urn != null && reverseLookup.containsKey(urn)){
            return reverseLookup.get(urn);
        } else {
            throw new RuntimeException("Unrecognized ProvisioningProfile: \"" + urn + "\"");
        }
    }

    public static boolean isValid(String urnString) {
        initializeReverseLookupIfNeeded();
        return (reverseLookup.containsKey(urnString));
    }

    private static void initializeReverseLookupIfNeeded() {
        if (reverseLookup.isEmpty()) {
            for (ProvisioningProfile v : values()) {
                reverseLookup.put(v.urn, v);
            }
        }
    }
}
