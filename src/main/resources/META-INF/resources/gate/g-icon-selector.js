let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-icon-selector">:host(*)
{
	width: 100%;
	margin: 8px;
	display: flex;
	overflow: auto;
	flex-wrap: wrap;
	align-items: flex-start;
	justify-content: flex-start;
}

a {
	width: 64px;
	speak: none;
	margin: 8px;
	height: 64px;
	display: flex;
	font-size: 28px;
	font-style: normal;
	font-weight: normal;
	align-items: center;
	font-family: 'gate';
	font-variant: normal;
	text-transform: none;
	text-decoration: none;
	justify-content: center;
	background-color: white;
}

a:hover {
	background-color: var(--hovered)
}</style>`;
/* global customElements */

import icons from './icon-list.js';

customElements.define('g-icon-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		icons.forEach(icon =>
		{
			let link = document.createElement("a");
			link.href = "#";
			link.innerHTML = `&#X${icon};`;
			link.addEventListener("click", event =>
			{
				event.preventDefault();
				this.dispatchEvent(new CustomEvent('selected',
					{detail: {selector: this, icon: icon}}));
			});
			this.shadowRoot.appendChild(link);
		});
	}
});