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

		this.addEventListener("focus", () => this.getSelection().collapse(editor));

		editor.addEventListener('beforeinput', event =>
		{
			const selection = this.getSelection();
			if (!selection || !selection.rangeCount)
				return;

			let range = selection.getRangeAt(0);
			if (!this.editor.contains(range.commonAncestorContainer))
				return;

			event.preventDefault();
			if (event.inputType === 'insertParagraph')
			{
				this._private.undo.push(this.editor.innerHTML);

				const textNode = document.createTextNode('\n\u200B');
				range.insertNode(textNode);
				range.setStartAfter(textNode);
				range.setEndAfter(textNode);
				selection.removeAllRanges();
				selection.addRange(range);
			} else if (event.inputType === 'insertText')
			{
				if (!this.editor.innerHTML || event.data === " ")
					this._private.undo.push(this.editor.innerHTML);

				const textNode = document.createTextNode(event.data);
				range.insertNode(textNode);
				range.setStart(textNode, event.data.length);
				range.setEnd(textNode, event.data.length);
				selection.removeAllRanges();
				selection.addRange(range);
				event.currentTarget.normalize();
			} else if (event.inputType === 'deleteContentForward')
			{
				this._private.undo.push(this.editor.innerHTML);

				if (range.collapsed && range.endOffset < event.currentTarget.innerText.length)
					range.setEnd(range.startContainer, range.endOffset + 1);
				range.deleteContents();
			} else if (event.inputType === 'deleteContentBackward')
			{
				this._private.undo.push(this.editor.innerHTML);

				if (range.collapsed && range.startOffset)
					range.setStart(range.startContainer, range.startOffset - 1);
				range.deleteContents();
			}
		});

		editor.addEventListener('keydown', event =>
		{
			if (event.ctrlKey)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				switch (event.key)
				{
					case "y":
						this.redo();
						break;
					case "z":
						this.undo();
						break;
				}
			}

		});

		editor.addEventListener('keydown', event =>
		{
			if (event.target.tagName === "DIV" && event.target.parentNode === editor)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				let target = event.target;
				switch (event.key)
				{
					case "Enter":
						if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
							target.previousSibling.textContent += "\n";
						else
							target.parentNode.insertBefore(document.createTextNode("\n"), target);
						break;
					case "Delete":
						target.remove();
						break;
					case "Backspace":
						let prev = target.previousSibling;
						if (prev.tagName === "SPAN" && prev.lastChild)
							prev = prev.lastChild;
						if (prev && prev.nodeType === Node.TEXT_NODE && prev.textContent.endsWith("\n"))
							prev.textContent = prev.textContent.slice(0, -1);
						break;
					case 'ArrowLeft':
					case 'ArrowUp':
						if (target.previousSibling && target.previousSibling.nodeType === Node.TEXT_NODE)
							select(target.previousSibling);
						else
							select(target.parentNode.insertBefore(document.createTextNode("\u200B"), target));
						break;

					case 'ArrowRight':
					case 'ArrowDown':
						if (!target.nextSibling)
							select(target.parentNode.appendChild(document.createTextNode("\u200B")));
						else if (target.nextSibling.nodeType === Node.TEXT_NODE)
							select(target.nextSibling);
						else
							select(target.parentNode.insertBefore(document.createTextNode("\u200B"), target.nextSibling));
						break;

				}
			}
		});

		editor.addEventListener('keydown', event =>
		{
			const selection = this.getSelection();
			if (!selection || !selection.rangeCount)
				return;

			let range = selection.getRangeAt(0);
			if (!this.editor.contains(range.commonAncestorContainer))
				return;

			let content = range.commonAncestorContainer;
			switch (event.key)
			{
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
							text = content.parentNode.insertBefore(document.createTextNode("\u200B"), content);
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
								text = content.parentNode.appendChild(document.createTextNode("\u200B"));
							else if (content.nextSibling.nodeType === Node.TEXT_NODE)
								text = content.nextSibling;
							else
								text = content.parentNode.insertBefore(document.createTextNode("\u200B"), content.nextSibling);
						}

						range.setStart(text, 0);
						range.setEnd(text, 0);
					}
					break;
				case "Tab":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					if (content === editor
						|| content.tagName === "A"
						|| content.tagName === "SPAN"
						|| content.nodeType === Node.TEXT_NODE)
					{
						this._private.undo.push(this.editor.innerHTML);

						range.insertNode(document.createTextNode("\t"));
						range.collapse();
						selection.removeAllRanges();
						selection.addRange(range);
					}
					break;
			}
		});
	}

	formatBold()
	{
		this._private.undo.push(this.editor.innerHTML);
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontWeight === "700"))
			selection.forEach(e => e.style.fontWeight = "400");
		else
			selection.forEach(e => e.style.fontWeight = "700");
		this.compact();
	}

	formatItalic()
	{
		this._private.undo.push(this.editor.innerHTML);
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.fontStyle === "italic"))
			selection.forEach(e => e.style.fontStyle = "normal");
		else
			selection.forEach(e => e.style.fontStyle = "italic");
		this.compact();
	}

	formatUnderline()
	{
		this._private.undo.push(this.editor.innerHTML);
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.textDecoration === "underline"))
			selection.forEach(e => e.style.textDecoration = "");
		else
			selection.forEach(e => e.style.textDecoration = "underline");
		this.compact();
	}

	formatStrikeThrough()
	{
		this._private.undo.push(this.editor.innerHTML);
		let selection = this.getSelectedNodes();
		if (selection.every(e => e.style.textDecoration === "line-through"))
			selection.forEach(e => e.style.textDecoration = "");
		else
			selection.forEach(e => e.style.textDecoration = "line-through");
		this.compact();
	}

	formatFontColor(value)
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e => e.style.color = value);
		this.compact();
	}

	formatBackColor(value)
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e => e.style.backgroundColor = value);
		this.compact();
	}

	formatJustifyLeft()
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "block";
			e.style.textAlign = "left";
		});
		this.compact();
	}

	formatJustifyCenter()
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "block";
			e.style.textAlign = "center";
		});
		this.compact();
	}

	formatJustifyRight()
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "block";
			e.style.textAlign = "right";
		});
		this.compact();
	}

	formatJustifyFull()
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e =>
		{
			e.style.display = "block";
			e.style.textAlign = "justify";
		});
		this.compact();
	}

	formatRemove()
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e => e.style = "");
		this.compact();
	}

	formatFontSize(value)
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e => e.style.fontSize = value);
		this.compact();

	}

	formatFontName(value)
	{
		this._private.undo.push(this.editor.innerHTML);
		this.getSelectedNodes().forEach(e => e.style.fontFamily = value);
		this.compact();
	}

	attach()
	{
		const selection = this.getSelection();
		if (!selection || !selection.rangeCount)
			return;

		let range = selection.getRangeAt(0);
		if (!this.editor.contains(range.commonAncestorContainer))
			return;

		this._private.undo.push(this.editor.innerHTML);

		let blob = document.createElement("input");
		blob.setAttribute("type", "file");

		blob.addEventListener("change", () =>
		{
			let file = blob.files[0];
			let reader = new FileReader();
			reader.readAsDataURL(file);
			reader.onloadend = () =>
			{
				this.shadowRoot.getElementById("editor").focus();
				switch (file.type)
				{
					case "image/jpeg":
					case "image/png":
					case "image/svg+xml":
						range.insertNode(range.createContextualFragment(`<div tabindex='1' contenteditable='false'><img src="${reader.result}"/></div>`));
						break;
					case "video/mp4":
					case "video/webm":
					case "video/ogg":
						range.insertNode(range.createContextualFragment(`<div tabindex='1' contenteditable='false'><video src="${reader.result}" controls/></div>`));
						break;
					case "audio/mp3":
					case "audio/ogg":
					case "audio/wav":
						range.insertNode(range.createContextualFragment(`<div tabindex='1' contenteditable='false'><audio src="${reader.result}" controls/></div>`));
						break;
					default:
						range.insertNode(range.createContextualFragment(`<a href="${reader.result}" download="${file.name}">${file.name}</a>`));
						break;
				}
			};
		});

		blob.click();
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
			this._private.undo.push(this.editor.innerHTML);
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
		this.editor.focus();
		this.editor.normalize();
		return this.shadowRoot.getSelection
			? this.shadowRoot.getSelection()
			: window.getSelection();
	}

	getSelectedNodes()
	{
		const selection = this.getSelection();
		if (!selection || !selection.rangeCount)
			return [];

		let range = selection.getRangeAt(0);
		if (!this.editor.contains(range.commonAncestorContainer))
			return [];

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