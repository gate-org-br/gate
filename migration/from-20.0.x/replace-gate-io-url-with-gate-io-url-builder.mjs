export default {
	name: "Replace gate.io.URL with gate.io.URLBuilder",
	description: "Rewrites gate.io.URL imports and constructor calls to gate.io.URLBuilder when the file clearly uses gate.io.URL.",
	filePredicate: ({file, source}) => /\.(?:java|js|mjs)$/i.test(file) && source.includes("gate.io.URL"),
	apply: ({source}) => source
		.replace(/\bimport\s+gate\.io\.URL\s*;/g, "import gate.io.URLBuilder;")
		.replace(/\bnew\s+gate\.io\.URL\s*\(/g, "new gate.io.URLBuilder(")
		.replace(/\bnew\s+URL\s*\(/g, "new URLBuilder(")
};
