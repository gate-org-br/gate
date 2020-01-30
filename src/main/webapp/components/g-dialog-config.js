/* global customElements, Overflow, Proxy, Commands, Dialog */

customElements.define('g-dialog-config', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.innerHTML = "<slot></slot>";
	}

	connectedCallback()
	{
		this.style.display = "none";
		if (window.frameElement && window.frameElement.dialog)
			window.addEventListener("load", () =>
			{
				let commands = new Commands();
				Array.from(this.children).forEach(e => commands.appendChild(Proxy.create(e)));
				commands.update();
				window.frameElement.dialog.commands = commands;
			});
	}

	static get observedAttributes()
	{
		return ['caption'];
	}

	attributeChangedCallback()
	{
		GDialog.caption = this.getAttribute("caption");
	}
});