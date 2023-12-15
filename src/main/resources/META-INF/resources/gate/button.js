import URL from './url.js';
import resolve from './resolve.js';
import Process from './process.js';
import GMessageDialog from './g-message-dialog.js';

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let button = event.composed
		? event.composedPath()[0].closest("button")
		: event.target.closest("button");

	if (!button)
		return;

	let target = button.getAttribute("formtarget");
	if (target)
	{
		switch (target)
		{
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
							GMessageDialog.show(status, 2000);
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

			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();

				if (button.form.reportValidity())
				{
					button.disabled = true;

					new URL(button.getAttribute("formaction")).post(new FormData(button.form), process =>
					{
						button.setAttribute("data-process", process);
						process = new Process(JSON.parse(process));
						let dialog = window.top.document.createElement("g-progress-dialog");
						dialog.process = process.id;
						dialog.caption = button.getAttribute("title") || "Progresso";
						dialog.target = button.getAttribute("data-redirect") || "_self";

						dialog.addEventListener("show", () => button.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
						dialog.addEventListener("hide", () => button.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));

						dialog.addEventListener("redirect", event => window.location.href = event.detail);

						dialog.show();

						button.disabled = false;
					});
				}

				break;
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