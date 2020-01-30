/* global CopyTextMenuItem, CopyLinkMenuItem, OpenLinkMenuItem, Clipboard */

class ActionContextMenu
{
	static get instance()
	{
		if (!ActionContextMenu._instance)
		{
			ActionContextMenu._instance = new ContextMenu();
			ActionContextMenu._instance.appendChild(ActionContextMenu.CopyTextAction);
			ActionContextMenu._instance.appendChild(ActionContextMenu.CopyLinkAction);
			ActionContextMenu._instance.appendChild(ActionContextMenu.OpenLinkAction);
		}
		return ActionContextMenu._instance;
	}

	static get CopyLinkAction()
	{
		if (!ActionContextMenu._CopyLinkAction)
		{
			ActionContextMenu._CopyLinkAction = new Command();
			ActionContextMenu._CopyLinkAction.innerHTML = "Copiar endereço <i>&#X2159;</i>";
			ActionContextMenu._CopyLinkAction.action = e => Clipboard.copy(e.parentNode.context.getAttribute("data-action"), true);
		}
		return ActionContextMenu._CopyLinkAction;
	}

	static get CopyTextAction()
	{
		if (!ActionContextMenu._CopyTextAction)
		{
			ActionContextMenu._CopyTextAction = new Command();
			ActionContextMenu._CopyTextAction.innerHTML = "Copiar texto <i>&#X2217;</i>";
			ActionContextMenu._CopyTextAction.action = e => Clipboard.copy(e.parentNode.target.innerText, true);
		}
		return ActionContextMenu._CopyTextAction;
	}

	static get OpenLinkAction()
	{
		if (!ActionContextMenu._OpenLinkAction)
		{
			ActionContextMenu._OpenLinkAction = new Command();
			ActionContextMenu._OpenLinkAction.innerHTML = "Abrir em nova aba<i>&#X2256;</i>";

			ActionContextMenu._OpenLinkAction.action = e =>
			{
				var context = e.parentNode.context;
				switch (context.getAttribute("data-method")
					? context.getAttribute("data-method").toLowerCase() : "get")
				{
					case "get":
						new Link(document.createElement("a"), context)
							.setTarget("_blank")
							.setAction(context.getAttribute("data-action"))
							.setTitle(context.getAttribute("title"))
							.setBlock(context.getAttribute("data-block"))
							.setAlert(context.getAttribute("data-alert"))
							.setConfirm(context.getAttribute("data-confirm"))
							.execute();
						break;
					case "post":
						new Button(document.createElement("button"), context)
							.setTarget("_blank")
							.setAction(context.getAttribute("data-action"))
							.setTitle(context.getAttribute("title"))
							.setBlock(context.getAttribute("data-block"))
							.setAlert(context.getAttribute("data-alert"))
							.setConfirm(context.getAttribute("data-confirm"))
							.execute();
						break;
				}
			};

		}
		return ActionContextMenu._OpenLinkAction;
	}
}

