
/* global customElements */

class GCheckableContextMenu extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();

		let inverterSelecao = this.appendChild(document.createElement("a"));
		inverterSelecao.innerHTML = "Inverter seleção<i>&#X2205;</i>";
		inverterSelecao.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked));

		let selecionarTudo = this.appendChild(document.createElement("a"));
		selecionarTudo.innerHTML = "Selecionar tudo<i>&#X1011;</i>";
		selecionarTudo.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = true));

		let desmarcarSelecionados = this.appendChild(document.createElement("a"));
		desmarcarSelecionados.innerHTML = "Desmarcar tudo<i>&#X1014;</i>";
		desmarcarSelecionados.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = false));
	}
}

customElements.define('g-checkable-context-menu', GCheckableContextMenu);


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("ul.Checkable");
	if (!action)
		return;

	let menu = document.querySelector("g-checkable-context-menu")
		|| document.body.appendChild(new GCheckableContextMenu());
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});