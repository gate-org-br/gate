/* global Message, Block, ENTER, ESC, GDialog, GStackFrame */

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;
	let link = event.target
		.closest("a");
	if (!link)
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

	let target = link.getAttribute("target");
	if (link.getAttribute("target"))
	{
		target = target.toLowerCase();
		switch (target)
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

					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

					if (link.getAttribute("data-on-hide"))
						if (link.getAttribute("data-on-hide") === "reload")
							dialog.addEventListener("hide", () => window.location = window.location.href);
						else if (link.getAttribute("data-on-hide") === "submit")
							dialog.addEventListener("hide", () => link.closest("form").submit());
						else if (link.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
							dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
									.exec(link.getAttribute("data-on-hide"))[1]).submit());

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
					let dialog = GStackFrame.create();
					dialog.target = link.getAttribute("href");

					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

					if (link.getAttribute("data-on-hide"))
						if (link.getAttribute("data-on-hide") === "reload")
							dialog.addEventListener("hide", () => window.location = window.location.href);
						else if (link.getAttribute("data-on-hide") === "submit")
							dialog.addEventListener("hide", () => link.closest("form").submit());
						else if (link.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
							dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
									.exec(link.getAttribute("data-on-hide"))[1]).submit());

					dialog.show();
				}
				break;
			case "_message":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						Message.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
			case "_none":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type !== "SUCCESS")
							Message.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;
			case "_this":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type === "SUCCESS")
							link.innerHTML = status.message;
						else
							Message.show(status, 2000);
					} finally
					{
						link.style.pointerEvents = "";
					}
				});
				break;

			case "_alert":
				event.preventDefault();
				event.stopPropagation();
				link.style.pointerEvents = "none";
				new URL(link.href).get(function (status)
				{
					alert(status);
					link.style.pointerEvents = "";
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
					.forEach(e =>
					{
						var popup = window.top.document.createElement("g-popup");
						popup.element = e;
						popup.addEventListener("hide", () => link.appendChild(e));
						popup.show();
					});
				break;
			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(process =>
				{
					link.setAttribute("data-process", process);
					process = new GProcess(JSON.parse(process));

					let dialog = window.top.document.createElement("g-progress-dialog");
					dialog.process = process.id;
					dialog.caption = link.getAttribute("title") || "Progresso";
					dialog.target = link.getAttribute("data-redirect") || "_self";

					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

					if (link.getAttribute("data-on-hide"))
						if (link.getAttribute("data-on-hide") === "reload")
							dialog.addEventListener("hide", () => window.location = window.location.href);
						else if (link.getAttribute("data-on-hide") === "submit")
							dialog.addEventListener("hide", () => link.closest("form").submit());
						else if (link.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
							dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
									.exec(link.getAttribute("data-on-hide"))[1]).submit());


					dialog.addEventListener("redirect", event => window.location.href = event.detail);

					dialog.show();
				});
				break;
			case "_progress-window":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(process =>
				{
					link.setAttribute("data-process", process);
					process = new GProcess(JSON.parse(process));

					let dialog = window.top.document.createElement("g-progress-window");
					dialog.process = process.id;
					dialog.target = link.getAttribute("data-redirect") || "_self";


					dialog.addEventListener("show", () => link.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
					dialog.addEventListener("hide", () => link.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

					if (link.getAttribute("data-on-hide"))
						if (link.getAttribute("data-on-hide") === "reload")
							dialog.addEventListener("hide", () => window.location = window.location.href);
						else if (link.getAttribute("data-on-hide") === "submit")
							dialog.addEventListener("hide", () => link.closest("form").submit());
						else if (link.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
							dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
									.exec(link.getAttribute("data-on-hide"))[1]).submit());

					dialog.addEventListener("redirect", event => window.location.href = event.detail);

					dialog.show();
				});
				break;
			case "_report":
			case "_report-dialog":
				event.preventDefault();
				event.stopPropagation();
				let dialog = window.top.document.createElement("g-report-dialog");
				dialog.blocked = true;
				dialog.caption = link.getAttribute("title") || "Imprimir";
				dialog.get(link.href);
				break;

			default:
				if (/^_id\(([a-zA-Z0-9]+)\)$/g.test(target))
				{
					event.preventDefault();
					event.stopPropagation();
					link.style.pointerEvents = "none";
					new URL(link.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type === "SUCCESS")
								document.getElementById(/^_id\(([a-zA-Z0-9]+)\)$/g.exec(target)[1])
									.innerHTML = status.message;
							else
								Message.show(status, 2000);
						} finally
						{
							link.style.pointerEvents = "";
						}
					});
				}
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