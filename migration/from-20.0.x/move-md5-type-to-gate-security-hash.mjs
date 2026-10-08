export default {
	name: "Move MD5 type to gate.security.hash",
	description: "Moves the MD5 type from gate.type.MD5 to gate.security.hash.MD5.",
	filePredicate: ({file}) => /\.(?:java|html|jsp|js|mjs|wcc|wc|wcs)$/i.test(file),
	apply: ({source}) => source.replace(/\bgate\.type\.MD5\b/g, "gate.security.hash.MD5")
};
