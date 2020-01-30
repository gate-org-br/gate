/* global customElements */

class Popup extends Window
{
	constructor(element)
	{
		super();
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-popup");

		this.caption = "Erro de sistema";
		let close = new Command();

		this.commands.add(close);
		close.action = () => this.hide();
		close.innerHTML = "Fechar janela<i>&#x1011;<i/>";

		this.body.appendChild(element);
	}
}

window.addEventListener("load", () => Array.from(document.querySelectorAll('template[data-popup]'))
		.forEach(element => new Popup(element.content.cloneNode(true)).show()));


customElements.define('g-popup', Popup);