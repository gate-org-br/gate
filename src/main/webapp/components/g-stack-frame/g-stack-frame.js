/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GStackFrame extends GModal
{
	constructor()
	{
		super();

		this._private.iframe = window.top.document.createElement('iframe');

		this.iframe.dialog = this;
		this.iframe.scrolling = "no";
		this.iframe.setAttribute('name', '_stack');
		this.iframe.onmouseenter = () => this.iframe.focus();

		this.iframe.addEventListener("load", () =>
		{
			this.iframe.name = "_frame";
			this.iframe.setAttribute("name", "_frame");
			this.iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
			this.iframe.backgroundImage = "none";
		});

	}

	connectedCallback()
	{
		super.connectedCallback();
		this.appendChild(this._private.iframe);
	}

	get iframe()
	{
		return this._private.iframe;
	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}
}

customElements.define('g-stack-frame', GStackFrame);