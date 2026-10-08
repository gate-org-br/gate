export default {
	name: "Move mechanical javax imports to jakarta",
	description: "Rewrites direct package migrations from javax to jakarta for annotation, inject, enterprise, servlet, and JAX-RS namespaces.",
	filePredicate: ({file, source}) => /\.java$/i.test(file) && /\bjavax\.(annotation|inject|enterprise|servlet|ws\.rs)\b/.test(source),
	apply: ({source}) => source
		.replace(/\bjavax\.annotation\b/g, "jakarta.annotation")
		.replace(/\bjavax\.inject\b/g, "jakarta.inject")
		.replace(/\bjavax\.enterprise\b/g, "jakarta.enterprise")
		.replace(/\bjavax\.servlet\b/g, "jakarta.servlet")
		.replace(/\bjavax\.ws\.rs\b/g, "jakarta.ws.rs")
};
