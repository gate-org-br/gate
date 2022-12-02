/* global Intl, NaN */

const DEFAULT_LOCALE = document.documentElement.lang || navigator.language;

export default class NumberFormat
{
	constructor(locale = DEFAULT_LOCALE)
	{
		const parts = new Intl.NumberFormat(locale).formatToParts(12345.6);
		const numerals = [...new Intl.NumberFormat(locale, {useGrouping: false}).format(9876543210)].reverse();
		const index = new Map(numerals.map((d, i) => [d, i]));
		this._locale = locale;
		this._group = new RegExp(`[${parts.find(d => d.type === "group").value}]`, "g");
		this._decimal = new RegExp(`[${parts.find(d => d.type === "decimal").value}]`);
		this._numeral = new RegExp(`[${numerals.join("")}]`, "g");
		this._index = d => index.get(d);
	}
	parse(string)
	{
		return (string = string.trim()
			.replace(this._group, "")
			.replace(this._decimal, ".")
			.replace(this._numeral, this._index)) ? +string : NaN;
	}

	format(number)
	{
		return number.toLocaleString(this._locale);
	}
}