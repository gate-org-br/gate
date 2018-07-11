function Modal()
{
	var body = window.top.document.body;
	var overflow = body.style.overflow;

	var dialog = window.top.document.createElement('div');
	dialog.className = "Modal";

	dialog.show = function ()
	{
		body.style.overflow = "hidden";
		body.appendChild(this);
		return this;
	};

	dialog.setOnHide = function (onHide)
	{
		this.onHide = onHide;
		return this;
	};

	dialog.hide = function ()
	{
		if (this.parentNode)
		{
			body.style.overflow = overflow;
			if (this.onHide)
				eval(this.onHide);
			this.parentNode.removeChild(this);
		}
		return this;
	};

	return dialog;
}