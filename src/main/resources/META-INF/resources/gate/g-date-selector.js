let template = document.createElement("template");
template.innerHTML = `
	<g-calendar max="1">
	</g-calendar>
 <style data-element="g-date-selector">:host(*)
{
	display:flex;
	align-items: stretch;
	align-content: stretch;
	justify-content: center;
}

g-calendar
{
	flex-grow: 1;
}</style>`;
/* global customElements, template */

import './g-calendar.js';
import DateFormat from './date-format.js';

customElements.define('g-date-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let calendar = this.shadowRoot.firstElementChild;
		calendar.addEventListener("remove", event => event.preventDefault());
		calendar.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		let calendar = this.shadowRoot.firstElementChild;
		let selection = calendar.selection();
		return selection.length ? DateFormat.DATE.format(selection[0]) : null;
	}

	set date(value)
	{
		let calendar = this.shadowRoot.firstElementChild;
		calendar.clear();
		calendar.select(value);
	}

	get date()
	{
		let calendar = this.shadowRoot.firstElementChild;
		let selection = calendar.selection();
		return selection.length ? selection[0] : null;
	}
});