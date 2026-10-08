export default {
	name: "Remove EnumStringConverter annotations",
	description: "Removes @gate.annotation.Converter(EnumStringConverter.class) and @Converter(EnumStringConverter.class) because EnumConverter now persists enums as strings by default.",
	filePredicate: ({file, source}) => /\.(?:java)$/i.test(file)
		&& (source.includes("EnumStringConverter.class")
		|| source.includes("gate.converter.EnumStringConverter")),
	apply: ({source}) =>
	{
		let migrated = source
			.replace(/\n[ \t]*@gate\.annotation\.Converter\s*\(\s*EnumStringConverter\.class\s*\)[ \t]*/g, "\n")
			.replace(/\n[ \t]*@Converter\s*\(\s*EnumStringConverter\.class\s*\)[ \t]*/g, "\n");

		if (!migrated.includes("EnumStringConverter"))
			migrated = migrated.replace(/\bimport\s+gate\.converter\.EnumStringConverter\s*;\s*\n/g, "");

		return migrated;
	}
};
