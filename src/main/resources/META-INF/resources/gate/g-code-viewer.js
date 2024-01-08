let template = document.createElement("template");
template.innerHTML = `
	<link rel="stylesheet"
	      href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/idea.css"/>
	<div id="html-pane">
		<slot></slot>
	</div>
	<div id="code-pane">
                <pre><code></code></pre>
	</div>
 <style>* {
	box-sizing: border-box;
}

:host(*) {
	width: 100%;
	display: grid;
	font-size: 10px;
	border: 1px solid #EEEEEE;
	grid-template-rows: auto auto;
}

#code-pane {
	padding: 10px;
	border-bottom: 1px solid #ccc;
	background-color: var(--hovered);
}

#code-pane * {
	font-size: inherit;
	background-color: var(--hovered);
}

#html-pane {
	gap: 10px;
	padding: 10px;
	display: flex;
	flex-direction: column;
	background-color: var(--main2);
}</style>`;

function highlightHTML(htmlString)
{
	const tag = 'style="color: #000066"';
	const key = 'style="color: #006600"';
	const val = 'style="color: #884444"';
	const selfClosing = ["AREA", "BASE", "BR", "COL", "COMMAND", "EMBED", "HR", "IMG", "INPUT",
		"KEYGEN", "LINK", "META", "PARAM", "SOURCE", "TRACK", "WBR"];


	let highlightedString = "";
	const parser = new DOMParser();
	const doc = parser.parseFromString(htmlString, "text/html");

	function highlightNodes(node, depth)
	{
		const indent = `\n${"\t".repeat(depth)}`;
		if (node.nodeType === Node.ELEMENT_NODE)
		{
			highlightedString += `${indent}<b ${tag}>&lt;${node.tagName.toLowerCase()}</b>`;
			if (node.hasAttributes())
			{
				let size = Array.from(node.attributes).map(attr => attr.name.length + attr.value.length)
					.reduce((a, b) => a + b);
				Array.from(node.attributes).map((attr, index) =>
					highlightedString += `${size > 120 && index ? indent + '\t\t' : ' '}<b ${key}>${attr.name}</b>`
						+ (attr.value ? `="<b ${val}>${attr.value}</b>"` : ""));
			}
			highlightedString += `<b ${tag}>&gt;</b>`;

			if (!selfClosing.includes(node.tagName))
			{
				Array.from(node.childNodes).forEach(e => highlightNodes(e, depth + 1));
				highlightedString += `${indent}<b ${tag}>&lt;/${node.tagName.toLowerCase()}&gt;</b>`;
			}
		} else if (node.nodeType === Node.TEXT_NODE
			&& node.textContent.trim().length)
			highlightedString += `${indent}${node.textContent.trim()}`;
	}

	highlightNodes(doc.body, 0);

	return highlightedString;
}

customElements.define('g-code-viewer', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		new MutationObserver(() => this.connectedCallback()).observe(this,
			{attributes: true, childList: true, subtree: true});
	}

	get code()
	{
		return this.shadowRoot.querySelector('code');
	}

	connectedCallback()
	{
		let text = this.innerHTML;
		if (this.hasAttribute("ignore"))
			text = text.replace(new RegExp(this.getAttribute("ignore"), 'gi'), "");
		this.code.innerHTML = highlightHTML(text);
	}
});
