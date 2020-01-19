/* global NodeList, Clipboard, customElements */

class Checkable
{
	static get ContextMenu()
	{
		if (!Checkable._ContextMenu)
		{
			Checkable._ContextMenu = new ContextMenu();

			var inverterSelecao = Checkable._ContextMenu.appendChild(document.createElement("g-command"));
			inverterSelecao.action(e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked));
			inverterSelecao.innerHTML = "Inverter seleção<i>&#X2205;</i>";

			var selecionarTudo = Checkable._ContextMenu.appendChild(document.createElement("g-command"));
			selecionarTudo.action(e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = true));
			selecionarTudo.innerHTML = "Selecionar tudo<i>&#X1011;</i>";

			var desmarcarSelecionados = Checkable._ContextMenu.appendChild(document.createElement("g-command"));
			desmarcarSelecionados.action(e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = false));
			desmarcarSelecionados.innerHTML = "Desmarcar tudo<i>&#X1014;</i>";
		}

		return Checkable._ContextMenu;
	}
}

window.addEventListener("load", function ()
{
	Checkable.ContextMenu.register(document.querySelectorAll("ul.Checkable"));
});