export default {
	name: "Replace conditional set attributes with has attributes",
	description: "Rewrites legacy conditional set:* attributes to has:* because set:* now assigns evaluated values.",
	filePredicate: ({file, source}) => /\.(?:html|xhtml|xml)$/i.test(file) && /\sset:[A-Za-z_][\w.-]*/.test(source),
	apply: ({source}) =>
		source.replace(/(\s)set:([A-Za-z_][\w.-]*)/g, "$1has:$2")
};
