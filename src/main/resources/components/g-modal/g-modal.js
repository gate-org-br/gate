
/* global customElements, GOverflow, WindowList */

class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		this.addEventListener("contextmenu", event =>
		{
			event.preventDefault();
			this.hide();
		});

		this.addEventListener("keypress", event => event.stopPropagation());
		this.addEventListener("keydown", event => event.stopPropagation());

		this.addEventListener("click", event => !this.blocked
				&& (event.target === this || event.srcElement === this)
				&& this.hide());
	}

	connectedCallback()
	{
		this.classList.add("g-modal");
	}

	get blocked()
	{
		return this._private.blocked || false;
	}

	set blocked(blocked)
	{
		this._private.blocked = blocked;
	}

	show()
	{

		if (window.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			GOverflow.disable(window.top.document.documentElement);

			window.top.document.documentElement.appendChild(this);

			this.dispatchEvent(new CustomEvent('show', {detail: {modal: this}}));
		}

		return this;
	}

	hide()
	{
		if (this.parentNode
			&& window.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}}))
			&& this.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
		{
			GOverflow.enable(window.top.document.documentElement);
			this.parentNode.removeChild(this);
		}

		return this;
	}

	minimize()
	{
		if (!this.parentNode)
			return;

		this.style.display = "none";
		GOverflow.enable(window.top.document.documentElement);

		var link = WindowList.instance.appendChild(document.createElement("a"));
		link.href = "#";
		link.innerHTML = this.caption || "Janela";

		link.addEventListener("click", () =>
		{
			this.style.display = "";
			link.parentNode.removeChild(link);
			GOverflow.disable(window.top.document.documentElement);
		});
	}
}

customElements.define('g-modal', GModal);