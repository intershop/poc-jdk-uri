# Introduction

<b>The content of this project is not production ready</b>

* The first part tries an explanation, why it's difficult to provide a correct encoded URI with JDK java.net classes.
* The second part tries to provide a proposal for easier usage with a URI builder API.


## Expected correct URI

For the given customer REST API endpoint:
`https://servername/rest/customers/<customer-id>`
I would expect the following encoded URI.

<pre>
EXPECTED_URI_PATH.put("2024-1234", "https://servername/rest/customers/2024-1234");
EXPECTED_URI_PATH.put("2024/1234", "https://servername/rest/customers/2024%2F1234");
EXPECTED_URI_PATH.put("2024 1234", "https://servername/rest/customers/2024%201234");
EXPECTED_URI_PATH.put("2024+1234", "https://servername/rest/customers/2024+1234");
</pre>

## Correct URI Encoding with java.net?

### Using java.net.URI

The test class "URITest" shows that the path elements are encoded, but the slash '/' not.
What is to do, if path elements containing slashes like "customer-numbers". 
Patterns with slashes are common. So I could have the customer number "2024/1234" at my local town gas dealer.

<pre>
URI.create("https://servername/rest").resolve(URI.create("/customers/" + customerID));
</pre>

This breaks:
<pre>
EXPECTED_URI_PATH.put("2024/1234", "https://servername/rest/customers/2024%2F1234");
</pre>
because the result is `https://servername/rest/customers/2024/1234` that is a complete different endpoint.

### Using java.net.URLEncode#encode

The test class URLEncodeTest shows the common solution to encode path elements with URLEncode#encode.
That leads to '+' sign in the path of the URL, which is not RFC conform.
The documentation of URLEncode#encode clearly defines, that the method is valid for the query part.
<pre>
Translates a string into application/ x-www-form-urlencoded
format using a specific Charset.
</pre>

<pre>
URI.create("https://servername/rest/customers/" + URLEncode.encode(customerID));
</pre>

This breaks:
<pre>
EXPECTED_URI_PATH.put("2024 1234", "https://servername/rest/customers/2024%201234");
</pre>
because the result is `https://servername/rest/customers/2024+1234` that could be another customer.

## Workaround with java.net

URLEncode.encode doing it well, except the '+' sign encoding. And this method is available in other languages in a similar way (e.g. typescript)
<pre>
URLEncoder.encode(customerID.replaceAll("\\+", "%2B"), StandardCharsets.UTF_8)
  .replaceAll("\\+", "%20").replaceAll("%252B", "+");
</pre>

# Proposal for URIBuilder API

Personally, I would prefer a builder pattern, where the attributes are declarative assigned.

<pre>
var customerURIBuilder = URIBuilder.createURL()
                            .scheme(SCHEME)
                            .server(SERVER)
                            .pathElements("rest", "customers", "2024/1234");
URI uri = customerURIBuilder.build();
</pre>

The test class `URIBuilderTest` contains also example for relative URLs.

<pre>
var relativeAddress = URIBuilder.createRelativeURL()
                            .pathElements("addresses", "shipping address")
                            .build();
</pre>

Combining both
<pre>
URI addressURI = customerURIBuilder.pathElements(relativeAddress.getPathElements()).build();
</pre>

The proposal is incomplete and should only explain the idea.
