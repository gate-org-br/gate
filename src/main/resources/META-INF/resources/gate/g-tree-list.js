let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-tree-list">ul[is='g-tree-list']
{
	margin: 0;
	padding: 4px;
	font-size: 16px;
	list-style-type: none;

	a {
		padding: 4px;
		font-size: inherit;
		align-items: center;
		display: inline-flex;
	}

	a:hover {
		color: blue;
	}

	ul
	{
		display: none;
		list-style-type: none;
	}

	li {
		cursor: pointer;
	}

	li::before
	{
		padding: 4px;
		content: '+';
		color: inherit;
		font-weight: bold;
		font-size: inherit;
		align-items: center;
		display: inline-flex;
		font-family: monospace;
	}

	li[data-expanded]::before {
		content: '-';
	}

	li[data-empty]::before {
		content: '\\25CF';
	}

	li[data-expanded]>ul {
		display: block;
	}
}</style>`;
/* global template */

const sheet = new CSSStyleSheet();
sheet.replaceSync(template.content.querySelector("style").textContent);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

customElements.define('g-tree-list', class extends HTMLUListElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		const root = this.getRootNode();
		if (root instanceof ShadowRoot)
			if (!root.adoptedStyleSheets.includes(sheet))
				root.adoptedStyleSheets = [...root.adoptedStyleSheets, sheet];

		Array.from(this.querySelectorAll("li")).forEach(li =>
		{
			if (li.querySelector("ul > li"))
			{
				li.addEventListener("click", event =>
				{
					if (event.target === li)
					{
						event.stopPropagation();
						if (li.hasAttribute('data-expanded'))
						{
							li.removeAttribute("data-expanded");
							Array.from(li.getElementsByTagName("li"))
								.forEach(e => e.removeAttribute("data-expanded"));
						} else
							li.setAttribute("data-expanded", "data-expanded");
					}
				});
			} else
			{
				li.setAttribute("data-empty", "data-empty");
				li.addEventListener("click", event => event.stopPropagation());
			}
		});
	}
}, {extends: 'ul'});
