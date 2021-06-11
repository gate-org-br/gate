/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class WindowList extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.windows = [];
	}

	static get instance()
	{
		if (!WindowList._private)
			WindowList._private = {};
		if (!WindowList._private.instance)
			WindowList._private.instance =
				window.top.document.documentElement
				.appendChild(new WindowList());
		return WindowList._private.instance;
	}
}

customElements.define('g-window-list', WindowList);