/* global customElements */

import WindowList from './g-window-list.mjs';

const PREVENT_BODY_SCROLL = e => e.preventDefault();

const disableScroll = function (element)
{
	element.setAttribute("data-scroll-disabled", "data-scroll-disabled");
	Array.from(element.children).forEach(e => disableScroll(e));
	window.top.document.documentElement.addEventListener("touchmove", PREVENT_BODY_SCROLL, false);
};

const enableScroll = function (element)
{
	element.removeAttribute("data-scroll-disabled");
	Array.from(element.children).forEach(e => enableScroll(e));
	window.top.document.documentElement.removeEventListener("touchmove", PREVENT_BODY_SCROLL, false);
};

export default class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		this.addEventListener("contextmenu", event =>
		{
			event.preventDefault();
			this.hide();
		});

		this.addEventListener("keypress", event => event.stopPropagation());
		this.addEventListener("keydown", event => event.stopPropagation());

		this.addEventListener("click", event => !this.blocked
				&& (event.target === this || event.srcElement === this)
				&& this.hide());
	}

	connectedCallback()
	{
		this.classList.add("g-modal");
	}

	get blocked()
	{
		return this._private.blocked || false;
	}

	set blocked(blocked)
	{
		this._private.blocked = blocked;
	}

	show()
	{

		if (window.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			disableScroll(window.top.document.documentElement);

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
			enableScroll(window.top.document.documentElement);
			this.parentNode.removeChild(this);
		}

		return this;
	}

	minimize()
	{
		if (!this.parentNode)
			return;

		this.style.display = "none";
		enableScroll(window.top.document.documentElement);

		var link = WindowList.instance.appendChild(document.createElement("a"));
		link.href = "#";
		link.innerHTML = this.caption || "Janela";

		link.addEventListener("click", () =>
		{
			this.style.display = "";
			link.parentNode.removeChild(link);
			disableScroll(window.top.document.documentElement);
		});
	}
}

customElements.define('g-modal', GModal);