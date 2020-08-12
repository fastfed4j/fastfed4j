package org.fastfed4j.profile;

import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;
import org.fastfed4j.profile.scim.enterprise.EnterpriseSCIM;

import java.util.Arrays;

/**
 Static registry of all known Profiles implemented by this library,
 plus convenience methods to access a list of all the profiles or a particular
 profile definition.
 */
public class KnownProfiles {

    public static final Profile ENTERPRISE_SAML = new EnterpriseSAML();
    public static final Profile ENTERPRISE_SCIM = new EnterpriseSCIM();

    public static final ProfileRegistry ALL = new ProfileRegistry(Arrays.asList(
            ENTERPRISE_SAML,
            ENTERPRISE_SCIM
    ));

}
