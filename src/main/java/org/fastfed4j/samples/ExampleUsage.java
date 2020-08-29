package org.fastfed4j.samples;

import org.fastfed4j.core.configuration.FastFedConfiguration;
import org.fastfed4j.core.contract.Contract;
import org.fastfed4j.core.contract.ContractChange;
import org.fastfed4j.core.contract.ContractProposal;
import org.fastfed4j.core.contract.ContractProposalStatus;
import org.fastfed4j.core.exception.InvalidMetadataException;
import org.fastfed4j.core.metadata.ApplicationProviderMetadata;
import org.fastfed4j.core.metadata.IdentityProviderMetadata;
import org.fastfed4j.profile.saml.enterprise.EnterpriseSAML;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;


public class ExampleUsage {

    public static void main(String [] args) {

        final FastFedConfiguration config = FastFedConfiguration.DEFAULT;

        /*
         EXAMPLE OF APPLICATION PROVIDER METADATA FROM JSON
         */
        ApplicationProviderMetadata appMetadata =
                ApplicationProviderMetadata.fromJson(config, getSampleJsonForAppMetadata());

        System.out.println("\nAPPLICATION PROVIDER METADATA\n---------------------");
        System.out.println("AppMetadata entity_id = " + appMetadata.getEntityId());
        System.out.println("AppMetadata handshake_register_uri = " + appMetadata.getHandshakeRegisterUri());
        System.out.println("AppMetadata.Capabilities authentication_profiles = " + appMetadata.getCapabilities().getAuthenticationProfiles());

        EnterpriseSAML.ApplicationProviderMetadataExtension samlExtensions = appMetadata.getEnterpriseSamlExtension();
        System.out.println("AppMetadata.EnterpriseSamlExtensions required_user_attributes = " + samlExtensions.getDesiredAttributes().getRequiredUserAttributes());

         /*
          EXAMPLE OF IDENTITY PROVIDER METADATA FROM JSON
         */
        IdentityProviderMetadata idpMetadata =
                IdentityProviderMetadata.fromJson(config, getSampleJsonForIdpMetadata());

        System.out.println("\nIDENTITY PROVIDER METADATA\n---------------------");
        System.out.println("IdpMetadata entity_id = " + idpMetadata.getEntityId());
        System.out.println("IdpMetadata handshake_start_uri = " + idpMetadata.getHandshakeStartUri());
        System.out.println("IdpMetadata.Capabilities authentication_profiles = " + idpMetadata.getCapabilities().getAuthenticationProfiles());

        /*
         EXAMPLE OF ERRORS FROM MALFORMED JSON
         */
        System.out.println("\nMALFORMED JSON\n---------------------");
        try {
            ApplicationProviderMetadata.fromJson(config, getInvalidJsonForAppMetadata());
        }
        catch (InvalidMetadataException ex) {
            System.out.println(ex.toString());
        }

        /*
          EXAMPLE OF CONTRACT
         */
        Contract contract = new Contract(idpMetadata, appMetadata);
        System.out.println("\nContract\n---------------------");
        System.out.println("IdentityProvider=" + contract.getIdentityProvider().getEntityId());
        System.out.println("ApplicationProvider=" + contract.getApplicationProvider().getEntityId());
        System.out.println("EnabledProfiles=(" + contract.getEnabledProfiles().toString() + ")");
        System.out.println("App FastFedHandshakeRegisterUri=" + contract.getApplicationProvider().getHandshakeRegisterUri());
        System.out.println("App SAML Required User Attributes=" + contract.getApplicationProvider().getEnterpriseSamlDesiredAttributes().getRequiredUserAttributes());
        System.out.println("App SCIM Required User Attributes=" + contract.getApplicationProvider().getEnterpriseScimDesiredAttributes().getRequiredUserAttributes());

        /*
          EXAMPLE OF REGISTRATION REQUEST
         */
        contract.validateAndOverlayRegistrationRequest( getSampleJsonForRegistrationRequest());
        System.out.println("\nContract overlaid with data from Registration Request\n---------------------");
        System.out.println("Enabled Profiles=(" + contract.getEnabledProfiles().toString() + ")");
        System.out.println("IdP Saml Metadata Uri=" + contract.getIdentityProvider().getEnterpriseSamlMetadataUri());
        System.out.println("IdP Scim Client Org=" + contract.getIdentityProvider().getEnterpriseScimClientContactInformation().getOrganization());
        System.out.println("IdP Scim Client Jwks Uri=" + contract.getIdentityProvider().getEnterpriseScimOauth2JwtClient().getJwksUri());

        /*
          EXAMPLE OF REGISTRATION RESPONSE
         */
        contract.validateAndOverlayRegistrationResponse( getSampleJsonForRegistrationResponse());
        System.out.println("\nContract overlaid with data from Registration Response\n---------------------");
        System.out.println("App Saml Metadata Uri=" + contract.getApplicationProvider().getEnterpriseSamlMetadataUri());
        System.out.println("App Scim Service Uri=" + contract.getApplicationProvider().getEnterpriseScimServiceUri());
        System.out.println("App Handshake Finish Uri=" + contract.getApplicationProvider().getHandshakeFinalizeUri());
        System.out.println("App Scim Service Uri=" + contract.getApplicationProvider().getEnterpriseScimServiceUri());
        System.out.println("App Scim Service Outh Token Endpoint=" + contract.getApplicationProvider().getEnterpriseScimOauth2JwtService().getOauthTokenEndpoint());
        System.out.println("App Scim Service Outh Token Scope=" + contract.getApplicationProvider().getEnterpriseScimOauth2JwtService().getOauthScope());

        /*
          EXAMPLE OF CONTRACT CHANGE

          The following simulates an ApplicationProvider who wants to edit a Contract
          to turn off SCIM provisioning.
         */

        // First, we need new Application Metadata with SCIM provisioning removed from the
        // list of provisioning profiles. This is what the app would send when re-running
        // the handshake.
        ApplicationProviderMetadata newAppMetadata = ApplicationProviderMetadata.fromJson(config, getSampleJsonForAppMetadata());
        newAppMetadata.getCapabilities().setProvisioningProfiles( new HashSet<>()); //Empty value for Provisioning

        // Next, the Idp would receive this. It needs to make a new proposed contract.
        Contract proposedContract = new Contract(idpMetadata, newAppMetadata);

        // Now, the IdP can compare the two contracts.
        ContractChange change = new ContractChange(proposedContract, contract);
        System.out.println("\nContract Change\n---------------------");
        System.out.println("ChangeType=" + change.getChangeType().toString());
        System.out.println("AuthenticationProfilesAdded=" + change.getAuthenticationProfilesAdded());
        System.out.println("AuthenticationProfilesRemoved=" + change.getAuthenticationProfilesRemoved());
        System.out.println("ProvisioningProfilesAdded=" + change.getProvisioningProfilesAdded());
        System.out.println("ProvisioningProfilesRemoved=" + change.getProvisioningProfilesRemoved());

        /*
          EXAMPLE OF CONTRACT TERMINATION

          Termination is simply a contract change in which the Application Provider requests that
          all capabilities be disabled.
         */

        // First, we need new Application Metadata with empty values for both
        // authentication and provisioning.
        ApplicationProviderMetadata terminateMetadata = ApplicationProviderMetadata.fromJson(config, getSampleJsonForAppMetadata());
        terminateMetadata.getCapabilities().setAuthenticationProfiles( new HashSet<>()); //Empty value for Authentication
        terminateMetadata.getCapabilities().setProvisioningProfiles( new HashSet<>()); //Empty value for Provisioning

        // Next, the Idp would receive this. It needs to make a new proposed contract.
        Contract proposedContractTermination = new Contract(idpMetadata, terminateMetadata);

        // Now, the IdP can compare the two contracts & observe the termination request.
        ContractChange termination = new ContractChange(proposedContractTermination, contract);
        System.out.println("\nContract Termination\n---------------------");
        System.out.println("ChangeType=" + termination.getChangeType().toString());
        System.out.println("AuthenticationProfilesAdded=" + termination.getAuthenticationProfilesAdded());
        System.out.println("AuthenticationProfilesRemoved=" + termination.getAuthenticationProfilesRemoved());
        System.out.println("ProvisioningProfilesAdded=" + termination.getProvisioningProfilesAdded());
        System.out.println("ProvisioningProfilesRemoved=" + termination.getProvisioningProfilesRemoved());

        /*
          TO JSON
         */
        System.out.println("\nExample JSON Serialization\n---------------------");
        System.out.println(idpMetadata.toJson().toString());
        System.out.println(appMetadata.toJson().toString());
        System.out.println(contract.toJson().toString());

        ContractProposal proposal = new ContractProposal(contract, ContractProposalStatus.Pending, new Date());
        System.out.println(proposal.toJson().toString());

    }

    public static String getInvalidJsonForAppMetadata() {
        return "{\n" +
                "   \"application_provider\": {\n" +
                "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
                "     \"provider_domain\": 23,\n" +
                "     \"provider_contact_information\": {\n" +
                "       \"organization\": \"Example Inc.\",\n" +
                "       \"phone\": true,\n" +
                "       \"email\": 3.14\n" +
                "     },\n" +
                "     \"display_settings\": {\n" +
                "       \"display_name\": \"Example Application Provider\",\n" +
                "       \"logo_uri\": \"garbage\",\n" +
                "       \"icon_uri\": \"https://app.example.com/images/icon.png\",\n" +
                "       \"license\": {\"key\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"}\n" +
                "     },\n" +
                "     \"capabilities\": {\n" +
                "       \"authentication_profiles\": \"garbage\"\n" +
                "       \"provisioning_profiles\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
                "       ],\n" +
                "       \"schema_grammars\": [\n" +
                "         100\n" +
                "       ],\n" +
                "       \"signing_algorithms\": [\n" +
                "         \"ES512\",\n" +
                "         \"RS256\"\n" +
                "       ]\n" +
                "     },\n" +
                "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
                "       \"desired_attributes\": 12\n" +
                "     },\n" +
                "     \"fastfed_handshake_register_uri\": \"http://tenant-67890.app.example.com/fastfed/register\"\n" +
                "     }\n" +
                " }";
    }

    public static String getSampleJsonForAppMetadata() {
        return "{\n" +
                "   \"application_provider\": {\n" +
                "     \"entity_id\": \"https://tenant-67890.app.example.com/\",\n" +
                "     \"provider_domain\": \"app.example.com\",\n" +
                "     \"provider_contact_information\": {\n" +
                "       \"organization\": \"Example Inc.\",\n" +
                "       \"phone\": \"+1-800-555-5555\",\n" +
                "       \"email\": \"support@example.com\"\n" +
                "     },\n" +
                "     \"display_settings\": {\n" +
                "       \"display_name\": \"Example Application Provider\",\n" +
                "       \"logo_uri\": \"https://app.example.com/images/logo.png\",\n" +
                "       \"icon_uri\": \"https://app.example.com/images/icon.png\",\n" +
                "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
                "     },\n" +
                "     \"capabilities\": {\n" +
                "       \"authentication_profiles\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
                "       ],\n" +
                "       \"provisioning_profiles\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
                "       ],\n" +
                "       \"schema_grammars\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
                "       ],\n" +
                "       \"signing_algorithms\": [\n" +
                "         \"ES512\",\n" +
                "         \"RS256\"\n" +
                "       ]\n" +
                "     },\n" +
                "     \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
                "       \"desired_attributes\": {\n" +
                "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
                "           \"required_user_attributes\": [\n" +
                "             \"username\",\n" +
                "             \"emails[primary eq true].value\"\n" +
                "           ],\n" +
                "           \"optional_user_attributes\": [\n" +
                "             \"displayName\",\n" +
                "             \"phoneNumbers[primary eq true].value\"\n" +
                "           ]\n" +
                "         }\n" +
                "       }\n" +
                "     },\n" +
                "     \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
                "       \"desired_attributes\": {\n" +
                "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\": {\n" +
                "           \"required_user_attributes\": [\n" +
                "             \"externalId\",\n" +
                "             \"username\",\n" +
                "             \"emails[primary eq true].value\"\n" +
                "           ],\n" +
                "           \"optional_user_attributes\": [\n" +
                "             \"displayName\",\n" +
                "             \"phoneNumbers[primary eq true].value\"\n" +
                "           ]\n" +
                "         }\n" +
                "       }\n" +
                "     },\n" +
                "     \"fastfed_handshake_register_uri\": \"https://tenant-67890.app.example.com/fastfed/register\"\n" +
                "     }\n" +
                " }";
    }

    public static String getSampleJsonForIdpMetadata() {
        return  " {\n" +
                "   \"identity_provider\": {\n" +
                "     \"entity_id\": \"https://tenant-12345.idp.example.com/,\"\n" +
                "     \"provider_domain\": \"example.com\",\n" +
                "     \"provider_contact_information\": {\n" +
                "       \"organization\": \"Example Inc.\",\n" +
                "       \"phone\": \"+1-800-555-5555\",\n" +
                "       \"email\": \"support@example.com\"\n" +
                "     },\n" +
                "     \"display_settings\": {\n" +
                "       \"display_name\": \"Example Identity Provider\",\n" +
                "       \"logo_uri\": \"https://idp.example.com/images/logo.png\",\n" +
                "       \"icon_uri\": \"https://idp.example.com/images/icon.png\",\n" +
                "       \"license\": \"https://openid.net/intellectual-property/licenses/fastfed/1.0/\"\n" +
                "     },\n" +
                "     \"capabilities\": {\n" +
                "       \"authentication_profiles\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"\n" +
                "       ],\n" +
                "       \"provisioning_profiles\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"\n" +
                "       ],\n" +
                "       \"schema_grammars\": [\n" +
                "         \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\"\n" +
                "       ],\n" +
                "       \"signing_algorithms\": [\n" +
                "         \"ES512\",\n" +
                "         \"RS256\"\n" +
                "       ]\n" +
                "     },\n" +
                "     \"jwks_uri\": \"https://idp.example.com/keys\",\n" +
                "     \"fastfed_handshake_start_uri\": \"https://tenant-12345.idp.example.com/fastfed/start\"\n" +
                "   }\n" +
                " }";
    }

    public static String getSampleJsonForRegistrationRequest() {
        return " {\n" +
                "   \"iss\": \"https://tenant-12345.idp.example.com\",\n" +
                "   \"aud\": \"https://tenant-67890.app.example.com\",\n" +
                "   \"exp\": 1234567890,\n" +
                "   \"authentication_profiles\": [\"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\"],\n" +
                "   \"provisioning_profiles\": [\"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\"],\n" +
                "   \"schema_grammar\": \"urn:ietf:params:fastfed:1.0:schemas:scim:2.0\",\n" +
                "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
                "     \"saml_metadata_uri\": \"https://tenant-12345.idp.example.com/saml-metadata.xml\",\n" +
                "   },\n" +
                "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
                "     \"provider_contact_information\": {\n" +
                "       \"organization\": \"Example Inc.\",\n" +
                "       \"phone\": \"+1-800-555-6666\",\n" +
                "       \"email\": \"provisioning@example.com\"\n" +
                "     },\n" +
                "     \"provider_authentication_methods\": {\n" +
                "       \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\": {\n" +
                "         \"jwks_uri\": \"https://provisioning.example.com/keys\"\n" +
                "       }\n" +
                "     }\n" +
                "   }\n" +
                " }";
    }

    public static String getSampleJsonForRegistrationResponse() {
        return " {  \n" +
                "   \"fastfed_handshake_finalize_uri\": \"https://tenant-67890.app.example.com/fastfed/finalize\",\n" +
                "   \"urn:ietf:params:fastfed:1.0:authentication:saml:2.0:enterprise\": {\n" +
                "     \"saml_metadata_uri\": \"https://tenant-67890.app.example.com/saml-metadata.xml\"\n" +
                "   },\n" +
                "   \"urn:ietf:params:fastfed:1.0:provisioning:scim:2.0:enterprise\": {\n" +
                "     \"scim_service_uri\": \"https://tenant-67890.app.example.com/scim\",\n" +
                "     \"provider_authentication_method\": \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\",\n" +
                "     \"urn:ietf:params:fastfed:1.0:provider_authentication:oauth:2.0:jwt_profile\":\n" +
                "     {\n" +
                "       \"token_endpoint\": \"https://tenant-67890.app.example.com/oauth\",\n" +
                "       \"scope\": \"scim_provisioning\"\n" +
                "     }\n" +
                "   }\n" +
                " }";
    }
}
