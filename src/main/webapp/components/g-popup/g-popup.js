/* global customElements */

class GPopup extends GWindow
{
	constructor()
	{
		super();
		this.hideButton;
		this.caption = "Erro de sistema";
	}

	set element(element)
	{
		if (this.body.firstChild)
			this.body.removeChild(this.body.firstChild);
		this.body.appendChild(element);
	}

	get element()
	{
		return this.main.firstChild();
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-popup");
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('template[data-popup]')).forEach(element =>
	{
		var popup = window.top.document.createElement("g-popup");
		popup.element = element.content.cloneNode(true);
		popup.show();
	});
});


customElements.define('g-popup', GPopup);