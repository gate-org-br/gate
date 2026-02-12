package gate.util;

import gate.converter.Converter;
import gate.type.Parameter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Container for URL query parameters.
 *
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 * 	<li>Storing name/value pairs while preserving insertion order</li>
 * 	<li>Serializing parameters into a URL-encoded query string</li>
 * 	<li>Parsing existing query strings</li>
 * </ul>
 *
 * <p>
 * All encoding and decoding operations use UTF-8.
 * </p>
 */
public class Parameters
{

    private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    /**
     * Creates an empty parameters container.
     */
    public Parameters()
    {
    }

    /**
     * Creates a defensive copy of another {@code Parameters} instance.
     *
     * @param other parameters to copy
     */
    public Parameters(Parameters other)
    {
        if (other != null)
            values.putAll(other.values);
    }

    /**
     * Creates parameters from a list of {@link Parameter}.
     *
     * @param parameters list of parameters
     */
    public Parameters(List<Parameter> parameters)
    {
        if (parameters == null)
            return;

        for (Parameter p : parameters)
            if (p != null)
                values.put(p.getName(), p.getValue());
    }

    /**
     * Indicates whether there are no parameters.
     *
     * @return {@code true} if empty
     */
    public boolean isEmpty()
    {
        return values.isEmpty();
    }

    /**
     * Returns the value associated with a parameter name.
     *
     * @param key parameter name
     * @return associated value or {@code null}
     */
    public Object get(String key)
    {
        return values.get(key);
    }

    /**
     * Adds or removes a parameter.
     *
     * <p>
     * If {@code value} is {@code null}, the parameter is removed.
     * </p>
     *
     * @param name  parameter name
     * @param value parameter value
     * @return this instance
     */
    public Parameters put(String name, Object value)
    {
        if (value != null)
            values.put(name, value);
        else
            values.remove(name);
        return this;
    }

    /**
     * Parses a query string and adds its parameters.
     *
     * <p>
     * The query string must follow the format {@code a=b&c=d}.
     * Names and values are URL-decoded using UTF-8.
     * </p>
     *
     * @param query query string
     * @return this instance
     * @throws IllegalArgumentException if the query string is invalid
     */
    public Parameters put(String query)
    {
        if (query == null || query.isEmpty())
            return this;

        for (String pair : query.split("&"))
        {
            int idx = pair.indexOf('=');

            if (idx <= 0)
                throw new IllegalArgumentException(query + " is not a valid query string");

            String name = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);

            if (!value.isEmpty())
                values.put(name, value);
        }

        return this;
    }

    /**
     * Adds all parameters from another {@code Parameters} instance.
     *
     * @param parameters parameters to add
     * @return this instance
     */
    public Parameters put(Parameters parameters)
    {
        if (parameters == null)
            return this;

        values.putAll(parameters.values);
        return this;
    }

    /**
     * Removes a parameter by name.
     *
     * @param name parameter name
     */
    public void remove(String name)
    {
        values.remove(name);
    }

    /**
     * Returns an unmodifiable view of the parameter entries.
     *
     * @return set of entries
     */
    public Set<Map.Entry<String, Object>> entrySet()
    {
        return Collections.unmodifiableSet(values.entrySet());
    }

    /**
     * Serializes the parameters into a URL-encoded query string.
     *
     * @return encoded query string
     */
    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("&");

        for (Map.Entry<String, Object> e : values.entrySet())
        {
            if (e.getValue() == null)
                continue;

            String value = Converter.toString(e.getValue());
            if (value.isEmpty())
                continue;

            joiner.add(
                    URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                            + "="
                            + URLEncoder.encode(value, StandardCharsets.UTF_8)
            );
        }

        return joiner.toString();
    }

    /**
     * Creates a {@code Parameters} instance from a query string.
     *
     * @param query query string
     * @return new {@code Parameters}
     */
    public static Parameters parse(String query)
    {
        return new Parameters().put(query);
    }
}
