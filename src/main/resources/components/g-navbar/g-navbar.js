/* global customElements */

class GNavBar extends HTMLElement
{
	constructor(links, url)
	{
		super();
		this._private = {};
		this._private.index = 0;
		this._private.links = links;

		this._private.text = document.createElement("label");
		this._private.text.addEventListener("click", () => this.update(this._private.links[this._private.index]));

		this._private.frst = document.createElement("a");
		this._private.frst.innerHTML = "&#x2277;";
		this._private.frst.setAttribute("href", "#");
		this._private.frst.addEventListener("click", () => this.update(this._private.links[0]));

		this._private.prev = document.createElement("a");
		this._private.prev.innerHTML = "&#x2273;";
		this._private.prev.setAttribute("href", "#");
		this._private.prev.addEventListener("click", () => this.update(this._private.links[this._private.index - 1]));

		this._private.next = document.createElement("a");
		this._private.next.innerHTML = "&#x2275;";
		this._private.next.setAttribute("href", "#");
		this._private.next.addEventListener("click", () => this.update(this._private.links[this._private.index + 1]));
		this._private.last = document.createElement("a");

		this._private.last.innerHTML = "&#x2279;";
		this._private.last.setAttribute("href", "#");
		this._private.last.addEventListener("click", () => this.update(this._private.links[this._private.links.length - 1]));

		this.update(url);
	}

	connectedCallback()
	{
		this.appendChild(this._private.frst);
		this.appendChild(this._private.prev);
		this.appendChild(this._private.text);
		this.appendChild(this._private.next);
		this.appendChild(this._private.last);
	}

	disconnectedCallback()
	{
		this.removeChild(this._private.frst);
		this.removeChild(this._private.prev);
		this.removeChild(this._private.text);
		this.removeChild(this._private.next);
		this.removeChild(this._private.last);
	}

	update(url)
	{
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail: {navbar: this, target: url}})))
			return this;

		this._private.index = Math.max(this._private.links.indexOf(url), 0);
		this._private.frst.setAttribute("navbar-disabled", String(this._private.index === 0));
		this._private.prev.setAttribute("navbar-disabled", String(this._private.index === 0));
		this._private.text.innerHTML = "" + (this._private.index + 1) + " de " + this._private.links.length;
		this._private.next.setAttribute("navbar-disabled", String(this._private.index === this._private.links.length - 1));
		this._private.last.setAttribute("navbar-disabled", String(this._private.index === this._private.links.length - 1));
		return this;
	}
}

customElements.define('g-navbar', GNavBar);