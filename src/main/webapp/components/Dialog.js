/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments */

class Dialog extends Modal
{
	constructor(options)
	{
		super(options);

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.className = "Dialog";
		if (options && options.size && options.size.w)
			dialog.style.width = options.size.w;
		if (options && options.size && options.size.h)
			dialog.style.height = options.size.h;

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.onmouseenter = () => head.focus();
		head.setAttribute("tabindex", "1");
		head.focus();


		var caption = head.appendChild(window.top.document.createElement('label'));
		if (options && options.title)
			caption.innerHTML = options.title;

		if (!this.blocked())
		{
			var close = head.appendChild
				(window.top.document.createElement("a"));
			close.title = 'Fechar janela';
			close.innerHTML = "&#x1011;";
			close.onclick = () => this.hide();
		}
		var body = dialog.appendChild(window.top.document.createElement('div'));

		var iframe = body.appendChild(window.top.document.createElement('iframe'));
		iframe.dialog = this;
		iframe.scrolling = "no";
		iframe.setAttribute('name', '_dialog');
		iframe.onmouseenter = () => iframe.focus();

		var resize = function ()
		{
			if (!iframe.contentWindow
				|| !iframe.contentWindow.document
				|| !iframe.contentWindow.document.body
				|| !iframe.contentWindow.document.body.scrollHeight)
				return false;

			let height = Math.max(iframe.contentWindow.document.body.scrollHeight, body.offsetHeight) + "px";
			if (iframe.height !== height)
			{
				iframe.height = "0";
				iframe.height = height;
			}
			return true;
		};

		iframe._newCommand = function (icon, name, action)
		{
			var link = window.top.document.createElement("a");
			link.title = name;
			link.innerHTML = icon;
			link.onclick = action;

			if (close)
				head.insertBefore(link, close);
			else
				head.appendChild(link);
		};

		iframe.addEventListener("load", () =>
		{
			iframe.name = "_frame";
			iframe.setAttribute("name", "_frame");

			head.onkeydown = undefined;
			head.addEventListener("keydown", event =>
			{
				event = event ? event : window.event;
				switch (event.keyCode)
				{
					case ESC:
						if (!this.blocked())
							this.hide();
						break;
					case ENTER:
						iframe.focus();
						break;
				}

				event.preventDefault();
				event.stopPropagation();
			});

			iframe.addEventListener("keydown", event =>
			{
				event = event ? event : window.event;
				if (event.keyCode === ESC)
				{
					head.focus();
					event.preventDefault();
					event.stopPropagation();
				}
			});


			iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));

			resize();
			window.addEventListener("refresh_size", resize);

			iframe.backgroundImage = "none";
		});

		if (options && options.navigator)
		{
			var navigator = new NavBar(options.navigator);
			head.appendChild(navigator.element());
			navigator.element().addEventListener("go",
				event => iframe.setAttribute('src', event.detail.target));
			if (options.target)
				navigator.go(options.target);
		} else if (options && options.target)
			iframe.setAttribute('src', options.target);
	}

	get()
	{
		this.arguments = arguments;
		this.show();
	}

	ret()
	{
		let size = Math.min(arguments.length, this.arguments.length);
		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				if (this.arguments[i].tagName.toLowerCase() === "textarea")
					this.arguments[i].innerHTML = arguments[i];
				else
					this.arguments[i].value = arguments[i];

		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				this.arguments[i].dispatchEvent(new CustomEvent('changed'));

		this.hide();
	}

	static command(icon, name, action)
	{
		if (window.frameElement && window.frameElement._newCommand)
			window.frameElement._newCommand(icon, name, action);
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('a[data-get]')).forEach(function (element)
	{
		element.addEventListener("click", function (event)
		{
			event.preventDefault();
			event.stopPropagation();
			var parameters =
				CSV.parse(this.getAttribute('data-get'))
				.map(e => e.trim())
				.map(e => e !== null ? document.getElementById(e) : null);
			if (parameters.some(e => e && e.value))
			{
				parameters = parameters.filter(e => e && e.value);
				parameters.forEach(e => e.value = "");
				parameters.forEach(e => e.dispatchEvent(new CustomEvent('changed')));
			} else {
				var dialog = new Dialog({target: this.href, title: this.getAttribute("title")});
				dialog.get.apply(dialog, parameters);
			}
		});
	});
	Array.from(document.querySelectorAll('input[data-getter]')).forEach(function (element)
	{
		element.addEventListener("change", function ()
		{
			var getter = document.getElementById(this.getAttribute("data-getter"));
			var url = resolve(getter.href);
			var parameters =
				CSV.parse(getter.getAttribute('data-get'))
				.map(id => id.trim())
				.map(id => id !== null ? document.getElementById(id) : null);
			if (this.value)
			{
				parameters
					.filter(e => e)
					.filter(e => e.value)
					.forEach(e => e.value = "");
				var dialog = new Dialog({target: url,
					title: getter.getAttribute("title")});
				dialog.get.apply(dialog, parameters);
			} else
				parameters
					.filter(e => e)
					.filter(e => e.value)
					.forEach(e => e.value = "");
			event.preventDefault();
			event.stopPropagation();
		});
	});
	Array.from(document.querySelectorAll('*[data-ret]')).forEach(function (element)
	{
		element.onmouseover = () => element.focus();
		element.onmouseout = () => element.blur();
		element.onclick = function ()
		{
			var ret = CSV.parse(this.getAttribute("data-ret")).map(e => e.trim());
			window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
			return false;
		};
		element.onkeydown = function (e)
		{
			e = e ? e : window.event;
			if (e.keyCode === 13)
				this.onclick();
			return true;
		};
	});
	Array.from(document.querySelectorAll('a.Hide')).forEach(function (element)
	{
		element.onclick = function ()
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