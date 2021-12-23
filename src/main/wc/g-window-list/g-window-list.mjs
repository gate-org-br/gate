/* global customElements */

export default class GWindowList extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.windows = [];
	}

	static get instance()
	{
		if (!GWindowList._private)
			GWindowList._private = {};
		if (!GWindowList._private.instance)
			GWindowList._private.instance =
				window.top.document.documentElement
				.appendChild(new WindowList());
		return GWindowList._private.instance;
	}
}

customElements.define('g-window-list', GWindowList);