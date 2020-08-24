/* global Message, Block, ENTER, ESC, link, GDialog, GStackFrame */

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let button = event.target
		.closest("button");
	if (!button)
		return;

	if (button.hasAttribute("data-cancel"))
	{
		Message.error(event.target.getAttribute("data-cancel"), 2000);
		event.preventDefault();
		event.stopImmediatePropagation();
		return;
	}


	if (button.hasAttribute("data-disabled"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (button.hasAttribute("data-confirm")
		&& !confirm(button.getAttribute("data-confirm")))
	{
		event.preventDefault();
		event.stopImmediatePropagation();
		return;
	}

	if (button.hasAttribute("data-alert"))
		alert(button.getAttribute("data-alert"));


	if (button.getAttribute("formaction") &&
		(button.getAttribute("formaction").match(/([@][{][^}]*[}])/g)
			|| button.getAttribute("formaction").match(/([!][{][^}]*[}])/g)
			|| button.getAttribute("formaction").match(/([?][{][^}]*[}])/g)))
	{
		let resolved = resolve(button.getAttribute("formaction"));
		if (resolved !== null)
		{
			var formaction = button.getAttribute("formaction");
			button.setAttribute("formaction", resolved);
			button.click();
			button.setAttribute("formaction", formaction);
		}

		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	let target = button.getAttribute("formtarget");
	if (target)
	{
		switch (target)
		{
			case "_dialog":
				if (button.form.checkValidity())
				{
					if (event.ctrlKey)
					{
						event.preventDefault();
						event.stopPropagation();
						button.setAttribute("formtarget", "_blank");
						button.click();
						button.setAttribute("formtarget", "_dialog");
					} else if (event.target.form.getAttribute("target") !== "_dialog")
					{
						let dialog = GDialog.create();
						dialog.caption = event.target.getAttribute("title");
						dialog.blocked = Boolean(event.target.getAttribute("data-blocked"));

						dialog.addEventListener("show", () => button.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => button.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

						if (button.getAttribute("data-on-hide"))
							if (button.getAttribute("data-on-hide") === "reload")
								dialog.addEventListener("hide", () => window.location = window.location.href);
							else if (button.getAttribute("data-on-hide") === "submit")
								dialog.addEventListener("hide", () => button.closest("form").submit());
							else if (button.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
								dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
										.exec(button.getAttribute("data-on-hide"))[1]).submit());

						dialog.show();
					}
				}
				break;
			case "_stack":
				if (button.form.checkValidity())
				{
					if (event.ctrlKey)
					{
						event.preventDefault();
						event.stopPropagation();
						button.setAttribute("formtarget", "_blank");
						button.click();
						button.setAttribute("formtarget", "_stack");
					} else if (event.target.form.getAttribute("target") !== "_stack")
					{
						let dialog = GStackFrame.create();

						dialog.addEventListener("show", () => button.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => button.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

						if (button.getAttribute("data-on-hide"))
							if (button.getAttribute("data-on-hide") === "reload")
								dialog.addEventListener("hide", () => window.location = window.location.href);
							else if (button.getAttribute("data-on-hide") === "submit")
								dialog.addEventListener("hide", () => button.closest("form").submit());
							else if (button.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
								dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
										.exec(button.getAttribute("data-on-hide"))[1]).submit());

						dialog.show();
					}
				}
				break;
			case "_message":
				event.preventDefault();
				event.stopPropagation();

				button.disabled = true;
				new URL(button.getAttribute("formaction"))
					.post(new FormData(button.form), function (status)
					{
						try
						{
							status = JSON.parse(status);
							Message.show(status, 2000);
						} finally
						{
							button.disabled = false;
						}
					});
				break;
			case "_none":
				event.preventDefault();
				event.stopPropagation();

				button.disabled = true;
				new URL(button.getAttribute("formaction"))
					.post(new FormData(button.form), function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type !== "SUCCESS")
								Message.show(status, 2000);
						} finally
						{
							button.disabled = false;
						}
					});
				break;
			case "_this":
				event.preventDefault();
				event.stopPropagation();

				button.disabled = true;
				new URL(button.getAttribute("formaction"))
					.post(new FormData(button.form), function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type === "SUCCESS")
								button.innerHTML = status.value;
							else
								Message.show(status, 2000);
						} finally
						{
							button.disabled = false;
						}
					});
				break;
			case "_alert":
				event.preventDefault();
				event.stopPropagation();

				if (button.form.reportValidity())
				{
					button.disabled = true;
					new URL(button.getAttribute("formaction"))
						.post(new FormData(button.form), function (status)
						{
							alert(status);
							button.disabled = false;
						});
				}
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

			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();

				if (button.form.reportValidity())
				{
					button.disabled = true;

					new URL(button.getAttribute("formaction")).post(new FormData(button.form), process =>
					{
						button.setAttribute("data-process", process);
						process = new GProcess(JSON.parse(process));
						let dialog = window.top.document.createElement("g-progress-dialog");
						dialog.process = process.id;
						dialog.caption = button.getAttribute("title") || "Progresso";
						dialog.target = button.getAttribute("data-redirect") || "_self";

						dialog.addEventListener("show", () => button.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => button.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

						if (button.getAttribute("data-on-hide"))
							if (button.getAttribute("data-on-hide") === "reload")
								dialog.addEventListener("hide", () => window.location = window.location.href);
							else if (button.getAttribute("data-on-hide") === "submit")
								dialog.addEventListener("hide", () => button.closest("form").submit());
							else if (button.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
								dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
										.exec(button.getAttribute("data-on-hide"))[1]).submit());

						dialog.addEventListener("redirect", event => window.location.href = event.detail);

						dialog.show();

						button.disabled = false;
					});
				}

				break;

			case "_progress-window":
				event.preventDefault();
				event.stopPropagation();

				if (button.form.reportValidity())
				{
					button.disabled = true;

					new URL(button.getAttribute("formaction")).post(new FormData(button.form), process =>
					{
						button.setAttribute("data-process", process);
						process = new GProcess(JSON.parse(process));
						let status = window.top.document.createElement("g-progress-window");
						status.process = process.id;
						status.target = button.getAttribute("data-redirect") || "_self";

						dialog.addEventListener("show", () => button.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => button.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

						if (button.getAttribute("data-on-hide"))
							if (button.getAttribute("data-on-hide") === "reload")
								dialog.addEventListener("hide", () => window.location = window.location.href);
							else if (button.getAttribute("data-on-hide") === "submit")
								dialog.addEventListener("hide", () => button.closest("form").submit());
							else if (button.getAttribute("data-on-hide").match(/submit\([^)]+\)/))
								dialog.addEventListener("hide", () => document.getElementById(/submit\(([^)]+)\)/
										.exec(button.getAttribute("data-on-hide"))[1]).submit());

						dialog.addEventListener("redirect", event => window.location.href = event.detail);

						status.show();

						button.disabled = false;
					});
				}
				break;

			case "_report":
			case "_report-dialog":
				event.preventDefault();
				event.stopPropagation();

				if (button.form.reportValidity())
				{
					let dialog = new GReportDialog();
					dialog.blocked = true;
					dialog.caption = button.getAttribute("title") || "Imprimir";
					dialog.post(button.getAttribute("formaction") || button.form.action,
						new FormData(button.form));
					button.disabled = false;
				}

				break;

			default:
				if (/^_id\(([a-zA-Z0-9]+)\)$/g.test(target))
				{
					event.preventDefault();
					event.stopPropagation();
					button.style.pointerEvents = "none";
					new URL(button.getAttribute("formaction") || button.form.action).get(function (status)
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
							button.style.pointerEvents = "";
						}
					});
				}
		}

		return;
	}
});


window.addEventListener("keydown", function (event)
{
	let link = event.target;
	if (link.tagName === 'BUTTON' && event.keyCode === 32)
	{
		event.target.click();
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
});