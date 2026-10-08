export default {
	name: "Replace BearerAuthorization constructor reference",
	description: "Rewrites BearerAuthorization::new to BearerAuthorization::from when the file uses BearerAuthorization.",
	filePredicate: ({file, source}) => /\.java$/i.test(file) && source.includes("BearerAuthorization"),
	apply: ({source}) => source.replace(/\bBearerAuthorization::new\b/g, "BearerAuthorization::from")
};
