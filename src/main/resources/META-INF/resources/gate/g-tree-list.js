let template = document.createElement("template");
template.innerHTML = `
 <style>ul[is='g-tree-list']
{
	list-style-type: none;

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
		content: '+';
		font-size: 12px;
		font-weight: bold;
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

document.head.insertAdjacentHTML('beforeend', template.innerHTML);

customElements.define('g-tree-list', class extends HTMLUListElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
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
