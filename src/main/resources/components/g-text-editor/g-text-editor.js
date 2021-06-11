/* global customElements */

class GTextEditor extends HTMLElement
{
	constructor()
	{
		super();

		this.addEventListener("focus", () => this._private.editor.focus());

		this._private = {};

		this._private.root = document.createElement("div");

		this._private.toolbar = this._private.root.appendChild(document.createElement("div"));

		this._private.bold = this.addCommand(() => this.bold(), "&#X3026;", "Negrito");
		this._private.italic = this.addCommand(() => this.italic(), "&#X3027;", "Itálico");
		this._private.underline = this.addCommand(() => this.underline(), "&#X3028;", "Sublinhado");
		this._private.strikeThrough = this.addCommand(() => this.strikeThrough(), "&#X3029;", "Riscado");

		this.addSeparator();

		this._private.redFont = this.addCommand(() => this.redFont(), "&#X3025;", "Vermelho", "#660000");
		this._private.greenFont = this.addCommand(() => this.greenFont(), "&#X3025;", "Verde", "#006600");
		this._private.blueFont = this.addCommand(() => this.blueFont(), "&#X3025;", "Azul", "#000066");

		this.addSeparator();

		this._private.increaseFontSize = this.addCommand(() => this.increaseFontSize(), "&#X1012;", "Aumentar fonte");
		this._private.decreaseFontSize = this.addCommand(() => this.decreaseFontSize(), "&#X1013;", "Reduzir fonte");

		this.addSeparator();

		this._private.removeFormat = this.addCommand(() => this.removeFormat(), "&#X3030;", "Remover formatação");

		this.addSeparator();

		this._private.justifyCenter = this.addCommand(() => this.justifyCenter(), "&#X3034;", "Centralizar");
		this._private.justifyLeft = this.addCommand(() => this.justifyLeft(), "&#X3032;", "Alinha à esquerda");
		this._private.justifyRight = this.addCommand(() => this.justifyRight(), "&#X3033;", "Alinha à direita");
		this._private.justifyFull = this.addCommand(() => this.justifyFull(), "&#X3031;", "Justificar");

		this.addSeparator();

		this._private.indent = this.addCommand(() => this.indent(), "&#X3039;", "Indentar");
		this._private.outdent = this.addCommand(() => this.outdent(), "&#X3040;", "Remover indentação");

		this.addSeparator();

		this._private.insertUnorderedList = this.addCommand(() => this.insertUnorderedList(), "&#X3035;", "Criar lista");
		this._private.insertOrderedList = this.addCommand(() => this.insertOrderedList(), "&#X3038;", "Criar lista ordenada");

		this.addSeparator();

		this._private.createLink = this.addCommand(() => this.createLink(), "&#X2076;", "Criar link");
		this._private.unlink = this.addCommand(() => this.unlink(), "&#X2233;", "Remover link");

		this.addSeparator();

		this._private.happyFace = this.addCommand(() => this.happyFace(), "&#X2104;", "Carinha feliz");
		this._private.sadFace = this.addCommand(() => this.sadFace(), "&#X2106;", "Carinha triste");
		this._private.icon = this.addCommand(() => this.insertIcon(), "&#X3017;", "Inserir ícone");

		this.addSeparator();

		this._private.image = this.addCommand(() => this.insertImage(), "&#X2009;", "Inserir imagem");

		this._private.editor = this._private.root.appendChild(document.createElement("div"));
		this._private.editor.setAttribute("contentEditable", true);

		this._private.input = this._private.root.appendChild(document.createElement("input"));
		this._private.input.setAttribute("type", "hidden");

		this._private.editor.addEventListener("input", () => this._private.input.value = this._private.editor.innerHTML);
	}

	addCommand(action, icon, title, color)
	{
		let button = this._private.toolbar.appendChild(document.createElement("button"));
		button.innerHTML = icon;
		button.title = title;
		button.style.color = color || "#000000";
		button.addEventListener("click", e =>
		{
			e.preventDefault();
			e.stopPropagation();
			action();
		});
	}

	addSeparator()
	{
		return this._private.toolbar.appendChild(document.createElement("span"));
	}

	bold()
	{
		this._private.editor.focus();
		document.execCommand("bold");
	}

	italic()
	{
		this._private.editor.focus();
		document.execCommand("italic");
	}

	underline()
	{
		this._private.editor.focus();
		document.execCommand("underline");
	}

	strikeThrough()
	{
		this._private.editor.focus();
		document.execCommand("strikeThrough");
	}

	redFont()
	{
		this._private.editor.focus();
		document.execCommand("foreColor", null, "#660000");
	}

	greenFont()
	{
		this._private.editor.focus();
		document.execCommand("foreColor", null, "#006600");
	}

	blueFont()
	{
		this._private.editor.focus();
		document.execCommand("foreColor", null, "#000066");
	}

	removeFormat()
	{
		this._private.editor.focus();
		document.execCommand("removeFormat");
	}

	justifyCenter()
	{
		this._private.editor.focus();
		document.execCommand("justifyCenter");
	}

	justifyLeft()
	{
		this._private.editor.focus();
		document.execCommand("justifyLeft");
	}

	justifyRight()
	{
		this._private.editor.focus();
		document.execCommand("justifyRight");
	}

	justifyFull()
	{
		this._private.editor.focus();
		document.execCommand("justifyFull");
	}

	indent()
	{
		this._private.editor.focus();
		document.execCommand("indent");
	}

	outdent()
	{
		this._private.editor.focus();
		document.execCommand("outdent");
	}

	insertUnorderedList()
	{
		this._private.editor.focus();
		document.execCommand("insertUnorderedList");
	}

	insertOrderedList()
	{
		this._private.editor.focus();
		document.execCommand("insertOrderedList");
	}

	createLink()
	{
		this._private.editor.focus();
		document.execCommand("createLink", null, prompt("Entre com a url"));
	}

	unlink()
	{
		this._private.editor.focus();
		document.execCommand("unlink");
	}

	happyFace()
	{
		this._private.editor.focus();
		document.execCommand("insertHTML", null, `<i>&#X2104</i>`);
	}

	sadFace()
	{
		this._private.editor.focus();
		document.execCommand("insertHTML", null, `<i>&#X2106</i>`);
	}

	increaseFontSize()
	{
		this._private.editor.focus();
		document.execCommand("increaseFontSize");
	}

	decreaseFontSize()
	{
		this._private.editor.focus();
		document.execCommand("decreaseFontSize");
	}

	insertIcon()
	{
		this._private.editor.focus();
		let picker = window.top.document.createElement("g-icon-picker");
		picker.addEventListener("picked", e => document.execCommand("insertHTML", null, `<i>&#X${e.detail}</i>`));
		picker.show();
	}

	insertImage()
	{
		this._private.editor.focus();
		let blob = document.createElement("input");
		blob.setAttribute("type", "file");
		blob.setAttribute("accept", ".jpg, .png, .svg, .jpeg, .gif, .bmp, .tif, .tiff|image/*");
		blob.addEventListener("change", () =>
		{
			let reader = new FileReader();
			reader.readAsDataURL(blob.files[0]);
			reader.onloadend = () => document.execCommand("insertImage", null, reader.result);
		});
		blob.click();
	}

	connectedCallback()
	{
		this.appendChild(this._private.root);
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
				this._private.input.setAttribute("name", this.getAttribute("name"));
				break;
			case "value":
				this._private.editor.innerHTML = this.getAttribute("value");
				this._private.input.setAttribute("value", this.getAttribute("value"));
				break;
			case "required":
				this._private.input.setAttribute("required", this.getAttribute("required"));
				break;
			case "maxlength":
				this._private.input.setAttribute("maxlength", this.getAttribute("maxlength"));
				break;
			case "pattern":
				this._private.input.setAttribute("pattern", this.getAttribute("pattern"));
				break;
			case "tabindex":
				this._private.editor.setAttribute("tabindex", this.getAttribute("tabindex"));
				break;
		}
	}

	static get observedAttributes()
	{
		return ["name", "value", "required", "maxlength", "pattern", "tabindex"];
	}
}

customElements.define('g-text-editor', GTextEditor);