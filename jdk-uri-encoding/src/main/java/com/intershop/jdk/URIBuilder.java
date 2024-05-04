package com.intershop.jdk;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class provides easy possibility to create kinds two kinds of URI, URL and URN.
 */
public class URIBuilder
{
    /**
     * The hexadecimal characters in upper case.
     */
    private static final String HEX_CODE_UPPER = "0123456789ABCDEF";

    public static URLBuilder createURL()
    {
        return new URLBuilder();
    }
    public static URLBuilder.URLBuilderWithAbsolute createRelativeURL()
    {
        return new URLBuilder.URLBuilderWithAbsolute(null, null, false);
    }
    public static URLBuilder.URLBuilderWithAbsolute createAbsoluteURL()
    {
        return new URLBuilder.URLBuilderWithAbsolute(null, null, true);
    }

    public static URNBuilder createURN()
    {
        return new URNBuilder();
    }

    /**
     * Encodes URI components and joins them with a slash
     * @param pathSeparator URI path elements are separated by (mostly '/' or ':')
     * @param pathElements components for URI
     * @return joined for path info (don't encode it again) without leading and trailing slash
     */
    public static String combinePathElements(Character pathSeparator, String[] pathElements)
    {
        return Arrays.stream(pathElements).map((e) -> URIBuilder.encodeEntry(e, pathSeparator)).collect(Collectors.joining("/"));
    }

    private static String encodeEntry(String entry, Character pathSeparator)
    {
        byte stringBytes[] = entry.getBytes(StandardCharsets.UTF_8);

        // get length of byte array
        int l = stringBytes.length;
        StringBuilder builder = new StringBuilder(entry.length());
        // for each character
        for (int i = 0; i < l; i++)
        {
            // get the next byte
            byte chValue = stringBytes[i];

            // see RFC 3986 for which characters must be encoded and which not
            // https://datatracker.ietf.org/doc/html/rfc3986#appendix-A (path-rootless)
            if (isPchar(chValue) && pathSeparator != chValue)
            {
                builder.append((char) chValue);
            }
            else
            {
                // add the % sign
                builder.append('%');

                // get the int value
                int intValue = chValue;

                // get the hex value
                builder.append(HEX_CODE_UPPER.charAt((intValue >>> 4) & 0xf));
                builder.append(HEX_CODE_UPPER.charAt(intValue & 0xf));
            }
        }
        return builder.toString();
    }

    /**
     * from RFC pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
     * @param chValue
     * @return true if chValue is "pchar" except the pct-encoded defined by RFC 3986
     */
    private static boolean isPchar(byte chValue)
    {
        return isUnreserved(chValue) || isSubDelims(chValue) || (chValue == ':') || (chValue == '@');
    }

    /**
     * @param chValue
     * @return true if chValue is "unreserved" defined by RFC 3986
     */
    private static boolean isUnreserved(byte chValue)
    {
        return (chValue == '-') || (chValue == '_') || (chValue == '.') || (chValue == '~')
                        // digits 0-9
                        || (chValue >= 48) && (chValue <= 57)
                        // lowercase characters a-z
                        || (chValue >= 65) && (chValue <= 90)
                        // uppercase characters A-Z
                        || (chValue >= 97) && (chValue <= 122);
    }

    /**
     * from RFC sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
     *                        / "*" / "+" / "," / ";" / "="
     * @param chValue
     * @return true if chValue is "sub-delims" defined by RFC 3986.
     */
    private static boolean isSubDelims(byte chValue)
    {
        return (chValue == '!') || (chValue == '$') || (chValue == '&') || (chValue == '\'') || (chValue == '(') || (chValue == ')')
                        || (chValue == '*') || (chValue == '+') || (chValue == ',') || (chValue == ';') || (chValue == '=');
    }
}
