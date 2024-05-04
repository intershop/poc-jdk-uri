package com.intershop.jdk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The URLEncodeTest shows that, URLEncode is designed for encoding of the query parameter part of a URI and not for the path elements.
 * URLEncode and URI.create are often used to combine path and query parameter. And leads to many trouble.
 * https://github.com/OAI/OpenAPI-Specification/issues/1778
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
public class URLEncodeTest
{
    private static final String RESOURCE_PATH = "https://servername/rest/customers";
    private static final Map<String, String> EXPECTED_URI_PATH = new HashMap<>();
    static
    {
        EXPECTED_URI_PATH.put("2024-1234", "https://servername/rest/customers/2024-1234");
        EXPECTED_URI_PATH.put("2024/1234", "https://servername/rest/customers/2024%2F1234");
        EXPECTED_URI_PATH.put("2024 1234", "https://servername/rest/customers/2024%201234");
        EXPECTED_URI_PATH.put("2024+1234", "https://servername/rest/customers/2024+1234");
    }

    /**
     * Provide arguments for test
     * @return
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
     * Doesn't work, the space ' ' will be converted to '+'
     * @param customerID
     * @param expectedURIAsString
     * @throws URISyntaxException
     */
    @ParameterizedTest()
    @MethodSource("provideCustomerIDandExpectedPath")
    void testWithURLEncoder(String customerID, String expectedURIAsString) throws URISyntaxException
    {
        String encCustomerID = URLEncoder.encode(customerID, StandardCharsets.UTF_8);
        assertEquals(URI.create(expectedURIAsString), URI.create(RESOURCE_PATH + "/" + encCustomerID));
    }

    /**
     * Current workaround to avoid '+' sign conversion.
     * @param customerID
     * @param expectedURIAsString
     * @throws URISyntaxException
     */
    @ParameterizedTest()
    @MethodSource("provideCustomerIDandExpectedPath")
    void testWithWorkaround(String customerID, String expectedURIAsString) throws URISyntaxException
    {
        String encCustomerID = URLEncoder.encode(customerID.replaceAll("\\+", "%2B"), StandardCharsets.UTF_8).replaceAll("\\+", "%20").replaceAll("%252B", "+");
        assertEquals(URI.create(expectedURIAsString), URI.create(RESOURCE_PATH + "/" + encCustomerID));
    }
}
