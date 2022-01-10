/* global customElements */

import Scroll from './scroll.mjs';
import WindowList from './g-window-list.mjs';

export default class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.addEventListener("keydown", event => event.stopPropagation());
		this.addEventListener("keypress", event => event.stopPropagation());
	}

	show()
	{

		if (window.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			Scroll.disable(window.top.document.documentElement);

			window.top.document.documentElement.appendChild(this);

			this.dispatchEvent(new CustomEvent('show', {detail: {modal: this}}));
		}

		return this;
	}

	hide()
	{
		if (this.parentNode
			&& window.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}}))
			&& this.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
		{
			Scroll.enable(window.top.document.documentElement);
			this.parentNode.removeChild(this);
		}

		return this;
	}

	minimize()
	{
		if (!this.parentNode)
			return;

		this.style.display = "none";
		WindowList.instance.add(this);
		Scroll.enable(window.top.document.documentElement);
	}

	maximize()
	{
		this.style.display = "";
		Scroll.disable(window.top.document.documentElement);
	}

}

customElements.define('g-modal', GModal);