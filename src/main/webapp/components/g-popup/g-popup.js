/* global customElements */

class GPopup extends GWindow
{
	constructor()
	{
		super();
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

		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-popup");

		this.caption = "Erro de sistema";

		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011;<i/>";
		this.head.appendChild(close);
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