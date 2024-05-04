package com.intershop.jdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The URITest shows that, it's difficult to combine a URI correctly.
 * In resource oriented REST-URLs, we need to combine a URI with several identifier. (https://cloud.google.com/apis/design/resources)
 * e.g. /customers/customer-id/addresses/address-id
 * To make it simple, I used one root resource only, but this resource has identifier with nearly all characters. Most important are
 * <li>' ' - because of the super seeded possibility to encode a space with a '+' in the query part
 * <li>'+' - same as ' ', the encoding must make sure that a plus will not be interpreted as space.
 * <li>'/' - path separator for URL</li>
 * <li>';' - separator for matrix parameter in URL</li>
 * <li>'?' - separator for query</li>
 * <li>'&' - separator for query parameters</li>
 * <li>':' - path separator for URN</li>
 */
public class URITest
{
    // we have different scenarios/domain objects with different allowed chars at the identifier
    // Arrays.asList("12345", "2024/1234", "2024-1234", "2024+1234", "USA 2024/1234", "USA 2024+1234");
    private static final Map<String, String> EXPECTED_URI_PATH = new HashMap<>();
    static
    {
        EXPECTED_URI_PATH.put("2024-1234", "https://servername/rest/customers/2024-1234");
        EXPECTED_URI_PATH.put("2024/1234", "https://servername/rest/customers/2024%2F1234");
        EXPECTED_URI_PATH.put("2024 1234", "https://servername/rest/customers/2024%201234");
        EXPECTED_URI_PATH.put("2024+1234", "https://servername/rest/customers/2024+1234");
    }

    private static final String SCHEME = "https";
    private static final String SERVER = "servername";
    private static final String RESOURCE_PATH = "/rest/customers";

    /**
     * Provide arguments for tests
     */
    private static Stream<Arguments> provideCustomerIDandExpectedPath()
    {
        List<Arguments> args = new ArrayList<>();
        for (Map.Entry<String, String> entry : EXPECTED_URI_PATH.entrySet())
        {
            args.add(Arguments.of(entry.getKey(), entry.getValue()));
        }
        return args.stream();
    }

    /**
     * Doesn't work, because URI#resolve ignores the original URI (this)
     */
    @ParameterizedTest()
    @MethodSource("provideCustomerIDandExpectedPath")
    void testURIConstructorWithPathResolve(String customerID, String expectedURIAsString) throws URISyntaxException
    {
        // I would expect that URI#resolve appends the given String (path) to the URI - it doesn't
        assertEquals(URI.create(expectedURIAsString), new URI(SCHEME, SERVER, RESOURCE_PATH).resolve(customerID));
    }

    /**
     * After looking in to the implementation if found an "append", but it doesn't work with "/" in the customer id.
     */
    @ParameterizedTest()
    @MethodSource("provideCustomerIDandExpectedPath")
    void testURIConstructorWithPathResolveURI(String customerID, String expectedURIAsString) throws URISyntaxException
    {
        // need to add many 'null' parameter to make sure that the 'path' is identified correctly.
        assertEquals(URI.create(expectedURIAsString), new URI(SCHEME, SERVER, RESOURCE_PATH+ "/", null, null).resolve(
                        new URI(null, null, customerID, null, null)));
    }
    /**
     * After looking in to the implementation if found an "append", but it doesn't work with "/" in the customer id.
     */
    @Test
    void debugTestURIConstructorWithPathResolveURI() throws URISyntaxException
    {
        assertEquals(URI.create("https://servername/rest/customers/2024%2F1234"), new URI(SCHEME, SERVER, RESOURCE_PATH + "/", null, null).resolve(
                        new URI(null, null, "2024/1234", null, null)));
    }

    /**
     * AS RESULT, we have to encode the path elements by our own. see URLEncodeTest.
     */
}
