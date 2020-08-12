package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of FastFed Authentication Profile URNs
 */
public enum AuthenticationProfile {

    ENTERPRISE_SAML("urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise");

    private static final Map<String, AuthenticationProfile> reverseLookup = new ConcurrentHashMap<>();
    private final String urn;

    AuthenticationProfile(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    @Override
    public String toString() {
        return getUrn();
    }

    public static AuthenticationProfile fromString(String urnString) {
        initializeReverseLookupIfNeeded();
        if (urnString != null && reverseLookup.containsKey(urnString)){
            return reverseLookup.get(urnString);
        } else {
            throw new RuntimeException("Unrecognized AuthenticationProfile: \"" + urnString + "\"");
        }
    }

    public static boolean isValid(String urn) {
        initializeReverseLookupIfNeeded();
        return (reverseLookup.containsKey(urn));
    }

    private static void initializeReverseLookupIfNeeded() {
        if (reverseLookup.isEmpty()) {
            for (AuthenticationProfile v : values()) {
                reverseLookup.put(v.urn, v);
            }
        }
    }
}
