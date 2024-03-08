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
		<span></span>
		<button tabindex='-1' id='red' title='Vermelho'>
			&#X3025;
		</button>
		<button tabindex='-1' id='green' title='Verde'>
			&#X3025;
		</button>
		<button tabindex='-1' id='blue' title='Azul'>
			&#X3025;
		</button>
		<span></span>
		<button tabindex='-1' id='increaseFontSize' title='Aumentar fonte'>
			&#X1012;
		</button>
		<button tabindex='-1' id='decreaseFontSize' title='Reduzir fonte'>
			&#X1013;
		</button>
		<span></span>
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
		<button tabindex='-1' id='insertImage' title='Inserir imagem'>
			&#X2009;
		</button>
	</div>
	<div id='editor' tabindex="0" contentEditable='true'>
	</div>
 <style data-element="g-text-editor">:host(*)
{
	width: 100%;
	height: 100%;
	display: grid;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	grid-template-rows: auto 1fr;
}

#toolbar
{
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
	margin: 2px;
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

#red {
	color: #660000
}
#green {
	color: #006600
}
#blue {
	color: #000066
}

:host(*) > span  {
	flex: 0 0 8px;
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
	background-color: var(--hovered);
}

i {
	font-family: gate;
	font-style: normal;
	text-decoration: none;
}

img {
	max-width: 100%;
}</style>`;
/* global customElements */

customElements.define('g-text-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		if (!this.tabindex)
			this.tabindex = 0;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this._private = {"input": document.createElement("input")};
		this._private.input.setAttribute("type", "hidden");

		this.shadowRoot.getElementById("bold").addEventListener("click", () => this.bold());
		this.shadowRoot.getElementById("italic").addEventListener("click", () => this.italic());
		this.shadowRoot.getElementById("underline").addEventListener("click", () => this.underline());
		this.shadowRoot.getElementById("strikeThrough").addEventListener("click", () => this.strikeThrough());

		this.shadowRoot.getElementById("red").addEventListener("click", () => this.redFont());
		this.shadowRoot.getElementById("green").addEventListener("click", () => this.greenFont());
		this.shadowRoot.getElementById("blue").addEventListener("click", () => this.blueFont());

		this.shadowRoot.getElementById("increaseFontSize").addEventListener("click", () => this.increaseFontSize());
		this.shadowRoot.getElementById("decreaseFontSize").addEventListener("click", () => this.decreaseFontSize());

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

		this.shadowRoot.getElementById("insertImage").addEventListener("click", () => this.insertImage());

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
		editor.addEventListener("input", () => this._private.input.value = editor.innerHTML);
	}

	bold()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("bold");
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

	redFont()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("foreColor", null, "#660000");
	}

	greenFont()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("foreColor", null, "#006600");
	}

	blueFont()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("foreColor", null, "#000066");
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

	increaseFontSize()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("increaseFontSize");
	}

	decreaseFontSize()
	{
		this.shadowRoot.getElementById("editor").focus();
		document.execCommand("decreaseFontSize");
	}

	insertIcon()
	{
		this.shadowRoot.getElementById("editor").focus();
		let picker = window.top.document.createElement("g-icon-picker");
		picker.addEventListener("picked", e => document.execCommand("insertHTML", null, `<i>&#X${e.detail}</i>`));
		picker.show();
	}

	insertImage()
	{
		let blob = document.createElement("input");
		blob.setAttribute("type", "file");
		blob.setAttribute("accept", ".jpg, .png, .svg, .jpeg, .gif, .bmp, .tif, .tiff|image/*");
		blob.addEventListener("change", () =>
		{
			let reader = new FileReader();
			reader.readAsDataURL(blob.files[0]);
			reader.onloadend = () =>
			{
				this.shadowRoot.getElementById("editor").focus();
				document.execCommand("insertImage", null, reader.result);
			};
		});
		blob.click();
	}

	connectedCallback()
	{
		this.appendChild(this._private.input);
		document.execCommand("styleWithCSS", null, true);
	}

	get value()
	{
		return this.getAttribute("value");
	}

	set value(value)
	{
		this.setAttribute("value", value);
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
		return this.getAttribute("maxlength");
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

	attributeChangedCallback(atrribute)
	{
		switch (atrribute)
		{
			case "name":
				this._private.input.setAttribute("name", this.name);
				break;
			case "value":
				this.shadowRoot.getElementById("editor").innerHTML = this.value;
				this._private.input.setAttribute("value", this.value);
				break;
			case "required":
				this._private.input.setAttribute("required", this.required);
				break;
			case "maxlength":
				this._private.input.setAttribute("maxlength", this.maxlength);
				break;
			case "pattern":
				this._private.input.setAttribute("pattern", this.pattern);
				break;
		}
	}

	static get observedAttributes()
	{
		return ["name", "value", "required", "maxlength", "pattern", "tabindex"];
	}
});