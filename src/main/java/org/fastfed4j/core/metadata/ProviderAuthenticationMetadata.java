package org.fastfed4j.core.metadata;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.constants.ProviderAuthenticationProtocol;

/**
 * Base class for Provider Authentication Metadata, which represent a way for two providers to authenticate
 * directly to each other without the presence of an end-user.
 */
abstract public class ProviderAuthenticationMetadata extends Metadata {

    /**
     * Construct an empty instance
     */
    public ProviderAuthenticationMetadata(FastFedConfiguration configuration) {
        super(configuration);
    }

    /**
     * Copy constructor
     */
    public ProviderAuthenticationMetadata(ProviderAuthenticationMetadata other) {
        super(other);
    }

    /**
     * Get the URN that identifies the Provider Authentication Protocol.
     */
    abstract public ProviderAuthenticationProtocol getProviderAuthenticationProtocol();

}
