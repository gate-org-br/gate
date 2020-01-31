/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GDialog extends Window
{
	constructor()
	{
		super();
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-dialog");

		let minimize = new Command();
		this.head.appendChild(minimize);
		minimize.innerHTML = "<i>&#x3019;<i/>";
		minimize.action = () => this.minimize();

		let fullScreen = new Command();
		this.head.appendChild(fullScreen);
		fullScreen.innerHTML = (FullScreen.status() ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");
		fullScreen.action = element => element.innerHTML = (FullScreen.switch(this.main) ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");

		let close = new Command();
		this.head.appendChild(close);
		close.action = () => this.hide();
		close.innerHTML = "<i>&#x1011;<i/>";

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
					this.focus();
					break;
			}

			event.preventDefault();
			event.stopPropagation();
		});
	}

	get iframe()
	{
		if (!this._private.iframe)
		{
			let iframe = this._private.iframe
				= this.body.appendChild(window.top.document.createElement('iframe'));

			iframe.dialog = this;
			iframe.scrolling = "no";
			iframe.setAttribute('name', '_dialog');
			iframe.onmouseenter = () => this._private.iframe.focus();

			iframe.addEventListener("keydown", event =>
			{
				event = event ? event : window.event;
				if (event.keyCode === ESC)
				{
					this.head.focus();
					event.preventDefault();
					event.stopPropagation();
				}
			});

			iframe.addEventListener("load", () =>
			{
				iframe.name = "_frame";
				iframe.setAttribute("name", "_frame");
				iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));

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

				resize();
				window.addEventListener("refresh_size", resize);
				iframe.backgroundImage = "none";
			});
		}

		return this._private.iframe;
	}

	set navigator(navigator)
	{
		if (navigator && navigator.length > 1)
		{
			let navbar = new NavBar(navigator, navigator.target);
			navbar.addEventListener("update", event => this._private.iframe.setAttribute('src', event.detail.target));
			this.foot.appendChild(navbar);
		}

	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
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

	static set caption(caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.caption = caption;
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static create()
	{
		return 	document === window.top.document ? new GDialog()
			: window.top.document.importNode(new GDialog());
	}
}

customElements.define('g-dialog', GDialog);

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
				let dialog = GDialog.create();
				dialog.target = this.href;
				dialog.caption = this.getAttribute("title");
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

				let dialog = GDialog.create();
				dialog.target = url;
				dialog.caption = getter.getAttribute("title");
				dialog.get.apply(dialog, parameters);

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