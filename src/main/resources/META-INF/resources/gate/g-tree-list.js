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
		content: ' ';
	}

	li[data-expanded]>ul {
		display: block;
	}
}</style>`;
/* global template */
import applyTemplate from './apply-template.js';

applyTemplate(document, template);
customElements.define('g-tree-list', class extends HTMLUListElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		applyTemplate(this, template);

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
