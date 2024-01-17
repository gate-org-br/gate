let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) {
	display: none
}</style>`;

/* global customElements */

customElements.define('g-dialog-header', class extends HTMLElement
{
	get caption()
	{
		return this.dialog.caption;
	}

	set caption(value)
	{
		if (value)
			this.setAttribute("caption", value);
		else
			this.removeAttribute("caption");
	}

	get dialog()
	{
		for (let parent = this; parent;
			parent = parent.parentNode || parent.host || window.frameElement)
			if (parent.tagName === "G-DIALOG")
				return parent;
	}

	get toolbar()
	{
		return this.dialog?.toolbar;
	}

	attributeChangedCallback()
	{
		let dialog = this.dialog;
		if (dialog)
			dialog.caption = this.getAttribute("caption");
	}

	static get observedAttributes()
	{
		return ['caption'];
	}

	connectedCallback()
	{
		let toolbar = this.toolbar;
		if (toolbar)
		{
			while (toolbar.firstChild)
				toolbar.firstChild.remove();
			Array.from(this.children).forEach(e => toolbar.appendChild(e));
		}
	}
});