export default {
	name: "Remove LinkSource injection and replace getLink usage",
	description: "Removes injected LinkSource fields annotated with @DataSource and rewrites linkSource.getLink() calls to Link.of(dataSourceName).",
	filePredicate: ({file, source}) => /\.java$/i.test(file) && source.includes("LinkSource") && source.includes("getLink()"),
	apply: ({source}) =>
	{
		let migrated = source
			.replace(/^import\s+gate\.sql\.LinkSource\s*;\n/gm, "")
			.replace(/^import\s+gate\.annotation\.DataSource\s*;\n/gm, "");

		const fields = [...migrated.matchAll(/\n\s*@Inject\s*\n\s*@DataSource\("([^"]+)"\)\s*\n\s*(?:private\s+|protected\s+|public\s+)?LinkSource\s+(\w+)\s*;\s*\n/g)];
		for (const field of fields)
		{
			const [, dataSourceName, variableName] = field;
			migrated = migrated
				.replace(field[0], "\n")
				.replace(new RegExp(`\\b${variableName}\\.getLink\\(\\)`, "g"), `Link.of("${dataSourceName}")`);
		}

		return migrated;
	}
};
