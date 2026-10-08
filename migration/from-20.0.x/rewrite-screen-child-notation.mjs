export default {
	name: "Rewrite screen child notation",
	description: "Rewrites legacy screen child references from ParentScreen$Child to Parent.Child in HTML files.",
	filePredicate: ({file}) => /\.html$/i.test(file),
	apply: ({source}) =>
		source.replace(/(\b(?:g:screen|screen|SCREEN)\s*=\s*["'])([A-Za-z_][A-Za-z0-9_]*?)(Screen)?\$([A-Za-z_][A-Za-z0-9_]*)(["'])/g,
			(_, start, parent, _screenSuffix, child, end) => `${start}${parent}.${child}${end}`)
};
