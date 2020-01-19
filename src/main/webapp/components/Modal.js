
/* global customElements, Overflow */

class Modal extends HTMLElement
{
	constructor(options = {})
	{
		super();
		this._private = {};
		this.classList.add("g-modal");
		this._private.options = options;
		this._private.preventBodyScroll = e => e.preventDefault();

		this.addEventListener("contextmenu", event =>
		{
			event.preventDefault();
			this.hide();
		});

		if (!this.blocked)
			this.addEventListener("click", event =>
				(event.target === this || event.srcElement === this)
					&& this.hide());
	}

	get blocked()
	{
		return this._private.options.blocked;
	}

	get creator()
	{
		return this._private.options.creator || this;
	}

	show()
	{
		if (this.creator.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			Overflow.disable(window.top.document.documentElement);

			window.top.document.documentElement.addEventListener("touchmove", this._private.preventBodyScroll, false);

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
			Overflow.enable(window.top.document.documentElement);
			window.top.document.documentElement.removeEventListener("touchmove", this._private.preventBodyScroll, false);
			this.parentNode.removeChild(this);
		}

		return this;
	}
}

customElements.define('g-modal', Modal);