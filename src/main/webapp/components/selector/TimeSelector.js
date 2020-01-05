/* global customElements */

class TimeSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		this._private.h = this.appendChild(new Slider(0, e => (e + 23) % 24, e => (e + 1) % 24, e => "00".concat(String(e)).slice(-2), 5));
		this._private.h.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		this._private.m = this.appendChild(new Slider(0, e => (e + 50) % 60, e => (e + 10) % 60, e => "00".concat(String(e)).slice(-2), 5));
		this._private.m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return "00".concat(String(this._private.h.value())).slice(-2) + ":" + "00".concat(String(this._private.m.value())).slice(-2);
	}
}

customElements.define('g-time-selector', TimeSelector);