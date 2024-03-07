let template = document.createElement("template");
template.innerHTML = `
	<a id='first' href='#'>&#x2277;</a>
	<a id='prev' >&#x2273;</a>
	<label id='label'>
	</label>
	<a id='next' >&#x2275;</a>
	<a id='last' href='#'>&#x2279;</a>
 <style data-element="g-navbar">:host(*) {
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
	#index;
	#targets;

	constructor()
	{
		super();
		this.#index = -1;
		this.#targets = null;
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
		let rollback = this.index;
		this.index = 0;
		const detail = {target: this.target, index: this.index};
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.index = rollback;
	}

	previous()
	{
		let rollback = this.index;
		this.index = this.index - 1;
		const detail = {target: this.target, index: this.index};
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.index = rollback;
	}

	current()
	{
		const detail = {target: this.target, index: this.index};
		this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail}));
	}

	next()
	{
		let rollback = this.index;
		this.index = this.index + 1;
		const detail = {target: this.target, index: this.index};
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.index = rollback;
	}

	last()
	{
		let rollback = this.index;
		this.index = this.targets.length - 1;
		const detail = {target: this.target, index: this.index};
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail})))
			this.index = rollback;
	}

	get index()
	{
		return this.#index;
	}

	set index(value)
	{
		if (!this.targets || value >= this.targets.length)
			throw new Error("Invalid index");

		this.#index = value;
		this.shadowRoot.getElementById("first").setAttribute("navbar-disabled", String(value === 0));
		this.shadowRoot.getElementById("prev").setAttribute("navbar-disabled", String(value === 0));
		this.shadowRoot.getElementById("label").innerHTML = `${value + 1} de ${this.targets.length}`;
		this.shadowRoot.getElementById("next").setAttribute("navbar-disabled", String(value === this.targets.length - 1));
		this.shadowRoot.getElementById("last").setAttribute("navbar-disabled", String(value === this.targets.length - 1));
	}

	get targets()
	{
		return this.#targets;
	}

	set targets(value)
	{
		if (!Array.isArray(value))
			throw new Error("Invalid targets");
		this.#targets = value;

		this.#index = value.length ? 0 : null;
		this.style.display = this.#targets.length ? "" : "none";
		this.shadowRoot.getElementById("label").innerHTML = `${this.#index + 1} de ${this.#targets.length}`;
	}

	get target()
	{
		return this.targets ? this.targets[this.index] : null;
	}

});