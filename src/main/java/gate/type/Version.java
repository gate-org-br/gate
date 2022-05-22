package gate.type;

import gate.annotation.Converter;
import gate.annotation.Handler;
import gate.converter.custom.VersionConverter;
import gate.handler.VersionHandler;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Handler(VersionHandler.class)
@Converter(VersionConverter.class)
public interface Version extends Serializable, Comparable<Version>
{

	public static final Version UNDEFINED = new VersionImpl(0, 0, 0, null, null);
	public static final Version INVALID = new VersionImpl(0, 0, 0, "INVALID", null);
	public static final Pattern PATTERN = Pattern.compile("^([0-9]+)[.]([0-9]+)[.]([0-9]+)(-([0-9a-zA-Z]+))?(-([0-9a-zA-Z]+))?$");

	public abstract int getMajor();

	public abstract int getMinor();

	public abstract int getPatch();

	public abstract String getQualifier();

	public abstract String getIteration();

	@Override
	public String toString();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);

	public static Version of(int major, int minor, int patch)
	{
		return new VersionImpl(major, minor, patch, null, null);
	}

	public static Version of(int major, int minor, int patch, String qualifier)
	{
		return new VersionImpl(major, minor, patch, qualifier, null);
	}

	public static Version of(int major, int minor, int patch, String qualifier, String iteration)
	{
		return new VersionImpl(major, minor, patch, qualifier, iteration);
	}

	public static Version of(String string)
	{
		if (string == null)
			return Version.UNDEFINED;

		Matcher matcher = PATTERN.matcher(string);
		if (!matcher.matches())
			return Version.INVALID;

		return new VersionImpl(Short.parseShort(matcher.group(1)),
			Short.parseShort(matcher.group(2)),
			Short.parseShort(matcher.group(3)),
			matcher.group(5),
			matcher.group(7));
	}

	public static class VersionImpl implements Version
	{

		private final int major;
		private final int minor;
		private final int patch;
		private final String qualifier;
		private final String iteration;

		private static final long serialVersionUID = 1L;

		private VersionImpl(int major, int minor, int patch,
			String qualifier, String iteration)
		{
			this.major = major;
			this.minor = minor;
			this.patch = patch;
			this.qualifier = qualifier;
			this.iteration = iteration;
		}

		@Override
		public int getMajor()
		{
			return major;
		}

		@Override
		public int getMinor()
		{
			return minor;
		}

		@Override
		public int getPatch()
		{
			return patch;
		}

		@Override
		public String getQualifier()
		{
			return qualifier;
		}

		@Override
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
				&& ((Version) obj).getMajor() == major
				&& ((Version) obj).getMinor() == minor
				&& ((Version) obj).getPatch() == patch
				&& Objects.equals(((Version) obj).getQualifier(), qualifier)
				&& Objects.equals(((Version) obj).getIteration(), iteration);
		}

		@Override
		public int hashCode()
		{
			return toString().hashCode();
		}

		@Override
		public int compareTo(Version version)
		{
			if (major != version.getMajor())
				return version.getMajor() - major;
			if (minor != version.getMinor())
				return version.getMinor() - minor;
			if (patch != version.getPatch())
				return version.getPatch() - patch;

			if (!Objects.equals(qualifier, version.getQualifier()))
				if (version.getQualifier() == null)
					return 1;
				else if (qualifier == null)
					return -1;
				else
					return version.getQualifier().compareTo(qualifier);

			if (!Objects.equals(iteration, version.getClass()))
				if (version.getIteration() == null)
					return 1;
				else if (iteration == null)
					return -1;
				else
					return version.getIteration().compareTo(iteration);

			return 0;
		}

	}
}
