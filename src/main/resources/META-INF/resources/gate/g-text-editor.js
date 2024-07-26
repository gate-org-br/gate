let template = document.createElement("template");
template.innerHTML = `
	<g-text-editor-toolbar id='toolbar'>
	</g-text-editor-toolbar>
	<div id='scroll'>
		<div id='editor' tabindex="1" contentEditable='true'></div>
	</div>
 <style data-element="g-text-editor">:host(*)
{
	width: 100%;
	height: 100%;
	display: grid;
	border-radius: 5px;
	line-height: normal;
	border: 1px solid #f2f2f2;
	grid-template-rows: 60px 1fr;
}

:host([hidden])
{
	display:  none;
}

#scroll {
	display: flex;
	overflow: auto;
	align-items: stretch;
	justify-content: stretch;
}

#editor
{
	flex-grow: 1;
	padding: 12px;
	outline: none;
	overflow: auto;
	font-size: 16px;
	white-space: pre-wrap;
	background-color: white;
	border-radius: 0 0 5px 5px;
}

#editor:focus {
	outline: none;
}


#editor > div {
	padding: 8px;
	display: flex;
	overflow: auto;
	margin: 4px 0 4px 0;
	height: fit-content;
	align-items: stretch;
	justify-content: center;
	border: 1px solid #EFEFEF;
	background-color: var(--hovered);
}

#editor > div:hover
{
	outline: dotted;
	resize: vertical;
	outline-width: 1px;
}</style>`;
/* global customElements */

import './g-text-editor-toolbar.js';

const LINE_BREAK = '\n';

function equals(node1, node2)
{
	return node1
		&& node2
		&& node1.tagName
		&& node2.tagName
		&& node1.style.fontWeight === node2.style.fontWeight
		&& node1.style.fontStyle === node2.style.fontStyle
		&& node1.style.fontSize === node2.style.fontSize
		&& node1.style.fontFamily === node2.style.fontFamily
		&& node1.style.textDecoration === node2.style.textDecoration
		&& node1.style.color === node2.style.color
		&& node1.style.backgroundColor === node2.style.backgroundColor;
}

customElements.define('g-text-editor', class extends HTMLElement
{
	#internals;
	#undo = [];
	#redo = [];
	#selection;

	static formAssociated = true;

	constructor()
	{
		super();
		this.attachShadow({mode: "open", delegatesFocus: true});
		this.#internals = this.attachInternals();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		const editor = this.shadowRoot.getElementById("editor");

		this.addEventListener("focusin", () => editor.focus());
		editor.addEventListener('focusout', event => this.#selection = this.getSelection());
		editor.addEventListener('focusin', event => this.#selection && this.setSelection(this.#selection));

		editor.addEventListener('beforeinput', event =>
		{
			event.preventDefault();
			if (event.inputType === 'deleteContentForward')
			{
				this.mark();
				let range = this.getSelection();
				if (range.collapsed)
				{
					let node = range.startContainer;
					if (node instanceof Text
						&& range.endOffset < node.length)
						range.setEnd(node, range.endOffset + 1);
					else
					{
						node = node.nextSibling
							|| node?.parentNode.nextSibling;

						if (node instanceof Text)
						{
							range.setStart(node, 0);
							range.setEnd(node, Math.min(node.length, 1));
						} else if (node instanceof HTMLSpanElement)
						{
							node = node.firstChild;
							range.setStart(node, 0);
							range.setEnd(node, Math.min(node.length, 1));
						} else if (node instanceof HTMLDivElement)
							range.selectNode(node);
					}
				}
				range.deleteContents();
			} else if (event.inputType === 'deleteContentBackward')
			{
				this.mark();
				let range = this.getSelection();
				if (range.collapsed)
				{
					let node = range.startContainer;
					if (node instanceof Text && range.endOffset >= 1)
						range.setStart(node, range.startOffset - 1);
					else
					{
						node = node.previousSibling
							|| node?.parentNode.previousSibling;

						if (node instanceof Text)
						{
							range.setStart(node, Math.max(node.length - 1, 0));
							range.setEnd(node, node.length);
						} else if (node instanceof HTMLSpanElement)
						{
							node = node.firstChild;
							range.setStart(node, Math.max(node.length - 1, 0));
							range.setEnd(node, node.length);
						} else if (node instanceof HTMLDivElement)
							range.selectNode(node);
					}
				}
				range.deleteContents();
			} else if (event.inputType === 'insertParagraph')
				this.input(LINE_BREAK);
			else if (event.inputType === 'insertText')
				this.input(event.data);
			this.compact();
		});

		editor.addEventListener("paste", (event) =>
		{
			event.preventDefault();
			this.input(event.clipboardData.getData("text/plain"));
		});

		editor.addEventListener('keydown', event =>
		{
			if (!event.ctrlKey)
				return;

			if (event.key === "y" || event.key === "Y")
				this.redo();
			else if (event.key === "z" || event.key === "Z")
				this.undo();
			else if (event.key === "c" || event.key === "C")
				return;
			else if (event.key === "v" || event.key === "V")
				return;

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		});

		editor.addEventListener('keydown', event =>
		{

			let range = this.getSelection();
			if (range.startContainer !== range.endContainer)
				return;

			let target = range.startContainer;
			if (!target.closest)
				target = target.parentNode;
			target = target.closest("#editor > div");
			if (!target)
				return;

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();

			switch (event.key)
			{
				case "Delete":
					this.mark();
					target.remove();
					break;
				case "Enter":
					this.mark();
					if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
						target.previousSibling.textContent += LINE_BREAK;
					else
						target.parentNode.insertBefore(document.createTextNode(LINE_BREAK), target);
					break;
				case "Backspace":
					this.mark();
					let prev = target.previousSibling;
					if (prev.tagName === "SPAN" && prev.lastChild)
						prev = prev.lastChild;
					if (prev && prev.nodeType === Node.TEXT_NODE)
						if (prev.textContent.endsWith(LINE_BREAK))
							prev.textContent = prev.textContent.slice(0, -1);
					break;
				case 'ArrowLeft':
				case 'ArrowUp':
					this.mark();
					if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
						this.setSelection(target.previousSibling);
					else
						this.setSelection(target.parentNode.insertBefore(document.createTextNode(LINE_BREAK), target));
					break;

				case 'ArrowRight':
				case 'ArrowDown':
					this.mark();
					if (!target.nextSibling)
						this.setSelection(target.parentNode.appendChild(document.createTextNode(LINE_BREAK)));
					else if (target.nextSibling.nodeType === Node.TEXT_NODE)
						this.setSelection(target.nextSibling);
					else
						this.setSelection(target.parentNode.insertBefore(document.createTextNode(LINE_BREAK, target.nextSibling)));
					break;

			}
		});

		editor.addEventListener('keydown', event =>
		{
			switch (event.key)
			{
				case "Tab":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					let range = this.getSelection();
					range.collapse();
					let node = range.startContainer;

					if (node === editor
						|| node.tagName === "A"
						|| node.tagName === "SPAN"
						|| node.nodeType === Node.TEXT_NODE)
						this.input("\t");
					break;
			}
		});

		new MutationObserver(mutations =>
		{
			mutations.flatMap(e => Array.from(e.addedNodes))
				.filter(e => e instanceof HTMLDivElement)
				.forEach(node =>
				{
					node.addEventListener("click", event =>
					{
						event.stopPropagation();
						event.preventDefault();
						event.stopImmediatePropagation();
						this.setSelection(node);
					});
				});

			this.#update();
		}).observe(editor, {characterData: true, subtree: true, childList: true});

		this.#update();
	}

	#update()
	{
		this.#internals.setFormValue(this.value);
		this.#internals.setValidity({});
		if (this.required && !this.value)
			this.#internals.setValidity({valueMissing: true}, "This field is required");
		else if (this.maxlength && this.value.length > this.maxlength)
			this.#internals.setValidity({tooLong: true}, `This field must have max ${this.maxlength} characteres`);
		else if (this.pattern && this.value && !this.value.match(this.pattern))
			this.#internals.setValidity({patternMismatch: true}, `Enter the correct format`);
	}

	formatBold()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontWeight === "bold"))
			selection.forEach(e => e.style.fontWeight = "");
		else
			selection.forEach(e => e.style.fontWeight = "bold");
		this.compact();
	}

	formatItalic()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontStyle === "italic"))
			selection.forEach(e => e.style.fontStyle = "");
		else
			selection.forEach(e => e.style.fontStyle = "italic");
		this.compact();
	}

	formatUnderline()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.textDecoration === "underline"))
			selection.forEach(e => e.style.textDecoration = "");
		else
			selection.forEach(e => e.style.textDecoration = "underline");
		this.compact();
	}

	formatStrikeThrough()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.textDecoration === "line-through"))
			selection.forEach(e => e.style.textDecoration = "");
		else
			selection.forEach(e => e.style.textDecoration = "line-through");
		this.compact();
	}

	formatFontColor(value)
	{
		this.mark();
		this.getSelectedNodes().forEach(e => e.style.color = value);
		this.compact();
	}

	formatBackColor(value)
	{
		this.mark();
		this.getSelectedNodes().forEach(e => e.style.backgroundColor = value);
		this.compact();
	}

	formatRemove()
	{
		this.mark();
		this.getSelectedNodes().forEach(e => e.style = "");
		this.compact();
	}

	formatFontSize(value)
	{
		this.mark();
		this.getSelectedNodes().forEach(e => e.style.fontSize = value);
		this.compact();

	}

	formatFontName(value)
	{
		this.mark();
		this.getSelectedNodes().forEach(e => e.style.fontFamily = value);
		this.compact();
	}

	input(text)
	{
		if (!this.editor.innerHTML
			|| text.length > 1
			|| text === " "
			|| text === "\t"
			|| text === "\n")
			this.mark();
		let range = this.getSelection();
		const textNode = document.createTextNode(text);
		range.insertNode(textNode);
		range.setStart(textNode, text.length);
		range.setEnd(textNode, text.length);
		this.setSelection(range);
		this.editor.normalize();
	}

	attach(type, name, url)
	{
		this.mark();

		let range = this.getSelection();

		let html = null;
		if (type.startsWith("image"))
			html = `<div contenteditable='false'><img src="${url}"/></div>`;
		else if (type.startsWith("video"))
			html = `<div contenteditable='false'><video src="${url}" controls/></div>`;
		else if (type.startsWith("audio"))
			html = `<div contenteditable='false'><audio src="${url}" controls/></div>`;
		else
			html = `<div contenteditable='false'><a tabindex='-1' href="${url}" download="${name}">${name}</a></div>`;

		let element = range.createContextualFragment(html).childNodes[0];
		range.insertNode(element);
		if (element.parentNode !== this.editor)
		{
			element.parentNode.normalize();
			if (element.previousSibling)
			{
				let span = element.parentNode.cloneNode(false);
				span.appendChild(element.previousSibling);
				this.editor.insertBefore(span, element.parentNode);
			}
			this.editor.insertBefore(element, element.parentNode);
		}

		this.compact();
	}

	mark()
	{
		this.#undo.push(this.editor.innerHTML);
	}

	undo()
	{
		let value = this.#undo.pop();
		if (value !== undefined)
		{
			this.#redo.push(this.editor.innerHTML);
			this.editor.innerHTML = value;
		}
	}

	redo()
	{
		let value = this.#redo.pop();
		if (value !== undefined)
		{
			this.mark();
			this.editor.innerHTML = value;
		}
	}

	set hidden(value)
	{
		if (value)
			this.setAttribute("hidden", "true");
		else
			this.removeAttribute("hidden");
	}

	get hidden()
	{
		return this.hasAtribute("hidden");
	}

	getSelection()
	{
		let selection = this.shadowRoot.getSelection
			? this.shadowRoot.getSelection()
			: window.getSelection();

		if (selection
			&& selection.rangeCount
			&& this.editor.contains(selection.getRangeAt(0).commonAncestorContainer))
			return selection.getRangeAt(0);

		let range = document.createRange();
		range.setStart(this.editor, 0);
		range.setEnd(this.editor, 0);
		return range;
	}

	setSelection(range)
	{
		let selection = this.shadowRoot.getSelection
			? this.shadowRoot.getSelection()
			: window.getSelection();

		if (range.nodeType)
		{
			let element = range;
			range = document.createRange();
			range.setStart(element, 0);
			range.setEnd(element, 1);
		}

		selection.removeAllRanges();
		selection.addRange(range);
	}

	getSelectedNodes()
	{
		this.editor.focus();
		this.editor.normalize();

		let range = this.getSelection();
		let fragment = range.extractContents();
		let textNodes = Array.from(fragment.childNodes)
			.filter(e => e.nodeType === Node.TEXT_NODE || e.tagName === "SPAN" || e.tagName === "A")
			.flatMap(e => e.nodeType === Node.TEXT_NODE ? e : Array.from(e.childNodes));
		range.insertNode(fragment);

		textNodes.forEach(text =>
		{
			let parent = text.parentNode;
			if (parent === this.editor)
			{
				let span = document.createElement("span");
				text.replaceWith(span);
				span.appendChild(text);
			} else if (parent.childNodes.length > 1)
			{
				Array.from(parent.childNodes).forEach(textFragment =>
				{
					let span = parent.cloneNode(false);
					this.editor.insertBefore(span, parent);
					span.appendChild(textFragment);
				});
			}
		});

		return textNodes.map(e => e.parentNode);
	}

	compact()
	{

		this.editor.normalize();
		let spans = Array.from(this.editor.querySelectorAll("span, a"));
		for (let i = spans.length - 1; i >= 0; i--)
			if (equals(spans[i], spans[i].previousSibling))
				while (spans[i].firstChild)
					spans[i].previousSibling.appendChild(spans[i].firstChild);

		this.editor.normalize();
		Array.from(this.editor.querySelectorAll("span, a"))
			.filter(e => !e.hasAttribute("style") === ""
					|| e.getAttribute("style") === "")
			.forEach(e => e.outerHTML = e.innerHTML);

		this.editor.normalize();
		Array.from(this.editor.querySelectorAll("span, a"))
			.filter(e => e.innerText.trim() === "")
			.forEach(e => e.remove());

		this.editor.normalize();
		return this;
	}

	get value()
	{
		this.compact();
		return this.shadowRoot.getElementById("editor").innerHTML
			.trim()
			.replaceAll(LINE_BREAK, "<br>");
	}

	set value(value)
	{
		this.shadowRoot.getElementById("editor")
			.innerHTML = value
			.replaceAll(LINE_BREAK, "")
			.replaceAll("<br>", LINE_BREAK)
			.trim();
		this.compact();
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	get required()
	{
		return this.hasAttribute("required");
	}

	set required(required)
	{
		if (required)
			this.setAttribute("required", "");
		else
			this.removeAttribute("required", "");
	}

	get maxlength()
	{
		return Number(this.getAttribute("maxlength"));
	}

	set maxlength(maxlength)
	{
		this.setAttribute("maxlength", maxlength);
	}

	get pattern()
	{
		return this.getAttribute("pattern");
	}

	set pattern(pattern)
	{
		this.setAttribute("pattern", pattern);
	}

	get tabindex()
	{
		return this.getAttribute("tabindex");
	}

	set tabindex(tabindex)
	{
		this.setAttribute("tabindex", tabindex);
	}

	get toolbar()
	{
		return this.shadowRoot.getElementById("toolbar");
	}

	get editor()
	{
		return this.shadowRoot.getElementById("editor");
	}

	attributeChangedCallback()
	{
		this.value = this.getAttribute("value");
	}

	checkValidity()
	{
		return this.#internals.checkValidity();
	}

	reportValidity()
	{
		return this.#internals.reportValidity();
	}

	get validity()
	{
		return this.#internals.validity;
	}

	get validationMessage()
	{
		return this.#internals.validationMessage;
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});