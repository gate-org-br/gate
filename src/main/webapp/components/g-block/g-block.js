/* global customElements */

class GBlock extends GWindow
{
	constructor()
	{
		super();
		this.blocked = true;
		this.classList.add("g-block");
		this.body.appendChild(window.top.document.createElement('progress'));
		this.foot.appendChild(window.top.document.createElement('digital-clock'));
	}
}

customElements.define('g-block', GBlock);

GBlock.show = function (text)
{
	if (!window.top.GateBlockDialog)
	{
		window.top.GateBlockDialog = new GBlock(text);
		window.top.GateBlockDialog.caption = text || "Aguarde";
		window.top.GateBlockDialog.show();
	}
};

GBlock.hide = function ()
{
	if (window.top.GateBlockDialog)
	{
		window.top.GateBlockDialog.hide();
		window.top.GateBlockDialog = null;
	}
};

window.addEventListener("load", function ()
{
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
});


