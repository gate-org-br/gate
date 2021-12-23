/* global customElements */

import GWindow from './g-window.mjs';

export default class GBlock extends GWindow
{
	constructor()
	{
		super();
		this.blocked = true;
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-block");
		this.body.appendChild(window.top.document.createElement('progress'));
		this.foot.appendChild(window.top.document.createElement('g-digital-clock'));
	}

	static show(text)
	{
		if (!window.top.GateBlockDialog)
		{
			window.top.GateBlockDialog = window.top.document.createElement("g-block");
			window.top.GateBlockDialog.caption = text || "Aguarde";
			window.top.GateBlockDialog.show();
		}
	}

	static  hide()
	{
		if (window.top.GateBlockDialog)
		{
			window.top.GateBlockDialog.hide();
			window.top.GateBlockDialog = null;
		}
	}
}

customElements.define('g-block', GBlock);

GBlock.hide();

Array.from(document.querySelectorAll("form[data-block]")).forEach(function (element)
{
	element.addEventListener("submit", function ()
	{
		if (this.getAttribute("data-block"))
			GBlock.show(this.getAttribute("data-block"));
	});
});

Array.from(document.querySelectorAll("a")).forEach(function (element)
{
	element.addEventListener("click", function ()
	{
		if (this.getAttribute("data-block"))
			GBlock.show(this.getAttribute("data-block"));
	});
});

Array.from(document.querySelectorAll("button")).forEach(function (button)
{
	button.addEventListener("click", function ()
	{
		if (button.getAttribute("data-block"))
			if (button.form)
				button.form.addEventListener("submit", function (event)
				{
					GBlock.show(button.getAttribute("data-block"));
					event.target.removeEventListener(event.type, arguments.callee);
				});
			else
				GBlock.show(this.getAttribute("data-block"));
	});
});