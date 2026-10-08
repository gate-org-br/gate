function resolveStyleSheetImports(sheet, visited = new Set())
{
	const key = sheet.href || sheet.ownerNode?.textContent;
	if (visited.has(key))
		return '';
	visited.add(key);

	let css = '';
	try {
		for (const rule of sheet.cssRules)
			if (rule instanceof CSSImportRule)
				css += resolveStyleSheetImports(rule.styleSheet, visited);
			else
				css += rule.cssText + '\n';
	} catch (e) {
	}

	return css;
}

let css = '';
for (const sheet of document.styleSheets)
	css += resolveStyleSheetImports(sheet);
const globalStyles = new CSSStyleSheet();
globalStyles.replaceSync(css);


export default class StyledHTMLElement extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.adoptedStyleSheets = [globalStyles];
	}
}