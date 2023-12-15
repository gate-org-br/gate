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
	padding: 8px;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	color: var(--main);
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	background-color: var(--main4);
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
		this.style.display = "none";
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		this.addEventListener("click", event => event.stopPropagation());
		this.shadowRoot.getElementById("first").addEventListener("click", () => this.first());
		this.shadowRoot.getElementById("prev").addEventListener("click", () => this.previous());
		this.shadowRoot.getElementById("label").addEventListener("click", () => this.current());
		this.shadowRoot.getElementById("next").addEventListener("click", () => this.next());
		this.shadowRoot.getElementById("last").addEventListener("click", () => this.last());
	}

	first()
	{
		const detail = this.targets[0];
		if (this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.target = detail;
	}

	previous()
	{
		const detail = this.targets[this.index - 1];
		if (this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.target = detail;
	}

	current()
	{
		const detail = this.targets[this.index];
		if (this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.target = detail;
	}

	next()
	{
		const detail = this.targets[this.index + 1];
		if (this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.target = detail;
	}

	last()
	{
		const detail = this.targets[this.targets.length - 1];
		if (this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.target = detail;
	}

	get index()
	{
		return this._private.index;
	}

	get targets()
	{
		return this._private.targets || [];
	}

	set targets(targets)
	{
		this._private.index = 0;
		this._private.targets = targets;
		this.style.display = this.targets.length ? "" : "none";
		this.shadowRoot.getElementById("label").innerHTML = `${this.index + 1} de ${this.targets.length}`;
	}

	set target(value)
	{
		for (let index = 0; index < this.targets.length; index++)
		{
			if (value.endsWith(this.targets[index]))
			{
				this._private.index = index;
				this.shadowRoot.getElementById("first").setAttribute("navbar-disabled", String(index === 0));
				this.shadowRoot.getElementById("prev").setAttribute("navbar-disabled", String(index === 0));
				this.shadowRoot.getElementById("label").innerHTML = `${index + 1} de ${this.targets.length}`;
				this.shadowRoot.getElementById("next").setAttribute("navbar-disabled", String(index === this.targets.length - 1));
				this.shadowRoot.getElementById("last").setAttribute("navbar-disabled", String(index === this.targets.length - 1));
			}
		}
	}
});