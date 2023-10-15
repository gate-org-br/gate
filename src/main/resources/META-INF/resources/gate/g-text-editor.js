let template = document.createElement("template");
template.innerHTML = `
	<div id='toolbar'>
		<button tabindex='-1' id='bold' title='Negrito'>
			&#X3026;
		</button>
		<button tabindex='-1' id='italic' title='Itálico'>
			&#X3027;
		</button>
		<button tabindex='-1' id='underline' title='Sublinhado'>
			&#X3028;
		</button>
		<button tabindex='-1' id='strikeThrough' title='Riscado'>
			&#X3029;
		</button>
		<input id='foreColor' type="color"
		       value='#FFFFFF' title='Cor do texto'/>
		<select id="fontSize" title='Tamanho da fonte'>
			<option value="">0</option>
			<option value="1">1</option>
			<option value="2">2</option>
			<option value="3">3</option>
			<option value="4">4</option>
			<option value="5">5</option>
			<option value="6">6</option>
			<option value="7">7</option>
		</select>
		<button tabindex='-1' id='removeFormat' title='Remover formatação'>
			&#X3030;
		</button>
		<span></span>
		<button tabindex='-1' id='justifyCenter' title='Centralizar'>
			&#X3034;
		</button>
		<button tabindex='-1' id='justifyLeft' title='Alinha à esquerda'>
			&#X3032;
		</button>
		<button tabindex='-1' id='justifyRight' title='Alinha à direita'>
			&#X3033;
		</button>
		<button tabindex='-1' id='justifyFull' title='Justificar'>
			&#X3031;
		</button>
		<span></span>
		<button tabindex='-1' id='indent' title='Indentar'>
			&#X3039;
		</button>
		<button tabindex='-1' id='outdent' title='Remover indentação'>
			&#X3040;
		</button>
		<span></span>
		<button tabindex='-1' id='insertUnorderedList' title='Criar lista'>
			&#X3035;
		</button>
		<button tabindex='-1' id='insertOrderedList' title='Criar lista ordenada'>
			&#X3038;
		</button>
		<span></span>
		<button tabindex='-1' id='createLink' title='Criar link'>
			&#X2076;
		</button>
		<button tabindex='-1' id='unlink' title='Remover link'>
			&#X2233;
		</button>
		<span></span>
		<button tabindex='-1' id='happyFace' title='Carinha feliz"'>
			&#X2104;
		</button>
		<button tabindex='-1' id='sadFace' title='Carinha triste"'>
			&#X2106;
		</button>
		<button tabindex='-1' id='insertIcon' title='Inserir ícone'>
			&#X3017;
		</button>
		<span></span>
		<button tabindex='-1' id='attach' title='Anexar arquivo'>
			&#X2079;
		</button>
	</div>
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
}

:host([hidden])
{
	display:  none;
}

#scroll {
	top: 40px;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	overflow: auto;
	position: absolute;
	align-items: stretch;
	justify-content: stretch;
}

#editor picture {
	padding: 8px;
	resize: both;
	display: flex;
	overflow: auto;
	max-width: 100%;
	width: fit-content;
	height: fit-content;
	align-items: stretch;
	justify-content: stretch;
	border: 1px solid #CCCCCC;
	background-color: #EFEFEF;
}

#toolbar
{
	gap: 4px;
	padding: 4px;
	display: flex;
	overflow: auto;
	align-items: center;
	background-color: var(--main6);
	border-radius: 5px 5px 0 0;
	justify-content: flex-start;
}

button
{
	color: black;
	height: 32px;
	display: flex;
	flex: 0 0 32px;
	font-size: 16px;
	background: none;
	border-radius: 5px;
	align-items: center;
	font-family: "gate";
	justify-content: center;
	border: 1px solid var(--main5);
	background-color: var(--main4);
}

button:hover {
	background-color: var(--main1);
}

#toolbar > span  {
	flex: 0 0 8px;
	display: block;
}

#toolbar > div {
	flex-grow: 1;
	display: block;
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

#foreColor, #fontSize
{
	width: 64px;
	height: 32px;
	border-radius: 5px;
	border: 1px solid var(--main5);
}</style>`;

/* global customElements */

import './g-icon.js';
import './g-text-editor-box.js';

function ensureBoxSpaces(editor)
{
	Array.from(editor.querySelectorAll('g-text-editor-box')).forEach(box =>
	{
		if (!box.previousElementSibling)
			box.parentNode.insertBefore(document.createElement("br"), box);
		if (!box.nextElementSibling)
			box.parentNode.appendChild(document.createElement("br"));
	});
}

customElements.define('g-text-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 0;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.getElementById("bold").addEventListener("click", () => this.bold());
		this.shadowRoot.getElementById("italic").addEventListener("click", () => this.italic());
		this.shadowRoot.getElementById("underline").addEventListener("click", () => this.underline());
		this.shadowRoot.getElementById("strikeThrough").addEventListener("click", () => this.strikeThrough());

		this.shadowRoot.getElementById("foreColor").addEventListener("change",
			e => this.foreColor(e.target.value) & (e.target.value = "#FFFFFF"));

		this.shadowRoot.getElementById("fontSize")
			.addEventListener("change", e => e.target.value
					&& this.fontSize(e.target.value)
					& (e.target.value = ""));

		this.shadowRoot.getElementById("removeFormat").addEventListener("click", () => this.removeFormat());

		this.shadowRoot.getElementById("justifyCenter").addEventListener("click", () => this.justifyCenter());
		this.shadowRoot.getElementById("justifyLeft").addEventListener("click", () => this.justifyLeft());
		this.shadowRoot.getElementById("justifyRight").addEventListener("click", () => this.justifyRight());
		this.shadowRoot.getElementById("justifyFull").addEventListener("click", () => this.justifyFull());

		this.shadowRoot.getElementById("indent").addEventListener("click", () => this.indent());
		this.shadowRoot.getElementById("outdent").addEventListener("click", () => this.outdent());

		this.shadowRoot.getElementById("insertUnorderedList").addEventListener("click", () => this.insertUnorderedList());
		this.shadowRoot.getElementById("insertOrderedList").addEventListener("click", () => this.insertOrderedList());

		this.shadowRoot.getElementById("createLink").addEventListener("click", () => this.createLink());
		this.shadowRoot.getElementById("unlink").addEventListener("click", () => this.unlink());

		this.shadowRoot.getElementById("happyFace").addEventListener("click", () => this.happyFace());
		this.shadowRoot.getElementById("sadFace").addEventListener("click", () => this.sadFace());
		this.shadowRoot.getElementById("insertIcon").addEventListener("click", () => this.insertIcon());

		this.shadowRoot.getElementById("attach").addEventListener("click", () => this.attach());

		let editor = this.shadowRoot.getElementById("editor");
		editor.addEventListener('input', event => ensureBoxSpaces(editor));
		editor.addEventListener('click', event => ensureBoxSpaces(editor));

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
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("bold", false, null);
	}

	italic()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("italic");
	}

	underline()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("underline");
	}

	strikeThrough()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("strikeThrough");
	}

	fontSize(value)
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("fontSize", null, value);
	}

	foreColor(value)
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("foreColor", null, value);

	}

	removeFormat()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("removeFormat");
	}

	justifyCenter()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("justifyCenter");
	}

	justifyLeft()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("justifyLeft");
	}

	justifyRight()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("justifyRight");
	}

	justifyFull()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("justifyFull");
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
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("insertHTML", null, `<i>&#X2104</i>`);
	}

	sadFace()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("insertHTML", null, `<i>&#X2106</i>`);
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

	insertIcon()
	{
		this.shadowRoot.getElementById("editor").focus();
		let picker = window.top.document.createElement("g-icon-picker");
		picker.addEventListener("picked", e => document.execCommand("insertHTML", null, `<g-icon>&#X${e.detail}</g-icon>`));
		picker.show();
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

	separator()
	{
		this.shadowRoot.getElementById("toolbar")
			.appendChild(document.createElement("span"));
		return this;
	}

	spacer()
	{
		this.shadowRoot.getElementById("toolbar")
			.appendChild(document.createElement("div"));
		return this;
	}

	command(icon, title, action)
	{
		let button = this.shadowRoot.getElementById("toolbar")
			.appendChild(document.createElement("button"));
		button.title = title;
		button.innerHTML = `&#X${icon}`;
		button.addEventListener("click", action);
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