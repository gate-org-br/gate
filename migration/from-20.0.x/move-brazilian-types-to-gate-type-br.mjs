export default {
	name: "Move Brazilian types to gate.type.br",
	description: "Moves Brazilian document and text value types from gate.type to gate.type.br.",
	filePredicate: ({file}) => /\.(?:java|html|jsp|js|mjs|wcc|wc|wcs)$/i.test(file),
	apply: ({source}) =>
		source.replace(/\bgate\.type\.(CEP|CPF|CNPJ|Phone|PortugueseName|BrasilianDocument)\b/g,
			(_, type) => `gate.type.br.${type}`)
};
