/* global customElements */

class GPopup extends GWindow
{
	constructor(element)
	{
		super();
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-popup");

		this.caption = "Erro de sistema";

		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011;<i/>";
		this.head.appendChild(close);

		this.body.appendChild(element);
	}

	connectedCallback()
	{
		super.connectedCallback();
	}
}

window.addEventListener("load",
	() => Array.from(document.querySelectorAll('template[data-popup]'))
		.forEach(element => new GPopup(element.content.cloneNode(true)).show()));


customElements.define('g-popup', GPopup);