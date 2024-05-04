package com.intershop.jdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The URIBuilderTest shows the idea, how a URI could be built easily. Mostly with low know about encoding
 *
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

public class URIBuilderTest
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
    private static final String ONE_CUSTOMER = "2024/1234";

    /**
     * Parameter for tests
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

    @ParameterizedTest()
    @MethodSource("provideCustomerIDandExpectedPath")
    void testSimpleURI(String customerID, String expectedURIAsString)
    {
        URI actual = URIBuilder.createURL().scheme(SCHEME).server(SERVER).pathElements("rest", "customers", customerID).build();
        assertEquals(URI.create(expectedURIAsString), actual);
    }

    /**
     * Simply use one URIBuilder and extend that with sub elements.
     */
    @Test
    void testSecondLevel()
    {
        var customerURI = URIBuilder.createURL()
                                    .scheme(SCHEME)
                                    .server(SERVER)
                                    .pathElements("rest", "customers", ONE_CUSTOMER);
        assertEquals("https://servername/rest/customers/2024%2F1234/addresses/shipping", customerURI.pathElements("addresses", "shipping").build().toString());
        assertEquals("https://servername/rest/customers/2024%2F1234/addresses/billing", customerURI.pathElements("addresses", "billing").build().toString());
    }

    /**
     * Relative URLs are often used, because the REST controller has some scope and doesn't "like" to know it.
     */
    @Test
    void testRelativeURL()
    {
        var addressesURI = URIBuilder.createRelativeURL().pathElements("addresses");
        assertEquals("addresses/shipping%20address", addressesURI.pathElements("shipping address").build().toString());
        assertEquals("addresses/billing%20address", addressesURI.pathElements("billing address").build().toString());
    }

    /**
     * Absolute URLs
     */
    @Test
    void testAbsoluteURL()
    {
        var addressesURI = URIBuilder.createAbsoluteURL().pathElements("addresses");
        assertEquals("/addresses/shipping%20address", addressesURI.pathElements("shipping address").build().toString());
        assertEquals("/addresses/billing%20address", addressesURI.pathElements("billing address").build().toString());
    }

    /**
     * Avoid trouble with duplicate encoding - just provide original path elements URLs
     */
    @Test
    void testJoinTwo()
    {
        var customerURI = URIBuilder.createURL()
                                    .scheme(SCHEME)
                                    .server(SERVER)
                                    .pathElements("rest", "customers", ONE_CUSTOMER);
        var addressesURI = URIBuilder.createRelativeURL().pathElements("addresses", "shipping address");
        var actual = customerURI.pathElements(addressesURI.getPathElements()).build();
        assertEquals("https://servername/rest/customers/2024%2F1234/addresses/shipping%20address", actual.toString());
    }

}
