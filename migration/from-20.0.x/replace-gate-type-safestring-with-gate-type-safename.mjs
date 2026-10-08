export default {
	name: "Replace gate.type.SafeString with gate.type.SafeName",
	description: "Rewrites SafeString imports, qualified references, and simple type usages when the file clearly uses gate.type.SafeString.",
	filePredicate: ({file, source}) => /\.(?:java|html|jsp|js|mjs|wcc|wc|wcs)$/i.test(file) && source.includes("gate.type.SafeString"),
	apply: ({source}) => source
		.replace(/\bimport\s+gate\.type\.SafeString\s*;/g, "import gate.type.SafeName;")
		.replace(/\bgate\.type\.SafeString\b/g, "gate.type.SafeName")
		.replace(/\bSafeString\b/g, "SafeName")
};
