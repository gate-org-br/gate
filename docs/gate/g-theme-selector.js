let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
<style data-element="g-theme-selector">* {
	box-sizing: border-box;

}

:host(*) {
	gap: 12px;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 16px;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}

:host::before {
	content: '\\2140';
	font-family: 'gate';
}

:host([value='light'])::before {
	content: '\\2203';
}

:host([value='dark'])::before {
	content: '\\2164';
}</style>`;
import GContextMenu from "./g-context-menu.js";

/* global customElements, template */
customElements.define('g-theme-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", event =>
		{
			event.preventDefault();
			GContextMenu.show(this, this,
				{
					text: "Light", icon: "2203", action: () => this.value = "light"
				},
				{
					text: "Dark", icon: "2164", action: () => this.value = "dark"
				},
				{
					text: "System", icon: "2140", action: () => this.value = "system"
				});
		});
	}

	get value()
	{
		return this.getAttribute("value") || "false";
	}

	set value(value)
	{
		this.setAttribute("value", value);
		localStorage.setItem('theme', value);
	}

	connectedCallback()
	{
		this.setAttribute("title", "Change theme");
		this.value = localStorage.getItem('theme') || "false";
	}
});