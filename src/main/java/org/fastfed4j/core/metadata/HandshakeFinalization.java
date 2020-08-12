package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.json.JWT;

/**
 * Represents a Handshake Finalization Message, as defined in section 7.2.4.1 of the FastFed Core specification.
 */
public class HandshakeFinalization extends JWT {

    public HandshakeFinalization(FastFedConfiguration configuration) {
        super(configuration);
    }
}
