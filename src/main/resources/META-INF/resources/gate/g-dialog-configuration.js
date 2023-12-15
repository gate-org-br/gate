let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) {
	display: none
}</style>`;

/* global customElements */

customElements.define('g-dialog-configuration', class extends HTMLElement
{
	get caption()
	{
		return this.getAttribute("caption") || "";
	}

	set caption(value)
	{
		if (value)
			this.setAttribute("caption", value);
		else
			this.removeAttribute("caption");
	}

	attributeChangedCallback()
	{
		if (window.frameElement)
		{
			let dialog = window.frameElement.dialog;
			if (dialog)
				dialog.caption = this.caption;
		}
	}

	static get observedAttributes()
	{
		return ['caption'];
	}

	connectedCallback()
	{
		if (window.frameElement)
		{
			let dialog = window.frameElement.dialog;
			if (dialog)
			{
				while (dialog.firstChild)
					dialog.firstChild.remove();
				Array.from(this.children).forEach(e => dialog.appendChild(e));
			}
		}
	}
});