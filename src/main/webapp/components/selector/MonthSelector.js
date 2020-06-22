/* global customElements */

class MonthSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		var date = new Date();
		var months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
		this._private.m = this.appendChild(new GSlider(date.getMonth(), e => e > 0 ? e - 1 : 11, e => e < 11 ? e + 1 : 0, e => months[e]));
		this._private.m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		this._private.y = this.appendChild(new GSlider(date.getFullYear(), e => e - 1, e => e + 1, e => "0000".concat(String(e)).slice(-4)));
		this._private.y.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return "00".concat(String(this._private.m.value() + 1)).slice(-2) + "/" + "0000".concat(String(this._private.y.value())).slice(-4);
	}
}

customElements.define('g-month-selector', MonthSelector);