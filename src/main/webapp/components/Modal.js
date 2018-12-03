function Modal()
{
	var body = window.top.document.body;
	var overflow = body.style.overflow;

	var modal = window.top.document.createElement('div');
	modal.className = "Modal";

	modal.show = function ()
	{
		body.style.overflow = "hidden";
		body.appendChild(this);
		return this;
	};

	modal.setOnHide = function (onHide)
	{
		this.onHide = onHide;
		return this;
	};

	modal.hide = function ()
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

	return modal;
}