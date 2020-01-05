/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class Dialog extends Window
{
	constructor(options)
	{
		super(options);
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-dialog");

		let overflow = this.commands.add(document.createElement("g-overflow"));
		overflow.innerHTML = "<i>&#X3018;</i>";

		let close = this.commands.add(document.createElement("g-command"));
		close.action(() => this.hide());
		close.innerHTML = "Fechar janela<i>&#x1011;<i/>";

		let fullScreen = this.commands.add(document.createElement("g-command"));
		fullScreen.innerHTML = "Tela cheia" + (FullScreen.status() ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");
		fullScreen.action(element => element.innerHTML = "Tela cheia" + (FullScreen.switch(this.main) ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>"));

		if (options && options.navigator && options.navigator.length)
			this.main.appendChild(new NavBar(options.navigator, options.target))
				.addEventListener("update", event => iframe.setAttribute('src', event.detail.target));

		var iframe = this.body.appendChild(window.top.document.createElement('iframe'));
		iframe.dialog = this;
		iframe.scrolling = "no";
		iframe.setAttribute('name', '_dialog');
		iframe.onmouseenter = () => iframe.focus();

		var resize = () =>
		{
			if (!iframe.contentWindow
				|| !iframe.contentWindow.document
				|| !iframe.contentWindow.document.body
				|| !iframe.contentWindow.document.body.scrollHeight)
				return false;

			let height = Math.max(iframe.contentWindow.document.body.scrollHeight, this.body.offsetHeight) + "px";
			if (iframe.height !== height)
			{
				iframe.height = "0";
				iframe.height = height;
			}
			return true;
		};

		iframe.addEventListener("load", () =>
		{
			iframe.name = "_frame";
			iframe.setAttribute("name", "_frame");

			this.head.onkeydown = undefined;
			this.head.addEventListener("keydown", event =>
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

	static rename(caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.rename(caption);
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
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

customElements.define('g-dialog', Dialog);