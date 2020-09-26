package org.fastfed4j.core.util;

import org.fastfed4j.core.constants.JsonMember;
import org.fastfed4j.core.constants.SchemaGrammar;
import org.fastfed4j.core.exception.ErrorAccumulator;
import org.fastfed4j.core.exception.FastFedSecurityException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ValidationUtils {

    public boolean validateRequiredString(ErrorAccumulator errorAccumulator, String attributeName, String value) {
        if (value == null || value.isEmpty()) {
            errorAccumulator.add("Missing value for \"" + attributeName + "\"");
            return false;
        }
        return true;
    }

    public boolean validateOptionalStringCollection(ErrorAccumulator errorAccumulator, String attributeName, Collection<String> value) {
        if (value == null || value.size() == 0) {
            return true;
        }
        return validateRequiredStringCollection(errorAccumulator, attributeName, value);
    }

    public boolean validateRequiredStringCollection(ErrorAccumulator errorAccumulator, String attributeName, Collection<String> value) {
        if (value == null || value.size() == 0) {
            errorAccumulator.add("Missing value for \"" + attributeName + "\"");
            return false;
        }
        for (String s : value) {
            if (s == null || s.isEmpty()) {
                errorAccumulator.add("Invalid contents for \"" + attributeName + "\". List contains empty or null members.");
                return false;
            }
        }
        return true;
    }

    public boolean validateRequiredDate(ErrorAccumulator errorAccumulator, String attributeName, Date value) {
        return validateRequiredObject(errorAccumulator, attributeName, value);
    }

    public boolean validateRequiredObject(ErrorAccumulator errorAccumulator, String attributeName, Object o) {
        if (o == null) {
            errorAccumulator.add("Missing value for \"" + attributeName + "\"");
            return false;
        }
        return true;
    }

    public boolean validateRequiredUrl(ErrorAccumulator errorAccumulator, String attributeName, String value) {
        return (validateRequiredString(errorAccumulator, attributeName, value)
                && validateUrl(errorAccumulator, attributeName, value));
    }

    public boolean validateOptionalUrl(ErrorAccumulator errorAccumulator, String attributeName, String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return validateUrl(errorAccumulator, attributeName, value);
    }

    public boolean validateUrl(ErrorAccumulator errorAccumulator, String attributeName, String value) {

        URL url;
        try {
            // Not completely bulletproof, but good enough to catch obvious garbage
            // without adding another dependency or writing complicated regexes.
            // https://stackoverflow.com/questions/1600291/validating-url-in-java
            url = new URL(value);
            url.toURI();
        }
        catch (MalformedURLException | URISyntaxException e) {
            errorAccumulator.add("Invalid url format for \"" + attributeName + "\" (received: \"" + value + "\")");
            return false;
        }

        if (! url.getProtocol().equals("https")) {
            errorAccumulator.add("Invalid url protocol for \"" + attributeName + "\". Must be \"https\" (received: \"" + value + "\")");
            return false;
        }

        return true;
    }

    //Enforce Section 4.1.1 of the FastFed Core specification: Endpoint Validation
    public void assertProviderDomainIsValid(String remoteUrl, String providerDomain) {
        //Step 1: Protocol must be HTTPS
        URL url;
        try {
            url = new URL(remoteUrl);
            if (!url.getProtocol().equals("https")) {
                throw new FastFedSecurityException("Protocol of the FastFed Metadata Endpoint is not HTTPS (\"" + remoteUrl + "\")");
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed url", e);
        }

        //Step 2: Provider domain must suffix-match the remote endpoint URL
        String host = url.getHost().toLowerCase();
        if (! host.endsWith(providerDomain)) {
            throw new FastFedSecurityException(
                    "The URL of the FastFed Metadata Endpoint does not match the value of the provider_domain received within the metadata contents"
                            + "(endpoint_url=\"" + remoteUrl + "\", provider_domain=\"" + providerDomain + "\")"
            );
        }
    }

    public boolean validateSchemaGrammar(ErrorAccumulator errorAccumulator, String objectName, String schemaGrammarString) {
        if (SchemaGrammar.isValid(schemaGrammarString)) {
            return true;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Invalid member of \"");
        builder.append(objectName);
        builder.append("\". Unrecognized schema grammar: \"");
        builder.append(schemaGrammarString);
        builder.append("\". ");
        List<SchemaGrammar> schemaGrammars = Arrays.asList(SchemaGrammar.values());
        if (schemaGrammars.size() == 1) {
            builder.append("Expected: \"");
            builder.append(schemaGrammars.get(0).toString());
            builder.append("\"");
        } else {
            builder.append("Expected one of: ");
            for (int i = 0; i < schemaGrammars.size(); i++) {
                builder.append("\"");
                builder.append(schemaGrammars.get(i).toString());
                builder.append("\"");
                if (i < (schemaGrammars.size() - 1)) {
                    builder.append(", ");
                }
            }
        }
        errorAccumulator.add(builder.toString());
        return false;
    }

}
