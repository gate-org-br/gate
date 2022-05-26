let template = document.createElement("template");
template.innerHTML = `
	<a id='first' href='#'>&#x2277;</a>
	<a id='prev' >&#x2273;</a>
	<label id='label'>
	</label>
	<a id='next' >&#x2275;</a>
	<a id='last' href='#'>&#x2279;</a>
 <style>:host(*) {
	width: auto;
	padding: 4px;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	color: var(--g-navbar-color);
	border: 1px solid var(--g-navbar-border-color);
	background-color: var(--g-navbar-background-color);
	background-image: var(--g-navbar-background-image);
}

label {
	display: flex;
	color: inherit;
	cursor: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: center;
}

a {
	width: 20px;
	display: flex;
	color: inherit;
	cursor: inherit;
	font-size: inherit;
	font-family: 'gate';
	align-items: center;
	text-decoration: none;
	justify-content: center;
}

a[navbar-disabled="true"] {
	opacity: 0.2;
	cursor: not-allowed;
	pointer-events: none;
}</style>`;

/* global customElements */

customElements.define('g-navbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.index = 0;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("first").addEventListener("click", () => this.url = this.links[0]);
		this.shadowRoot.getElementById("prev").addEventListener("click", () => this.url = this.links[this._private.index - 1]);
		this.shadowRoot.getElementById("label").addEventListener("click", () => this.url = this.links[this._private.index]);
		this.shadowRoot.getElementById("next").addEventListener("click", () => this.url = this.links[this._private.index + 1]);
		this.shadowRoot.getElementById("last").addEventListener("click", () => this.url = this.links[this._private.links.length - 1]);
	}

	get index()
	{
		return this._private.index;
	}

	get links()
	{
		return this._private.links;
	}

	set links(links)
	{
		this._private.index = 0;
		this._private.links = links;
		this.shadowRoot.getElementById("label").innerHTML = `${this.index + 1} de ${this.links.length}`;
	}

	set url(url)
	{
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail: {navbar: this, target: url}})))
			return this;

		if (this._private.links)
		{
			this._private.index = Math.max(this._private.links.indexOf(url), 0);
			this.shadowRoot.getElementById("first").setAttribute("navbar-disabled", String(this.index === 0));
			this.shadowRoot.getElementById("prev").setAttribute("navbar-disabled", String(this.index === 0));
			this.shadowRoot.getElementById("label").innerHTML = `${this.index + 1} de ${this.links.length}`;
			this.shadowRoot.getElementById("next").setAttribute("navbar-disabled", String(this.index === this.links.length - 1));
			this.shadowRoot.getElementById("last").setAttribute("navbar-disabled", String(this.index === this.links.length - 1));
		}
	}
});