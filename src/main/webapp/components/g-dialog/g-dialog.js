/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GDialog extends GWindow
{
	constructor()
	{
		super();

		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-dialog");

		let minimize = document.createElement("a");
		minimize.href = "#";
		this.head.appendChild(minimize);
		minimize.innerHTML = "<i>&#x3019;<i/>";
		minimize.onclick = () => this.minimize();

		let fullScreen = document.createElement("a");
		fullScreen.href = "#";
		this.head.appendChild(fullScreen);
		fullScreen.innerHTML = (FullScreen.status() ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");
		fullScreen.onclick = element => element.innerHTML = (FullScreen.switch(this.main) ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");

		let close = document.createElement("a");
		close.href = "#";
		this.head.appendChild(close);
		close.onclick = () => this.hide();
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


		this._private.iframe = this.body.appendChild(window.top.document.createElement('iframe'));

		this.iframe.dialog = this;
		this.iframe.scrolling = "no";
		this.iframe.setAttribute('name', '_dialog');
		this.iframe.onmouseenter = () => this.iframe.focus();

		this.iframe.addEventListener("load", () =>
		{
			this.iframe.name = "_frame";
			this.iframe.setAttribute("name", "_frame");
			this.iframe.addEventListener("focus", () => autofocus(this.iframe.contentWindow.document));

			var resize = () =>
			{
				if (!this.iframe.contentWindow
					|| !this.iframe.contentWindow.document
					|| !this.iframe.contentWindow.document.body
					|| !this.iframe.contentWindow.document.body.scrollHeight)
					return false;

				let height = Math.max(this.iframe.contentWindow.document.body.scrollHeight,
					this.body.offsetHeight) + "px";
				if (this.iframe.height !== height)
				{
					this.iframe.height = "0";
					this.iframe.height = height;
				}
				return true;
			};

			resize();
			window.addEventListener("refresh_size", resize);
			this.iframe.backgroundImage = "none";
		});
	}

	connectedCallback()
	{
		super.connectedCallback();
	}

	get iframe()
	{
		return this._private.iframe;
	}

	set navigator(navigator)
	{
		if (navigator && navigator.length > 1)
		{
			let navbar = new GNavBar(navigator, navigator.target);
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
				this.arguments[i].dispatchEvent(new CustomEvent('changed', {bubbles: true}));

		this.hide();
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static set caption(caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.caption = caption;
	}

	static set commands(commands)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.commands = commands;
	}

	static get caption()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.caption;
	}

	static get commands()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.commands;
	}

	static create()
	{
		return 	document === window.top.document ? new GDialog()
			: window.top.document.importNode(new GDialog());
	}
}

customElements.define('g-dialog', GDialog);

window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-get]");

	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		var parameters = CSV.parse(action.getAttribute('data-get')).map(e => e.trim())
			.map(e => e !== null ? document.getElementById(e) : null);
		if (parameters.some(e => e && e.value))
		{
			parameters = parameters.filter(e => e && e.value);
			parameters.forEach(e => e.value = "");
			parameters.forEach(e => e.dispatchEvent(new Event('change', {bubbles: true})));
		} else {
			let dialog = GDialog.create();
			dialog.target = action.href;
			dialog.caption = action.getAttribute("title");
			dialog.get.apply(dialog, parameters);
		}
	}
});


window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");

	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		var ret = CSV.parse(action.getAttribute("data-ret")).map(e => e.trim());
		window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
	}
});

window.addEventListener("mouseover", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");

	if (action)
		action.focus();
});

window.addEventListener("mouseout", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");

	if (action)
		action.blur();
});

window.addEventListener("keydown", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");

	if (action && event.keyCode === 13)
		action.click();
});

window.addEventListener("change", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;

	if (action.tagName === "INPUT" && action.hasAttribute("data-getter"))
	{
		event.preventDefault();
		event.stopPropagation();

		var getter = document.getElementById(action.getAttribute("data-getter"));
		var url = resolve(getter.href);
		var parameters = CSV.parse(getter.getAttribute('data-get')).map(id => id.trim())
			.map(id => id !== null ? document.getElementById(id) : null);
		if (action.value)
		{
			parameters.filter(e => e).filter(e => e.value).forEach(e => e.value = "");
			let dialog = GDialog.create();
			dialog.target = url;
			dialog.caption = getter.getAttribute("title");
			dialog.get.apply(dialog, parameters);
			dialog.get.apply(dialog, parameters);
		} else
			parameters.filter(e => e).filter(e => e.value).forEach(e => e.value = "");
	}
});

window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("a.Hide");

	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		if (window.frameElement
			&& window.frameElement.dialog
			&& window.frameElement.dialog.hide)
			window.frameElement.dialog.hide();
		else
			window.close();
	}
});