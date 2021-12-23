function mask(element)
{
	var changed = false;
	element.style.fontFamily = 'monospace';
	element.setAttribute("autocomplete", "off");
	const MASKS = {'#': "[0-9]", '_': "[a-zA-Z]", '*': "[0-9a-zA-Z]"};

	var mask = element.getAttribute("data-mask");
	var placeholder = element.getAttribute("placeholder");
	if (!placeholder || placeholder.length !== mask.length)
	{
		placeholder = "";
		for (var i = 0; i < mask.length; i++)
			placeholder += MASKS[mask.charAt(i)] ? "_" : mask.charAt(i);
		element.setAttribute("placeholder", placeholder);
	}

	element.getCursor = function ()
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
			document.write("Navegador muito antigo. Favor atualizar.");
	};

	element.setCursor = function (i)
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
			document.write("Navegador muito antigo. Favor atualizar.");
		return false;
	};

	element.onkeydown = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
		{
			// ///////////////////////////////////////////////////////
			// ESQUERDA
			// ///////////////////////////////////////////////////////
			case 37:

				for (var i = this.getCursor() - 1; i >= 0; i--)
					if (MASKS[mask.charAt(i)])
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// DIREITA
				// ///////////////////////////////////////////////////////
			case 39:

				for (var i = this.getCursor() + 1; i <= mask.length; i++)
					if (i === mask.length || MASKS[mask.charAt(i)])
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// BACKSPACE
				// ///////////////////////////////////////////////////////
			case 8:

				for (var i = this.getCursor() - 1; i >= 0; i--)
				{
					if (MASKS[mask.charAt(i)])
					{
						this.value = this.value.substr(0, i) + placeholder[i] + this.value.substr(i + 1);
						this.setCursor(i);
						changed = true;
						break;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// Delete
				// ///////////////////////////////////////////////////////
			case 46:

				for (var i = this.getCursor(); i < mask.length; i++)
				{
					if (MASKS[mask.charAt(i)])
					{
						this.value = this.value.substr(0, i) + placeholder[i] + this.value.substr(i + 1);

						this.setCursor(i + 1);
						while (this.getCursor() < mask.length
							&& !MASKS[mask.charAt(this.getCursor())])
							this.setCursor(this.getCursor() + 1);
						changed = true;
						break;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// END
				// ///////////////////////////////////////////////////////
			case 35:
				return this.setCursor(mask.length);
				// ///////////////////////////////////////////////////////
				// HOME
				// ///////////////////////////////////////////////////////
			case 36:
				return this.setCursor(0);
		}

		return true;
	};

	element.onkeypress = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
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

				var c = String.fromCharCode(event.which ? event.which : event.keyCode);
				if (event.ctrlKey)
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

				for (var i = this.getCursor(); i < mask.length; i++)
				{
					if (MASKS[mask.charAt(i)] && c.match(MASKS[mask.charAt(i)]))
					{
						this.value = this.value.substr(0, i) + c + this.value.substr(i + 1);
						this.setCursor(i + 1);

						while (this.getCursor() < mask.length
							&& !MASKS[mask.charAt(this.getCursor())])
							this.setCursor(this.getCursor() + 1);

						changed = true;
						return false;
					}
				}

				return false;
		}
	};

	element.onpaste = function (event)
	{
		var data = event.clipboardData.getData("text");
		if (data)
		{
			var index = 0;
			var cursor = 0;

			while (index < data.length && cursor < mask.length)
			{
				while (cursor < mask.length && !MASKS[mask.charAt(cursor)])
					cursor++;
				while (cursor < mask.length && index < data.length && !data[index].match(MASKS[mask.charAt(cursor)]))
					index++;

				if (cursor < mask.length && index < data.length)
				{
					this.value = this.value.substr(0, cursor) + data[index] + this.value.substr(cursor + 1);
					changed = true;
					cursor++;
					index++;
				}
			}
		}
		return false;
	};

	element.onfocus = function ()
	{
		if (!this.value && placeholder)
			this.value = placeholder;

		setTimeout(() =>
		{
			this.setCursor(0);
			while (this.getCursor() < mask.length
				&& !MASKS[mask.charAt(this.getCursor())])
				this.setCursor(this.getCursor() + 1);
		}, 0);
	};

	element.onblur = function ()
	{
		if (this.value === placeholder)
			this.value = '';

		if (changed)
			this.dispatchEvent(new CustomEvent('change', {bubbles: true}));
		changed = false;
	};

	element.oncut = () => false;
}

Array.from(document.querySelectorAll('input[data-mask]')).forEach(element => mask(element));