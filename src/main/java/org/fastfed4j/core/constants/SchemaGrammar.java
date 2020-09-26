package org.fastfed4j.core.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration of FastFed Schema URNs
 */
public enum SchemaGrammar {

    // Implementors Note: Currently, this SDK only supports the SCIM 2.0 schema. It is not possible
    // for consumers to self-service plug in additional schemas. Schema evolutions should be rare
    // events. The FastFed specification highly encourages convergence on SCIM 2.0 as the schema grammar
    // for describing user/group attributes.
    // If additional schemas become necessary in the future, they can be added here. However, several
    // other sections of the library will need to be updated to handle the new schema. Many of these can
    // be found by executing the test suite and noting the exceptions generated with messages indicating
    // a missing handler for a schema.
    // One area requiring deeper attention will be the ContractChange class, which provides a convenient
    // summary of the DesiredAttributes being added/removed as a result of a FastFed Handshake. If it becomes
    // possible for a Provider to change the schema_grammar in use for an existing FastFed relationship, it introduces
    // additional complexity. For example, if the DesiredAttributes were previously expressed in
    // the SCIM grammar, but a Provider initiates an update with a different schema grammar, it becomes complex to
    // to determine whether this causes a change in the user/group attributes being released to an Application.
    // Solving this robustly requires some sort of transformation logic from the old to the new schema grammar.

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
