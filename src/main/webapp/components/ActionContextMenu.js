/* global CopyTextMenuItem, CopyLinkMenuItem, OpenLinkMenuItem, Clipboard */

class ActionContextMenu
{
	static get instance()
	{
		if (!ActionContextMenu._instance)
			ActionContextMenu._instance
				= new ContextMenu(ActionContextMenu.CopyTextAction,
					ActionContextMenu.CopyLinkAction,
					ActionContextMenu.OpenLinkAction);
		return ActionContextMenu._instance;
	}

	static get CopyLinkAction()
	{
		if (!ActionContextMenu._CopyLinkAction)
		{
			ActionContextMenu._CopyLinkAction = new ContextMenuItem(e => Clipboard.copy(e.context.getAttribute("data-action"), true));
			ActionContextMenu._CopyLinkAction.icon = "\u2159";
			ActionContextMenu._CopyLinkAction.name = "Copiar endereço";
		}
		return ActionContextMenu._CopyLinkAction;
	}

	static get CopyTextAction()
	{
		if (!ActionContextMenu._CopyTextAction)
		{
			ActionContextMenu._CopyTextAction = new ContextMenuItem(e => Clipboard.copy(e.element.innerText, true));
			ActionContextMenu._CopyTextAction.icon = "\u2256";
			ActionContextMenu._CopyTextAction.name = "Copiar texto";
		}
		return ActionContextMenu._CopyTextAction;
	}

	static get OpenLinkAction()
	{
		if (!ActionContextMenu._OpenLinkAction)
		{
			ActionContextMenu._OpenLinkAction = new ContextMenuItem(e => {
				switch (e.context.getAttribute("data-method")
					? e.context.getAttribute("data-method").toLowerCase() : "get")
				{
					case "get":
						new Link(document.createElement("a"), e.context)
							.setTarget("_blank")
							.setAction(e.context.getAttribute("data-action"))
							.setTitle(e.context.getAttribute("title"))
							.setBlock(e.context.getAttribute("data-block"))
							.setAlert(e.context.getAttribute("data-alert"))
							.setConfirm(e.context.getAttribute("data-confirm"))
							.execute();
						break;
					case "post":
						new Button(document.createElement("button"), e.context)
							.setTarget("_blank")
							.setAction(e.context.getAttribute("data-action"))
							.setTitle(e.context.getAttribute("title"))
							.setBlock(e.context.getAttribute("data-block"))
							.setAlert(e.context.getAttribute("data-alert"))
							.setConfirm(e.context.getAttribute("data-confirm"))
							.execute();
						break;
				}
			});

			ActionContextMenu._OpenLinkAction.icon = "\u2217";
			ActionContextMenu._OpenLinkAction.name = "Abrir em nova aba";
		}
		return ActionContextMenu._OpenLinkAction;
	}
}

