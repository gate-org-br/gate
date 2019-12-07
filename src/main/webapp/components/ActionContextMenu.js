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
			ActionContextMenu._CopyLinkAction = new ContextMenuItem("\u2159", "Copiar endereço",
				e => Clipboard.copy(e.context.getAttribute("data-action"), true));
		return ActionContextMenu._CopyLinkAction;
	}

	static get CopyTextAction()
	{
		if (!ActionContextMenu._CopyTextAction)
			ActionContextMenu._CopyTextAction = new ContextMenuItem("\u2256", "Copiar texto",
				e => Clipboard.copy(e.element.innerText, true));
		return ActionContextMenu._CopyTextAction;
	}

	static get OpenLinkAction()
	{
		if (!ActionContextMenu._OpenLinkAction)
			ActionContextMenu._OpenLinkAction = new ContextMenuItem("\u2217", "Abrir em nova aba",
				e => {
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
		return ActionContextMenu._OpenLinkAction;
	}
}

