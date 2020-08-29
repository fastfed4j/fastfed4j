package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of FastFed Schema URNs
 */
public enum SchemaGrammar {

    SCIM("urn:ietf:params:fastfed:1.0:schemas:scim:2.0");

    private static Map<String, SchemaGrammar> reverseLookup = new ConcurrentHashMap<>();
    private String urn;

    SchemaGrammar(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    @Override
    public String toString() {
        return getUrn();
    }

    public static SchemaGrammar fromString(String urn) {
        initializeReverseLookupIfNeeded();
        if (urn != null && reverseLookup.containsKey(urn)){
            return reverseLookup.get(urn);
        } else {
            throw new RuntimeException("Unrecognized SchemaGrammar: \"" + urn + "\"");
        }
    }

    public static boolean isValid(String urn) {
        initializeReverseLookupIfNeeded();
        return (reverseLookup.containsKey(urn));
    }

    private static void initializeReverseLookupIfNeeded() {
        if (reverseLookup.isEmpty()) {
            for (SchemaGrammar v : values()) {
                reverseLookup.put(v.urn, v);
            }
        }
    }
}
