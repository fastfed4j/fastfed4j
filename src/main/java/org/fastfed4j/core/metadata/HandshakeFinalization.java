package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.json.JsonObject;
import org.fastfed4j.core.json.Jwt;

/**
 * Represents a Handshake Finalization Message, as defined in section 7.2.4.1 of the FastFed Core specification.
 */
public class HandshakeFinalization extends Jwt {

    public HandshakeFinalization(FastFedConfiguration configuration) {
        super(configuration);
    }

    @Override
    public JsonObject toJson() {
        JsonObject.Builder builder = new JsonObject.Builder();
        builder.putAll(super.toJson());
        return builder.build();
    }
}
