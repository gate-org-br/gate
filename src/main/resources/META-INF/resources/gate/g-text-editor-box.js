let template = document.createElement("template");
template.innerHTML = `<slot></slot> <style>:host(*){
	padding: 8px;
	display: flex;
	overflow: auto;
	height: fit-content;
	align-items: stretch;
	justify-content: center;
}

:host(:focus), :host(:hover)
{
}

:host(:hover)
{
	resize: vertical;
	border: 1px solid #EFEFEF;
	background-color: var(--hovered);
}

:host(:focus)
{
	outline: dotted;
}</style>`;

/* global customElements */

function select(text)
{
	const range = document.createRange();
	range.setStart(text, 0);
	range.setEnd(text, 0);
	const selection = window.getSelection();
	selection.removeAllRanges();
	selection.addRange(range);
}

customElements.define('g-text-editor-box', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 1;
		this.attachShadow({mode: "open"});
		this.setAttribute("tabindex", "1");
		this.setAttribute("contenteditable", "false");
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener('click', event => event.stopPropagation() | this.focus());

		this.addEventListener('keydown', event =>
		{
			console.log(event.key);
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			if (event.key === "Enter")
			{
				if (this.previousSibling && this.previousSibling.nodeType === Node.TEXT_NODE)
					this.previousSibling.textContent += "\n";
				else
					this.parentNode.insertBefore(document.createTextNode("\n"), this);
			} else if (event.key === "Backspace")
			{
				let prev = this.previousSibling;
				if (prev.tagName === "SPAN" && prev.lastChild)
					prev = prev.lastChild;
				if (prev && prev.nodeType === Node.TEXT_NODE && prev.textContent.endsWith("\n"))
					prev.textContent = prev.textContent.slice(0, -1);
			} else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp')
			{
				if (this.previousSibling && this.previousSibling.nodeType === Node.TEXT_NODE)
					select(this.previousSibling);
				else
					select(this.parentNode.insertBefore(document.createTextNode("\u200B"), this));
			} else if (event.key === 'ArrowRight' || event.key === 'ArrowDown')
			{
				if (!this.nextSibling)
					select(this.parentNode.appendChild(document.createTextNode("\u200B")));
				else if (this.nextSibling.nodeType === Node.TEXT_NODE)
					select(this.nextSibling);
				else
					select(this.parentNode.insertBefore(document.createTextNode("\u200B"), this.nextSibling));
			} else if (event.key === "Delete")
				this.remove();
		});
	}

	connectedCallback()
	{
//		if (this.firstElementChild)
//		{
//			this.firstElementChild.tabIndex = 1;
//			this.shadowRoot.appendChild(this.firstElementChild);
//		}
	}
});