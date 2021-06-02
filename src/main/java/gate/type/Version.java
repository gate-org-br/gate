package gate.type;


import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.VersionConverter;
import gate.handler.VersionHandler;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Handler(VersionHandler.class)
@Converter(VersionConverter.class)
public class Version implements Serializable, Comparable<Version>
{

	public static final Version UNDEFINED = new Version(0, 0, 0, null, null);
	public static final Version INVALID = new Version(0, 0, 0, "INVALID", null);

	private final int major;
	private final int minor;
	private final int patch;
	private final String qualifier;
	private final String iteration;

	public static final Pattern PATTERN = Pattern.compile("^([0-9]+)[.]([0-9]+)[.]([0-9]+)(-([0-9a-zA-Z]+))?(-([0-9a-zA-Z]+))?$");

	private static final long serialVersionUID = 1L;

	private Version(int major, int minor, int patch,
		String qualifier, String iteration)
	{
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.qualifier = qualifier;
		this.iteration = iteration;
	}

	public int getMajor()
	{
		return major;
	}

	public int getMinor()
	{
		return minor;
	}

	public int getPatch()
	{
		return patch;
	}

	public String getQualifier()
	{
		return qualifier;
	}

	public String getIteration()
	{
		return iteration;
	}

	@Override
	public String toString()
	{
		if (qualifier != null)
			if (iteration != null)
				return major + "." + minor + "." + patch
					+ "-" + qualifier + "-" + iteration;
			else
				return major + "." + minor + "." + patch
					+ "-" + qualifier;
		else
			return major + "." + minor + "." + patch;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Version
			&& ((Version) obj).major == major
			&& ((Version) obj).minor == minor
			&& ((Version) obj).patch == patch
			&& Objects.equals(((Version) obj).qualifier, qualifier)
			&& Objects.equals(((Version) obj).iteration, iteration);
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public int compareTo(Version version)
	{
		if (major != version.major)
			return version.major - major;
		if (minor != version.minor)
			return version.minor - minor;
		if (patch != version.patch)
			return version.patch - patch;

		if (!Objects.equals(qualifier, version.qualifier))
			if (version.qualifier == null)
				return 1;
			else if (qualifier == null)
				return -1;
			else
				return version.qualifier.compareTo(qualifier);

		if (!Objects.equals(iteration, version.iteration))
			if (version.iteration == null)
				return 1;
			else if (iteration == null)
				return -1;
			else
				return version.iteration.compareTo(iteration);

		return 0;
	}

	public static Version of(int major, int minor, int patch)
	{
		return new Version(major, minor, patch, null, null);
	}

	public static Version of(int major, int minor, int patch, String qualifier)
	{
		return new Version(major, minor, patch, qualifier, null);
	}

	public static Version of(int major, int minor, int patch, String qualifier, String iteration)
	{
		return new Version(major, minor, patch, qualifier, iteration);
	}

	public static Version of(String string) throws ParseException
	{
		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			throw new ParseException("Invalid version number: " + string, 0);

		return new Version(Short.parseShort(matcher.group(1)),
			Short.parseShort(matcher.group(2)),
			Short.parseShort(matcher.group(3)),
			matcher.group(5),
			matcher.group(7));
	}

}
