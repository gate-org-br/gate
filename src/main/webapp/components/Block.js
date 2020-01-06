/* global Modal, customElements */

class Block extends Window
{
	constructor(text)
	{
		super({blocked: true});
		this.classList.add("g-block");

		this.caption = text || "Aguarde";

		this.body.appendChild(window.top.document.createElement('progress'));

		this.foot.appendChild(window.top.document.createElement('digital-clock'));

		this.show();
	}
}

customElements.define('g-block', Block);

Block.show = function (text)
{
	if (!window.top.GateBlockDialog)
		window.top.GateBlockDialog = new Block(text);
};

Block.hide = function ()
{
	if (window.top.GateBlockDialog)
	{
		window.top.GateBlockDialog.hide();
		window.top.GateBlockDialog = null;
	}
};

window.addEventListener("load", function ()
{
	Block.hide();

	Array.from(document.querySelectorAll("form[data-block]")).forEach(function (element)
	{
		element.addEventListener("submit", function ()
		{
			if (this.getAttribute("data-block"))
				Block.show(this.getAttribute("data-block"));
		});
	});

	Array.from(document.querySelectorAll("a")).forEach(function (element)
	{
		element.addEventListener("click", function ()
		{
			if (this.getAttribute("data-block"))
				Block.show(this.getAttribute("data-block"));
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
						Block.show(button.getAttribute("data-block"));
						event.target.removeEventListener(event.type, arguments.callee);
					});
				else
					Block.show(this.getAttribute("data-block"));
		});
	});
});


