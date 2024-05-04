package com.intershop.jdk;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Example of a URL Builder.
 */
public class URLBuilder
{
    public URLBuilderWithScheme scheme(String scheme)
    {
        return new URLBuilderWithScheme(scheme);
    }

    public static class URLBuilderWithScheme
    {
        private final String scheme;

        private URLBuilderWithScheme(String scheme)
        {
            this.scheme = scheme;
        }

        public URLBuilderWithServer server(String server)
        {
            return new URLBuilderWithServer(scheme, server);
        }
    }

    public static class URLBuilderWithServer
    {
        private final String scheme;
        private final String server;

        URLBuilderWithServer(String scheme, String server)
        {
            this.scheme = scheme;
            this.server = server;
        }

        public URLBuilderWithServerAndPath pathElements(String... elements)
        {
            return new URLBuilderWithServerAndPath(scheme, server, true, elements);
        }
    }

    static class URLBuilderWithAbsolute
    {
        private final String scheme;
        private final String server;
        private final boolean isAbsolute;

        URLBuilderWithAbsolute(String scheme, String server, boolean isAbsolute)
        {
            this.scheme = scheme;
            this.server = server;
            this.isAbsolute = isAbsolute;
        }

        public URLBuilderWithServerAndPath pathElements(String... elements)
        {
            return new URLBuilderWithServerAndPath(scheme, server, isAbsolute, elements);
        }
    }

    public static class URLBuilderWithServerAndPath
    {
        private final String scheme;
        private final String server;
        private final boolean isAbsolute;
        private final String[] pathElements;

        private URLBuilderWithServerAndPath(String scheme, String server, boolean isAbsolute, String[] pathElements)
        {
            this.scheme = scheme;
            this.server = server;
            this.isAbsolute = isAbsolute;
            this.pathElements = pathElements;
        }

        public URI build()
        {
            StringBuilder b = new StringBuilder();
            if (scheme != null)
            {
                b.append(scheme).append(":");
            }
            if (server != null)
            {
                b.append("//").append(server);
            }
            if (isAbsolute)
            {
                b.append("/");
            }
            var uriString = b.append(URIBuilder.combinePathElements('/', pathElements)).toString();
            return URI.create(uriString);
        }

        public URLBuilderWithServerAndPath pathElements(String... elements)
        {
            String[] newElements = Stream.concat(Arrays.stream(pathElements), Arrays.stream(elements)).toArray(String[]::new);
            return new URLBuilderWithServerAndPath(scheme, server, isAbsolute, newElements);
        }

        public String[] getPathElements()
        {
            return pathElements;
        }
    }
}
