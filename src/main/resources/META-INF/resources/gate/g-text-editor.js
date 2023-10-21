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
	display: flex;
	position: relative;
	border-radius: 5px;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
	border: 1px solid #f2f2f2;
}

:host([hidden])
{
	display:  none;
}

#scroll {
	top: 60px;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	overflow: auto;
	position: absolute;
	align-items: stretch;
	justify-content: stretch;
}

#editor
{
	flex-grow: 1;
	padding: 8px;
	outline: none;
	overflow: auto;
	font-size: 16px;
	line-height: 20px;
	white-space: pre-wrap;
	background-color: white;
	border-radius: 0 0 5px 5px;
}

#editor:focus {
	outline: none;
}

i {
	font-family: gate;
	font-style: normal;
}


span.callout
{
	gap: 12px;
	display: flex;
	align-items: center;

	padding: 12px;
	font-size: 16px;
	text-align: justify;

	border-radius: 0 3px 3px 0;
	background-color: var(--main1);

	border: 1px solid;
	border-left: 6px solid;
	border-color: var(--main6);
}

span.callout.fill {
	color: #000000;
	border-color: #000000;
	background-color: var(--main4);
}


span.warning
{
	color: var(--warning1);
	border-color: var(--warning1);
}

span.warning.fill {
	background-color: var(--warning3);
}

span.danger
{
	color: var(--error1);
	border-color: var(--error1);
}

span.danger.fill {
	background-color: var(--error3);
}

span.success {
	color: var(--success1);
	border-color: var(--success1);
}

span.success.fill {
	background-color: var(--success3);
}

span.question {
	color: var(--question1);
	border-color: var(--question1);
}

span.question.fill {
	background-color: var(--question3);
}

span.title {
	display: block;
	font-size: 32px;
	text-align: center
}

span.subtitle {
	display: block;
	font-size: 24px;
	text-align: center
}

</style>`;

/* global customElements */

import './g-icon.js';
import './g-text-editor-box.js';
import './g-text-editor-toolbar.js';
import GTextSelection from './g-text-selection.js';

function compareStyles(element1, element2)
{
	if (!element1 || !element2)
		return false;
	if (element1.tagName !== element2.tagName)
		return false;

	const style1 = element1.getAttribute("style");
	const style2 = element2.getAttribute("style");

	if (style1 === null)
		return style2 === null;
	if (style2 === null)
		return style1 === null;

	const sortedStyle1 = style1.split(";").map(style => style.trim()).filter(Boolean).sort().join(";");
	const sortedStyle2 = style2.split(";").map(style => style.trim()).filter(Boolean).sort().join(";");
	return sortedStyle1 === sortedStyle2;
}

customElements.define('g-text-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 0;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let editor = this.shadowRoot.getElementById("editor");
		this.addEventListener("focus", () =>
		{
			let range = document.createRange();
			range.setStart(editor, 0);
			range.setEnd(editor, 0);
			let selection = window.getSelection();
			selection.removeAllRanges();
			selection.addRange(range);
		});

		editor.addEventListener('beforeinput', (event) => {
			if (event.inputType === 'insertParagraph')
			{
				event.preventDefault();

				const selection = this.shadowRoot.getSelection ?
					this.shadowRoot.getSelection() : window.getSelection();

				if (selection.rangeCount > 0)
				{
					const range = selection.getRangeAt(0);
					range.deleteContents();
					const textNode = document.createTextNode('\n\u200B');
					range.insertNode(textNode);
					range.setStartAfter(textNode);
					range.setEndAfter(textNode);
					selection.removeAllRanges();
					selection.addRange(range);
				}
			}
		});

		this.editor.addEventListener('keydown', event =>
		{
			if (event.key === 'ArrowDown'
				|| event.key === 'ArrowRight'
				|| event.key === 'ArrowUp'
				|| event.key === 'ArrowLeft')
			{
				const selection = this.shadowRoot.getSelection ?
					this.shadowRoot.getSelection() : window.getSelection();

				let range = selection.getRangeAt(0);
				let content = range.commonAncestorContainer;
				if (content.nodeType === Node.TEXT_NODE)
					content = content.parentNode;

				if (content.tagName === "SPAN")
				{

					let text;
					content.normalize();
					if ((event.key === 'ArrowUp' || event.key === 'ArrowLeft') && range.endOffset === 0)
					{
						if (content.previousSibling && content.previousSibling.nodeType === Node.TEXT_NODE)
							text = content.previousSibling;
						else
							text = content.parentNode.insertBefore(document.createTextNode("\u200B"), content);
					} else if ((event.key === 'ArrowDown' || event.key === 'ArrowRight')
						&& (!content.firstChild || content.firstChild.textContent.length === range.endOffset))
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
			}
		});
	}

	insertUnorderedList()
	{
		this.processor.list();

//		this.shadowRoot.getElementById("editor").focus();
//		document.execCommand("insertUnorderedList");
	}

	insertOrderedList()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("insertOrderedList");
	}

	createLink()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("createLink", null, prompt("Entre com a url"));
	}

	unlink()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("unlink");
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

	attach()
	{
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
						document.execCommand("insertHTML", null,
							`<g-text-editor-box><img src="${reader.result}"/></g-text-editor-box>`);
						break;
					case "video/mp4":
					case "video/webm":
					case "video/ogg":
						document.execCommand("insertHTML", null,
							`<g-text-editor-box><video src="${reader.result}" controls /></g-text-editor-box>`);
						break;
					case "audio/mp3":
					case "audio/ogg":
					case "audio/wav":
						document.execCommand("insertHTML", null,
							`<audio src="${reader.result}" controls />`);
						break;
					default:
						document.execCommand("insertHTML", null,
							`<a href="${reader.result}" download="${file.name}">${file.name}</a>`);
				}
			};
		});
		blob.click();
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

	get value()
	{
		return this.shadowRoot.getElementById("editor").innerHTML;
	}

	set value(value)
	{
		return this.shadowRoot.getElementById("editor").innerHTML = value;
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

	get selection()
	{
		this.editor.focus();
		let selection = this.shadowRoot.getSelection ?
			this.shadowRoot.getSelection()
			: window.getSelection();
		if (selection.rangeCount)
		{
			let range = selection.getRangeAt(0);
			if (this.editor.contains(range.commonAncestorContainer))
			{
				if (range.commonAncestorContainer.tagName === "SPAN"
					&& range.commonAncestorContainer.childNodes.length === 1)
					range.selectNode(range.commonAncestorContainer);
				else if (range.commonAncestorContainer.nodeType === Node.TEXT_NODE
					&& range.commonAncestorContainer.parentNode.tagName === "SPAN"
					&& range.commonAncestorContainer.parentNode.childNodes.length === 1)
					range.selectNode(range.commonAncestorContainer.parentNode);
				return new GTextSelection(this, range);
			}
		}

		return new GTextSelection(this, null);
	}

	compact()
	{
		Array.from(this.editor.querySelectorAll("span"))
			.filter(e => compareStyles(e, e.nextElementSibling))
			.forEach(e => e.childNodes.forEach(node => e.nextElementSibling.appendChild(node)));

		Array.from(this.editor.querySelectorAll("span"))
			.filter(e => e.innerText.trim() === "")
			.forEach(e => e.remove());

		this.editor.normalize();
		return this;
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