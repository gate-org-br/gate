let template = document.createElement("template");
template.innerHTML = `
	<g-text-editor-toolbar id='toolbar'>
	</g-text-editor-toolbar>
	<div id='scroll'>
		<div id='editor' tabindex="0" contentEditable='true'></div>
	</div>
	<input id='value' type="hidden"/>
 <style>:host(*)
{
	width: 100%;
	height: 100%;
	display: grid;
	border-radius: 5px;
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
	height: fit-content;
	align-items: stretch;
	justify-content: center;
}

#editor > div:hover
{
	resize: vertical;
	border: 1px solid #EFEFEF;
	background-color: var(--hovered);
}

#editor > div:focus
{
	outline: dotted;
}

#editor > div > *
{
	height: 100%;
}</style>`;

/* global customElements */

import './g-text-editor-toolbar.js';

const LINE_BREAK = '\n\u200B';

customElements.define('g-text-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 0;
		this.attachShadow({mode: "open"});
		this._private = {undo: [], redo: []};
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		const editor = this.shadowRoot.getElementById("editor");

		editor.addEventListener('focusout', event => this._private.selection = this.getSelection());

		editor.addEventListener('focusin', event =>
		{
			let selection = this.getSelection();
			if (this._private.selection)
				this.setSelection(this._private.selection);
		});

		editor.addEventListener('beforeinput', event =>
		{
			event.preventDefault();
			if (event.inputType === 'deleteContentForward')
			{
				this.mark();
				let range = this.getSelection();
				if (range.collapsed && range.endOffset < event.currentTarget.innerText.length)
					range.setEnd(range.startContainer, range.endOffset + 1);
				range.deleteContents();
			} else if (event.inputType === 'deleteContentBackward')
			{
				this.mark();
				let range = this.getSelection();
				if (range.collapsed && range.startOffset)
					range.setStart(range.startContainer, range.startOffset - 1);
				range.deleteContents();
			} else if (event.inputType === 'insertParagraph')
				this.input(LINE_BREAK);
			else if (event.inputType === 'insertText')
				this.input(event.data);

		});

		editor.addEventListener("paste", (event) =>
		{
			event.preventDefault();
			this.input(event.clipboardData.getData("text/plain"));
		});

		editor.addEventListener('keydown', event =>
		{
			if (event.ctrlKey)
			{
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
			}

		});

		editor.addEventListener('keydown', event =>
		{
			if (event.target.tagName === "DIV"
				&& event.target.parentNode === editor)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				let target = event.target;
				switch (event.key)
				{
					case "Enter":
						this.mark();
						if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
							target.previousSibling.textContent += LINE_BREAK;
						else
							target.parentNode.insertBefore(document.createTextNode(LINE_BREAK), target);
						break;
					case "Delete":
						this.mark();
						target.remove();
						break;
					case "Backspace":
						let prev = target.previousSibling;
						if (prev.tagName === "SPAN" && prev.lastChild)
							prev = prev.lastChild;
						if (prev && prev.nodeType === Node.TEXT_NODE)
							if (prev.textContent.endsWith(LINE_BREAK))
								prev.textContent = prev.textContent.slice(0, -2);
							else if (prev.textContent.endsWith("\n"))
								prev.textContent = prev.textContent.slice(0, -1);
						break;
					case 'ArrowLeft':
					case 'ArrowUp':
						if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
							this.setSelection(target.previousSibling);
						else
							this.setSelection(target.parentNode.insertBefore(document.createTextNode(LINE_BREAK), target));
						break;

					case 'ArrowRight':
					case 'ArrowDown':
						if (!target.nextSibling)
							this.setSelection(target.parentNode.appendChild(document.createTextNode(LINE_BREAK)));
						else if (target.nextSibling.nodeType === Node.TEXT_NODE)
							this.setSelection(target.nextSibling);
						else
							this.setSelection(target.parentNode.insertBefore(document.createTextNode(LINE_BREAK, target.nextSibling)));
						break;

				}
			}
		});

		editor.addEventListener('keydown', event =>
		{
			let range = this.getSelection();
			let content = range.commonAncestorContainer;
			switch (event.key)
			{
				case "Tab":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					if (content === editor
						|| content.tagName === "A"
						|| content.tagName === "SPAN"
						|| content.nodeType === Node.TEXT_NODE)
						this.input("\t");
					break;
				case 'ArrowUp':
				case 'ArrowLeft':
					if (content.nodeType === Node.TEXT_NODE)
						content = content.parentNode;
					if (content.tagName === "SPAN" || content.tagName === "A")
					{
						let text;
						content.normalize();
						if (content.previousSibling && content.previousSibling.nodeType === Node.TEXT_NODE)
							text = content.previousSibling;
						else
							text = content.parentNode.insertBefore(document.createTextNode(LINE_BREAK), content);
						range.setStart(text, 0);
						range.setEnd(text, 0);
					}
					break;
				case 'ArrowDown':
				case 'ArrowRight':
					if (content.nodeType === Node.TEXT_NODE)
						content = content.parentNode;
					if (content.tagName === "SPAN" || content.tagName === "A")
					{
						let text;
						content.normalize();
						if (!content.firstChild || content.firstChild.textContent.length === range.endOffset)
						{
							if (!content.nextSibling)
								text = content.parentNode.appendChild(document.createTextNode(LINE_BREAK));
							else if (content.nextSibling.nodeType === Node.TEXT_NODE)
								text = content.nextSibling;
							else
								text = content.parentNode.insertBefore(document.createTextNode(LINE_BREAK), content.nextSibling);
						}

						range.setStart(text, 0);
						range.setEnd(text, 0);
					}
					break;

			}
		});
	}

	formatBold()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontWeight === "700"))
			selection.forEach(e => e.style.fontWeight = "400");
		else
			selection.forEach(e => e.style.fontWeight = "700");
		this.compact();
	}

	formatItalic()
	{
		this.mark();
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontStyle === "italic"))
			selection.forEach(e => e.style.fontStyle = "normal");
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

	formatJustifyLeft()
	{
		this.mark();
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "inline-block";
			e.style.textAlign = "left";
		});
		this.compact();
	}

	formatJustifyCenter()
	{
		this.mark();
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "inline-block";
			e.style.textAlign = "center";
		});
		this.compact();
	}

	formatJustifyRight()
	{
		this.mark();
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "inline-block";
			e.style.textAlign = "right";
		});
		this.compact();
	}

	formatJustifyFull()
	{
		this.mark();
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "inline-block";
			e.style.textAlign = "justify";
		});
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
			html = `<div tabindex='1' contenteditable='false'><img src="${url}"/></div>`;
		else if (type.startsWith("video"))
			html = `<div tabindex='1' contenteditable='false'><video src="${url}" controls/></div>`;
		else if (type.startsWith("audio"))
			html = `<div tabindex='1' contenteditable='false'><audio src="${url}" controls/></div>`;
		else
			html = `<a href="${reader.result}" download="${file.name}">${file.name}</a>`;

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
		this._private.undo.push(this.editor.innerHTML);
	}

	undo()
	{
		let value = this._private.undo.pop();
		if (value !== undefined)
		{
			this._private.redo.push(this.editor.innerHTML);
			this.editor.innerHTML = value;
		}
	}

	redo()
	{
		let value = this._private.redo.pop();
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

	connectedCallback()
	{
		let form = this.closest("form");
		if (form)
		{
			form.addEventListener("submit", event =>
			{
				if (this.required && !this.value)
					this.setCustomValidity("Please fill out this field");
				else if (this.maxlength && this.value.length > this.maxlength)
					this.setCustomValidity("The specified value exceeds max length");
				else if (this.pattern && this.value && !this.value.match(this.pattern))
					this.setCustomValidity("The specified value is not valid");
				else
					this.setCustomValidity("");
				if (!this.reportValidity())
					event.preventDefault();
			});
			form.addEventListener("formdata", event => event.formData.set(this.name, this.value));
		}
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

		if (range.nodeType === Node.TEXT_NODE)
		{
			let element = range;
			range = document.createRange();
			range.setStart(element, 0);
			range.setEnd(element, 0);
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
		Array.from(this.editor.querySelectorAll("span"))
			.filter(e => e.innerText.trim() === "")
			.forEach(e => e.remove());
		Array.from(this.editor.querySelectorAll("a"))
			.filter(e => e.innerText.trim() === "")
			.forEach(e => e.remove());
		this.editor.normalize();
		return this;
	}

	get value()
	{
		this.compact();
		return this.shadowRoot.getElementById("editor").innerHTML;
	}

	set value(value)
	{
		this.shadowRoot.getElementById("editor").innerHTML = value;
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
		return this.getAttribute("required");
	}

	set required(required)
	{
		this.setAttribute("required", required);
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

	static get observedAttributes()
	{
		return ["value"];
	}
});