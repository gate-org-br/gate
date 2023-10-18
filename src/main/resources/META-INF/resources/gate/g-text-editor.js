let template = document.createElement("template");
template.innerHTML = `
	<g-text-editor-toolbar id='toolbar'>
	</g-text-editor-toolbar>
	<div id='scroll'>
		<div id='editor' tabindex="0" contentEditable='true'>
		</div>
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
	line-height: 18px;
	background-color: white;
	border-radius: 0 0 5px 5px;
}

#editor:focus {
	outline: none;
}

i {
	font-family: gate;
	font-style: normal;
}</style>`;

/* global customElements */

import './g-icon.js';
import './g-text-editor-box.js';
import './g-text-editor-toolbar.js';
import GIconPicker from './g-icon-picker.js';
import * as processor from './g-text-processor.js';

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
	}

	bold()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.toggle(editor, "font-weight", "700", "400");
	}

	italic()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.toggle(editor, "font-style", "italic", "normal");
	}

	textDecoration(value)
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "text-decoration-line", value);
	}

	fontSize(value)
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "font-size", value);
	}

	foreColor(value)
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "color", value);
	}

	removeFormat()
	{

		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.removeSytles(editor);
	}

	justifyCenter()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "display", "block");
		processor.apply(editor, "text-align", "center");
	}

	justifyLeft()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "display", "block");
		processor.apply(editor, "text-align", "left");
	}

	justifyRight()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "display", "block");
		processor.apply(editor, "text-align", "right");
	}

	justifyFull()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.apply(editor, "display", "block");
		processor.apply(editor, "text-align", "justify");
	}

	indent()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("indent");
	}

	outdent()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("outdent");
	}

	insertUnorderedList()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("insertUnorderedList");
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

	happyFace()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.insertIcon(editor, "2104");
	}

	sadFace()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		processor.insertIcon(editor, "2106");
	}

	insertIcon()
	{
		let editor = this.shadowRoot.getElementById("editor");
		editor.focus();
		GIconPicker.pick().then(e => e && processor.insertIcon(editor, e));
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



		document.execCommand("insertBrOnReturn", null, true);
		document.execCommand("enableObjectResizing", null, true);
		document.execCommand("enableAbsolutePositionEditor", null, true);

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

	attributeChangedCallback()
	{
		this.value = this.getAttribute("value");
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});