/* global customElements, GOverflow, Proxy, GCommands, Dialog */

customElements.define('g-dialog-header', class extends HTMLElement
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
				let commands = window.top.document.createElement("g-commands");
				Array.from(this.children).forEach(e => commands.appendChild(Proxy.create(e)));
				let more = commands.appendChild(window.top.document.createElement("g-more"));
				more.innerHTML = "<i>&#X3018;</i>"
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