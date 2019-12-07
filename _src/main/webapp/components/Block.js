class Block extends Modal
{
	constructor(text)
	{
		super({blocked: true});

		var dialog = this.element()
			.appendChild(window.top.document.createElement('div'));
		dialog.className = "Block";

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.setAttribute("tabindex", "1");
		head.focus();

		head.appendChild(window.top.document.createElement('label'))
			.innerHTML = "Aguarde";

		var body = dialog.appendChild(window.top.document.createElement('div'));

		body.appendChild(window.top.document.createElement('label'))
			.innerHTML = text;

		body.appendChild(window.top.document.createElement('progress'));

		dialog.appendChild(window.top.document.createElement('digital-clock'));

		this.show();
	}
}

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