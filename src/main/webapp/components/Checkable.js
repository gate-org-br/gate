/* global NodeList, Clipboard, customElements */

class Checkable
{
	static get ContextMenu()
	{
		if (!Checkable._ContextMenu)
		{
			var selecionarTudo = new ContextMenuItem(e =>
				Array.from(e.context.getElementsByTagName("input"))
					.forEach(input => input.checked = true));
			selecionarTudo.icon = "\u1011";
			selecionarTudo.name = "Selecionar tudo";

			var desmarcarSelecionados = new ContextMenuItem(e =>
				Array.from(e.context.getElementsByTagName("input"))
					.forEach(input => input.checked = false));
			desmarcarSelecionados.icon = "\u1014";
			desmarcarSelecionados.name = "Desmarcar selecionados";

			Checkable._ContextMenu = new ContextMenu(selecionarTudo, desmarcarSelecionados);
		}

		return Checkable._ContextMenu;
	}
}

window.addEventListener("load", function ()
{
	Checkable.ContextMenu.register(document.querySelectorAll("ul.Checkable"));
});