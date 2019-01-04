class Modal
{
	constructor(options)
	{
		var element = window.top.document.createElement('div');
		element.className = "Modal";
		this.element = () => element;

		var blocked = options ? options.blocked : null;
		this.blocked = () => blocked;

		var creator = options ? options.creator : null;
		this.creator = () => creator ? creator : element;

		if (!blocked)
			element.addEventListener("click", event =>
				(event.target === element || event.srcElement === element) && this.hide());
	}

	show()
	{
		if (this.creator().dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			window.top.document.body.style.overflow = "hidden";
			window.top.document.body.appendChild(this.element());
			this.element().dispatchEvent(new CustomEvent('show', {detail: {modal: this}}));
		}

		return this;
	}

	hide()
	{
		if (this.element().parentNode
			&& this.creator().dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
		{
			window.top.document.body.style.overflow = "";
			this.element().parentNode.removeChild(this.element());
		}

		return this;
	}
}