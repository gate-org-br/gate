export default {
	name: "Replace Link constructor with Link.of",
	description: "Rewrites new Link() and new Link(...) to Link.of() and Link.of(...) when the file clearly uses gate.sql.Link.",
	filePredicate: ({file, source}) => /\.java$/i.test(file) && source.includes("gate.sql.Link"),
	apply: ({source}) => source.replace(/\bnew\s+Link\s*\(([^()]*)\)/g, "Link.of($1)")
};
