function Dialog()
{
	var modal = new Modal();

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.className = "Dialog";
	dialog.closeable = true;

	var head = dialog.appendChild(window.top.document.createElement('div'));
	head.setAttribute("tabindex", "1");
	head.focus();

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.style['float'] = "left";

	var close = head.appendChild(window.top.document.createElement("a"));
	close.title = 'Fechar janela';
	close.style['float'] = "right";
	close.innerHTML = "&#x1011;";

	close.onclick = function ()
	{
		modal.hide();
	};

	var last = head.appendChild(window.top.document.createElement("a"));
	last.title = 'Ir para o último registro';
	last.style['float'] = "right";
	last.innerHTML = "&#x2216;";
	last.style.display = "none";

	var next = head.appendChild(window.top.document.createElement("a"));
	next.title = 'Ir para o próximo registro';
	next.style['float'] = "right";
	next.innerHTML = "&#x2211;";
	next.style.display = "none";

	var navigator = head.appendChild(window.top.document.createElement('label'));
	navigator.title = 'Ir para o registro atual';
	navigator.style['float'] = "right";
	navigator.style.cursor = "pointer";
	navigator.style.display = "none";

	var prev = head.appendChild(window.top.document.createElement("a"));
	prev.title = 'Ir para o registro anterior';
	prev.style['float'] = "right";
	prev.innerHTML = "&#x2212;";
	prev.style.display = "none";

	var frst = head.appendChild(window.top.document.createElement("a"));
	frst.title = 'Ir para o primeiro registro';
	frst.style['float'] = "right";
	frst.innerHTML = "&#x2213;";
	frst.style.display = "none";

	var body = dialog.appendChild(window.top.document.createElement('div'));

	var iframe = body.appendChild(window.top.document.createElement('iframe'));
	iframe.dialog = modal;
	iframe.setAttribute('name', '_dialog');

	head.onmouseenter = function ()
	{
		this.focus();
	};

	iframe.onmouseenter = function ()
	{
		this.focus();
	};

	iframe.onload = function ()
	{
		iframe.name = "_frame";
		iframe.setAttribute("name", "_frame");

		head.onkeydown = undefined;
		head.addEventListener("keydown",
			function (e)
			{
				e = e ? e : window.event;
				switch (e.keyCode)
				{
					case ESC:
						if (dialog.closeable)
							modal.hide();
						break;
					case ENTER:
						iframe.focus();
						break;
				}

				e.preventDefault();
				e.stopPropagation();
			});

		iframe.contentWindow
			.addEventListener("keydown", function (e)
			{
				e = e ? e : window.event;
				switch (e.keyCode)
				{
					case ESC:
						head.focus();
						e.preventDefault();
						e.stopPropagation();
						break;
				}
			});

		iframe.addEventListener("focus", function ()
		{
			autofocus(this.contentWindow.document);
		});

		if (modal.navigator)
		{
			for (i = 0; i < modal.navigator.length; i++)
			{
				if (this.contentWindow.location.href.endsWith(modal.navigator[i]))
				{
					var index = i;
					if (index > 0)
					{
						frst.style.display = "block";
						prev.style.display = "block";
						frst.onclick = function ()
						{
							iframe.src = modal.navigator[0];
							return false;
						};

						prev.onclick = function ()
						{
							iframe.src = modal.navigator[index - 1];
							return false;
						};
					} else
					{
						frst.onclick = null;
						prev.onclick = null;
						frst.style.display = "none";
						prev.style.display = "none";
					}

					navigator.innerHTML = "Registro " + (i + 1) + " de " + modal.navigator.length;
					navigator.style.display = "block";
					navigator.onclick = function ()
					{
						iframe.src = modal.navigator[index];
					}

					if (index < modal.navigator.length - 1)
					{
						next.onclick = function ()
						{
							iframe.src = modal.navigator[index + 1];
							return false;
						};

						last.onclick = function ()
						{
							iframe.src = modal.navigator[modal.navigator.length - 1];
							return false;
						};

						next.style.display = "block";
						last.style.display = "block";
					} else
					{
						next.onclick = null;
						last.onclick = null;
						next.style.display = "none";
						last.style.display = "none";
					}

					head.onkeydown = function (e)
					{
						e = e ? e : window.event;
						switch (e.keyCode)
						{
							case END:
								if (index < modal.navigator.length - 1)
									iframe.src = modal.navigator[modal.navigator.length - 1];
								break;
							case HOME:
								if (index > 0)
									iframe.src = modal.navigator[0];
								break;
							case UP:
							case LEFT:
								if (index > 0)
									iframe.src = modal.navigator[index - 1];
								break;
							case DOWN:
							case RIGHT:
								if (index < modal.navigator.length - 1)
									iframe.src = modal.navigator[index + 1];
								break;
						}
						e.preventDefault();
						e.stopPropagation();
					}
					;

					break;
				}
			}
		}
	};

	modal.setCloseable = function (closeable)
	{
		dialog.closeable = closeable;
		close.style.display = closeable ? "" : "none";
		return this;
	};

	modal.setTitle = function (title)
	{
		caption.innerHTML = title;
		return this;
	};

	modal.setTarget = function (target)
	{
		iframe.setAttribute('src', target);
		return this;
	};

	modal.setNavigator = function (navigator)
	{
		this.navigator = navigator;
		return this;
	};

	modal.getWindow = function ()
	{
		return iframe.contentWindow;
	};

	modal.get = function ()
	{
		this.arguments = arguments;
		this.show();
	};

	modal.ret = function ()
	{
		for (var i = 0; i < Math.min(arguments.length, this.arguments.length); i++)
		{
			if (this.arguments[i])
			{
				switch (this.arguments[i].tagName.toLowerCase())
				{
					case "input":
						this.arguments[i].value = arguments[i];
						break;
					case "textarea":
						this.arguments[i].innerHTML = arguments[i];
						break;
					case "select":
						this.arguments[i].value = arguments[i];
						break;
				}
			}
		}
		this.hide();
	};

	return modal;
}

window.addEventListener("load", function ()
{
	search('a[data-get]').forEach(function (a)
	{
		a.get = a.getAttribute('data-get').split(",")
			.map(function (e)
			{
				return e.trim();
			})
			.map(function (e)
			{
				return e !== 'null' ? select(e) : null;
			});

		a.onclick = function ()
		{
			if (this.get.some(function (e)
			{
				return e && e.value;
			}))
			{
				this.get
					.filter(function (e)
					{
						return e && e.value;
					})
					.forEach(function (e)
					{
						e.value = "";
					});
				return false;
			}

			var dialog = new Dialog();
			dialog.setTitle(this.getAttribute("title"))
				.setTarget(this.href)
				.get.apply(dialog, this.get);
			return false;
		};
	});

	search('a[data-ret], tr[data-ret], li[data-ret], td[data-ret]').forEach(function (e)
	{
		e.onmouseover = function ()
		{
			this.focus();
		};

		e.onmouseout = function ()
		{
			this.blur();
		};

		e.onclick = function ()
		{
			var ret = this.getAttribute("data-ret")
				.split(this.getAttribute("data-sep") ?
					this.getAttribute("data-sep") : ",")
				.map(function (e)
				{
					return e.trim();
				});
			window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
			return false;
		};

		e.onkeydown = function (e)
		{
			e = e ? e : window.event;
			if (e.keyCode === 13)
				this.onclick();
			return true;
		};
	});

	search('a.Hide').forEach(function (a)
	{
		a.onclick = function ()
		{
			if (window.frameElement
				&& window.frameElement.dialog
				&& window.frameElement.dialog.hide)
				window.frameElement.dialog.hide();
			else
				window.close();
		};
	});
});

