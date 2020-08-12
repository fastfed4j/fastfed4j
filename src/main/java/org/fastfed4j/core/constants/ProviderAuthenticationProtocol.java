package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of FastFed Provider Authentication URNs
 */
public enum ProviderAuthenticationProtocol {

    OAUTH2_JWT("urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile");

    private static Map<String, ProviderAuthenticationProtocol> reverseLookup = new ConcurrentHashMap<>();
    private String urn;

    ProviderAuthenticationProtocol(String urn) {
        this.urn = urn;
    }

    public String getUrn() { return urn; }
    
    @Override
    public String toString() {
        return getUrn();
    }

    public static ProviderAuthenticationProtocol fromString(String urn) {
        initializeReverseLookupIfNeeded();
        if (urn != null && reverseLookup.containsKey(urn)){
            return reverseLookup.get(urn);
        } else {
            throw new RuntimeException("Unrecognized ProviderAuthenticationMethod: \"" + urn + "\"");
        }
    }

    public static boolean isValid(String urn) {
        initializeReverseLookupIfNeeded();
        return (reverseLookup.containsKey(urn));
    }

    private static void initializeReverseLookupIfNeeded() {
        if (reverseLookup.isEmpty()) {
            for (ProviderAuthenticationProtocol v : values()) {
                reverseLookup.put(v.urn, v);
            }
        }
    }
}
