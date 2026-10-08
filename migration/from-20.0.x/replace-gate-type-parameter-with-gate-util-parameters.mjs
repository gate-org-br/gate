export default {
	name: "Replace gate.type.Parameter with gate.util.Parameters",
	description: "Rewrites Parameter imports, qualified references, and simple type usages when the file clearly uses gate.type.Parameter.",
	filePredicate: ({file, source}) => /\.(?:java|html|jsp|js|mjs|wcc|wc|wcs)$/i.test(file) && source.includes("gate.type.Parameter"),
	apply: ({source}) => source
		.replace(/\bimport\s+gate\.type\.Parameter\s*;/g, "import gate.util.Parameters;")
		.replace(/\bgate\.type\.Parameter\b/g, "gate.util.Parameters")
		.replace(/\bParameter\b/g, "Parameters")
};
