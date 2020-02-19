/* global Message, Block, ENTER, ESC, GDialog, GStackFrame */

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;
	let link = event.target;
	if (link.tagName !== 'A')
		return;
	if (link.hasAttribute("data-cancel"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		Message.error(link.getAttribute("data-cancel"), 2000);
		return;
	}

	if (link.hasAttribute("data-disabled"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-confirm")
		&& !confirm(link.getAttribute("data-confirm")))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-alert"))
		alert(link.getAttribute("data-alert"));
	else if (link.href.match(/([@][{][^}]*[}])/g)
		|| link.href.match(/([!][{][^}]*[}])/g)
		|| link.href.match(/([?][{][^}]*[}])/g))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		var resolved = resolve(link.href);
		if (resolved !== null)
		{
			var href = link.href;
			link.href = resolved;
			link.click();
			link.href = href;
		}
		return;
	}

	if (link.getAttribute("target"))
	{
		switch (link.getAttribute("target").toLowerCase())
		{
			case "_dialog":
				event.preventDefault();
				event.stopPropagation();
				if (event.ctrlKey)
				{
					link.setAttribute("target", "_blank");
					link.click();
					link.setAttribute("target", "_dialog");
				} else
				{
					let dialog = GDialog.create();
					dialog.navigator = link.navigator;
					dialog.target = link.getAttribute("href");
					dialog.caption = link.getAttribute("title");
					dialog.blocked = Boolean(link.getAttribute("data-blocked"));


					if (link.hasAttribute("data-reload-on-hide"))
						dialog.addEventListener("hide", () => window.location = window.location.href);
					else if (link.hasAttribute("data-submit-on-hide"))
						dialog.addEventListener("hide", () => document.getElementById(link.getAttribute("data-submit-on-hide")).submit());

					dialog.show();
				}
				break;
			case "_stack":
				event.preventDefault();
				event.stopPropagation();
				if (event.ctrlKey)
				{
					link.setAttribute("target", "_blank");
					link.click();
					link.setAttribute("target", "_dialog");
				} else
				{
					let stackFrame = GStackFrame.create();
					stackFrame.target = link.getAttribute("href");

					if (link.hasAttribute("data-reload-on-hide"))
						stackFrame.addEventListener("hide", () => window.location = window.location.href);
					else if (link.hasAttribute("data-submit-on-hide"))
						stackFrame.addEventListener("hide", () => document.getElementById(link.getAttribute("data-submit-on-hide")).submit());

					stackFrame.show();
				}
				break;
			case "_message":
				event.preventDefault();
				event.stopPropagation();
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						Message.show(status, 2000);
					} finally
					{
						link.removeAttribute("data-cancel");
					}
				});
				break;
			case "_none":
				event.preventDefault();
				event.stopPropagation();
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type !== "SUCCESS")
							Message.show(status, 2000);
					} finally
					{
						link.removeAttribute("data-cancel");
					}
				});
				break;
			case "_this":
				event.preventDefault();
				event.stopPropagation();
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type === "SUCCESS")
							link.innerHTML = status.value;
						else
							Message.show(status, 2000);
					} finally
					{
						link.removeAttribute("data-cancel");
					}
				});
				break;
			case "_alert":
				event.preventDefault();
				event.stopPropagation();
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					alert(status);
					link.removeAttribute("data-cancel");
				});
				break;
			case "_hide":
				event.preventDefault();
				event.stopPropagation();
				if (window.frameElement
					&& window.frameElement.dialog
					&& window.frameElement.dialog.hide)
					window.frameElement.dialog.hide();
				else
					window.close();
				break;
			case "_popup":
				event.preventDefault();
				event.stopPropagation();
				Array.from(link.children)
					.filter(e => e.tagName.toLowerCase() === "div")
					.forEach(e => new GPopup(e));
				break;
			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(process =>
				{
					process = JSON.parse(process);
					new GProgressDialog(process,
						{title: link.getAttribute("title")}).show();
				});
				break;
			case "_progress-window":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(function (process)
				{
					process = JSON.parse(process);
					document.body.appendChild(new GProgressWindow(process));
				});
				break;
			case "_report":
			case "_report-dialog":
				event.preventDefault();
				event.stopPropagation();
				let dialog = new GReportDialog();
				dialog.blocked = true;
				dialog.caption = link.getAttribute("title") || "Imprimir";
				dialog.get(link.href);
				break;
		}
	}
});
window.addEventListener("keydown", function (event)
{
	let link = event.target;
	if (link.tagName === 'A' && event.keyCode === 32)
	{
		event.target.click();
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
});