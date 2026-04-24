package gate.util;

import gate.error.AppException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ConsoleParameters
{

	private final List<String> list = new ArrayList<>();
	private final Map<Flag, String> map = new HashMap<>();

	private static final Pattern SHORTCUT = Pattern.compile("(-[a-zA-Z0-9.:]+)(=([^ ]+))?");
	private static final Pattern LONGNAME = Pattern.compile("(--[a-zA-Z0-9.:]+)(=([^ ]+))?");

	private ConsoleParameters()
	{
	}

	public static ConsoleParameters parse(String[] args, Flag... flags) throws ParseException
	{
		ConsoleParameters parameters = new ConsoleParameters();

		int i = 0;
		while (i < args.length)
		{
			Matcher matcher = SHORTCUT.matcher(args[i]);
			if (matcher.matches())
			{
				int index = i;
				String shortcut = matcher.group(1);
				Flag flag = Stream.of(flags)
					.filter(e -> e.getShortcut().equals(shortcut))
					.findAny().orElseThrow(() -> new ParseException(shortcut + " is not a valid flag", index));

				String value = matcher.group(3);
				if (value == null)
				{
					value = flag.getDefaultValue();
					if (value == null)
						throw new ParseException(shortcut + " does not have a default value", index);
				}

				if (parameters.map.containsKey(flag))
					throw new ParseException("Duplicate flag specified: " + shortcut, index);
				parameters.map.put(flag, value);

			} else
			{
				matcher = LONGNAME.matcher(args[i]);
				if (matcher.matches())
				{
					int index = i;
					String longname = matcher.group(1);
					Flag flag = Stream.of(flags)
						.filter(e -> e.getLongname().equals(longname))
						.findAny().orElseThrow(() -> new ParseException(longname + " is not a valid flag", index));

					String value = matcher.group(2);
					if (value == null)
					{
						value = flag.getDefaultValue();
						if (value == null)
							throw new ParseException(longname + " does not have a default value", index);
					}

					if (parameters.map.containsKey(flag))
						throw new ParseException("Duplicate flag specified: " + longname, index);
					parameters.map.put(flag, value);
				} else
				{
					parameters.list.add(args[i]);
				}
			}

			i++;
		}
		return parameters;
	}

	public List<String> get()
	{
		return Collections.unmodifiableList(list);
	}

	public Optional<String> get(Flag flag) throws AppException
	{
		return map.containsKey(flag)
			? Optional.of(map.get(flag))
			: Optional.empty();
	}

	public static class Flag
	{

		private final String shortcut;
		private final String longname;
		private final String description;
		private final String defaultValue;

		private Flag(String shortcut, String longname, String description, String defaultValue)
		{
			this.shortcut = Objects.requireNonNull(shortcut);
			this.longname = Objects.requireNonNull(longname);
			this.description = Objects.requireNonNull(description);
			this.defaultValue = defaultValue;
		}

		public String getShortcut()
		{
			return shortcut;
		}

		public String getLongname()
		{
			return longname;
		}

		public String getDescription()
		{
			return description;
		}

		public String getDefaultValue()
		{
			return defaultValue;
		}

		public static Builder builder()
		{
			return new Builder();
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Flag
				&& Objects.equals(shortcut, ((Flag) obj).shortcut)
				&& Objects.equals(longname, ((Flag) obj).longname)
				&& Objects.equals(description, ((Flag) obj).description)
				&& Objects.equals(defaultValue, ((Flag) obj).defaultValue);
		}

		@Override
		public int hashCode()
		{
			return toString().hashCode();
		}

		@Override
		public String toString()
		{
			return shortcut + ", " + longname + ": " + description
				+ (defaultValue != null ? " = " + defaultValue : "");
		}

		public static class Builder
		{

			private String shortcut;
			private String longname;
			private String description;
			private String defaultValue;

			private Builder()
			{

			}

			public Builder shortcut(String shortcut)
			{
				this.shortcut = shortcut;
				return this;
			}

			public Builder longname(String longname)
			{
				this.longname = longname;
				return this;
			}

			public Builder description(String description)
			{
				this.description = description;
				return this;
			}

			public Builder defaultValue(String defaultValue)
			{
				this.defaultValue = defaultValue;
				return this;
			}

			public Flag build()
			{
				return new Flag(shortcut, longname, description, defaultValue);
			}

		}
	}
}
