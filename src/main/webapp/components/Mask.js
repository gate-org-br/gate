function Mask(e)
{
	e.placeholder = new String();
	e.mask = e.getAttribute("data-mask");
	for (var i = 0; i < e.mask.length; i++)
		e.placeholder += e.mask.charAt(i).match('[#*_]') ? "_" : e.mask.charAt(i);
	e.setAttribute('placeholder', e.placeholder);

	e.getCursor = function ()
	{
		if (this.selectionStart >= 0)
		{
			return this.selectionStart;
		} else if (document.selection)
		{
			this.focus();
			var selection = document.selection.createRange();
			selection.moveStart('character', -this.value.length);
			return selection.text.length;
		} else
		{
			document.write("Navegador muito antigo. Favor atualizar.");
		}
	};

	e.setCursor = function (i)
	{
		if (this.selectionStart >= 0)
		{
			this.focus();
			this.setSelectionRange(i, i);
		} else if (this.createTextRange)
		{
			var selection = this.createTextRange();
			selection.move('character', i);
			selection.select();
		} else
		{
			document.write("Navegador muito antigo. Favor atualizar.");
		}
		return false;
	};

	e.setValue = function (value)
	{
		this.value = value;
		if (!this.value)
			this.value = this.prevValue ? this.prevValue : this.placeholder;
	};


	e.check_mask = function ()
	{
		var ph = this.getAttribute('data-mask');
		for (var i = 0; i < this.mask.length && i < this.value.length; i++)
			if (ph.charAt(i) === '#')
				if (this.value.charAt(i).match("[_0-9]"))
					continue;
				else
					return false;
			else if (ph.charAt(i) === '_')
				if (this.value.charAt(i).match("[_a-zA-Z]"))
					continue;
				else
					return false;
			else if (ph.charAt(i) === '*')
				if (this.value.charAt(i).match("[_a-zA-Z0-9]"))
					continue;
				else
					return false;
			else if (this.value.charAt(i) === ph.charAt(i))
				continue;
			else
				return false;
		return true;
	};

	e.onkeydown = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			// ///////////////////////////////////////////////////////
			// ESQUERDA
			// ///////////////////////////////////////////////////////
			case 37:

				for (var i = this.getCursor() - 1; i >= 0; i--)
					if (this.mask.charAt(i).match("[#_*]"))
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// DIREITA
				// ///////////////////////////////////////////////////////
			case 39:

				for (var i = this.getCursor() + 1; i <= this.mask.length; i++)
					if (i === this.mask.length || this.mask.charAt(i).match("[#_*]"))
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// BACKSPACE
				// ///////////////////////////////////////////////////////
			case 8:

				for (var i = this.getCursor() - 1; i >= 0; i--)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						this.value = this.value.substr(0, i) + "__" + this.value.substr(i + 1);
						return this.setCursor(i + 1) || true;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// Delete
				// ///////////////////////////////////////////////////////
			case 46:

				for (var i = this.getCursor(); i < this.mask.length; i++)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						this.value = this.value.substr(0, i) + "__" + this.value.substr(i + 1);
						return this.setCursor(i + 1) || true;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// END
				// ///////////////////////////////////////////////////////
			case 35:
				return this.setCursor(this.mask.length);
				// ///////////////////////////////////////////////////////
				// HOME
				// ///////////////////////////////////////////////////////
			case 36:
				return this.setCursor(0);
		}

		return true;
	};

	e.onkeypress = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			case 8:
			case 9:
			case 13:
			case 35:
			case 36:
			case 37:
			case 39:
			case 46:
				return true;
			default:

				var c = String.fromCharCode(e.which ? e.which : e.keyCode);
				if (e.ctrlKey)
				{
					switch (c)
					{
						case 'c':
						case 'v':
							this.select();
							return true;
						default:
							return false;
					}
				}

				for (var i = this.getCursor(); i < this.mask.length; i++)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						switch (this.mask.charAt(i))
						{
							case '#':
								if (c.match("[0-9]"))
								{
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
							case '_':
								if (c.match("[a-zA-Z]")) {
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
							case '*':
								if (c.match("[0-9a-zA-Z]"))
								{
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
						}
					}
				}

				return false;
		}
	};

	e.oninput = function ()
	{
		if (this.check_mask())
		{
			for (var i = this.getCursor(); i < this.mask.length; i++)
			{
				if (this.mask.charAt(i).match("[#_*]"))
				{
					this.setCursor(i);
					break;
				}
			}
			return true;
		}

		this.setValue('');
		this.setCursor(0);
		return false;
	};
	e.onpaste = function ()
	{
		this.prevValue = this.value;
		this.select();
		return true;
	};
	e.onfocus = function ()
	{
		if (!this.value && this.placeholder)
			this.value = this.placeholder;

		var self = this;
		setTimeout(function ()
		{
			self.setCursor(0);
			for (var i = self.getCursor(); i < self.mask.length; i++)
				if (self.mask.charAt(i).match("[#_*]"))
					return self.setCursor(i);
		}, 0);
	};
	e.onblur = function ()
	{
		if (this.value === this.placeholder)
			this.value = '';
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('input[data-mask]')).forEach(function (e)
	{
		new Mask(e);
	});

	document.oncontextmenu = function (e)
	{
		return e && e.target && !e.target.getAttribute('data-mask');
	};
});

