
/* global customElements, GOverflow, WindowList */

class Modal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.classList.add("g-modal");

		this.addEventListener("contextmenu", event =>
		{
			event.preventDefault();
			this.hide();
		});

		this.addEventListener("click", event => !this.blocked
				&& (event.target === this || event.srcElement === this)
				&& this.hide());
	}

	set creator(creator)
	{
		this._private.creator = creator;
	}

	get blocked()
	{
		return this._private.blocked || false;
	}

	set blocked(blocked)
	{
		this._private.blocked = blocked;
	}

	get creator()
	{
		return this._private.creator || this;
	}

	show()
	{
		if (this.creator.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
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
			&& this.creator.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
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

customElements.define('g-modal', Modal);