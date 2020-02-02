/* global NodeList, Clipboard, customElements */

class Checkable
{
	static get ContextMenu()
	{
		if (!Checkable._ContextMenu)
		{
			Checkable._ContextMenu = new ContextMenu();

			var inverterSelecao = new GCommand();
			Checkable._ContextMenu.appendChild(inverterSelecao);
			inverterSelecao.innerHTML = "Inverter seleção<i>&#X2205;</i>";
			inverterSelecao.action = e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked);


			var selecionarTudo = new GCommand();
			Checkable._ContextMenu.appendChild(selecionarTudo);
			selecionarTudo.innerHTML = "Selecionar tudo<i>&#X1011;</i>";
			selecionarTudo.action = e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = true);

			var desmarcarSelecionados = new GCommand();
			Checkable._ContextMenu.appendChild(desmarcarSelecionados);
			desmarcarSelecionados.innerHTML = "Desmarcar tudo<i>&#X1014;</i>";
			desmarcarSelecionados.action = e => Array.from(e.parentNode.context.getElementsByTagName("input")).forEach(input => input.checked = false);

		}

		return Checkable._ContextMenu;
	}
}

window.addEventListener("load", function ()
{
	Checkable.ContextMenu.register(document.querySelectorAll("ul.Checkable"));
});