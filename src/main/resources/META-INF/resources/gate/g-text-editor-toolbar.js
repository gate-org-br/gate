let template = document.createElement("template");
template.innerHTML = `
	<select id="formatFontName" title='Fonte'>
		<option value="" disabled hidden selected>Fonte</option>
		<option value="Andalé Mono">Andalé Mono</option>
		<option value="Arial">Arial</option>
		<option value="Arial Black">Arial Black</option>
		<option value="Baskerville">Baskerville</option>
		<option value="Bradley Hand">Bradley Hand</option>
		<option value="Brush Script MT">Brush Script MT</option>
		<option value="Comic Sans MS">Comic Sans MS</option>
		<option value="Courier">Courier</option>
		<option value="Georgia">Georgia</option>
		<option value="Gill Sans">Gill Sans</option>
		<option value="Helvetica">Helvetica</option>
		<option value="Impact">Impact</option>
		<option value="Lucida">Lucida</option>
		<option value="Luminari">Luminari</option>
		<option value="Monaco">Monaco</option>
		<option value="Palatino">Palatino</option>
		<option value="Tahoma">Tahoma</option>
		<option value="Times New Roman">Times New Roman</option>
		<option value="Trebuchet MS">Trebuchet MS</option>
		<option value="Verdana">Verdana</option>
	</select>

	<select id="formatFontSize" title='Tamanho da fonte'>
		<option value="" disabled hidden selected>Tamanho</option>
		<option value="10px">10 Pixels</option>
		<option value="11px">11 Pixels</option>
		<option value="12px">12 Pixels</option>
		<option value="13px">13 Pixels</option>
		<option value="14px">14 Pixels</option>
		<option value="15px">15 Pixels</option>
		<option value="16px">16 Pixels</option>
		<option value="17px">17 Pixels</option>
		<option value="18px">18 Pixels</option>
		<option value="19px">19 Pixels</option>
		<option value="20px">20 Pixels</option>
		<option value="21px">21 Pixels</option>
		<option value="22px">22 Pixels</option>
		<option value="23px">23 Pixels</option>
		<option value="24px">24 Pixels</option>
		<option value="25px">25 Pixels</option>
		<option value="26px">26 Pixels</option>
		<option value="27px">27 Pixels</option>
		<option value="28px">28 Pixels</option>
		<option value="29px">29 Pixels</option>
		<option value="30px">30 Pixels</option>
		<option value="31px">31 Pixels</option>
		<option value="32px">32 Pixels</option>
		<option value="33px">33 Pixels</option>
		<option value="34px">34 Pixels</option>
		<option value="35px">35 Pixels</option>
		<option value="36px">36 Pixels</option>
		<option value="37px">37 Pixels</option>
		<option value="38px">38 Pixels</option>
		<option value="39px">39 Pixels</option>
		<option value="40px">40 Pixels</option>
		<option value="41px">41 Pixels</option>
		<option value="42px">42 Pixels</option>
		<option value="43px">43 Pixels</option>
		<option value="44px">44 Pixels</option>
		<option value="45px">45 Pixels</option>
		<option value="46px">46 Pixels</option>
		<option value="47px">47 Pixels</option>
		<option value="48px">48 Pixels</option>
		<option value="49px">49 Pixels</option>
		<option value="50px">50 Pixels</option>
	</select>
	<button tabindex='-1' id='formatBold' title='Negrito'>
		<g-icon>&#X3026;</g-icon>
	</button>
	<button tabindex='-1' id='formatItalic' title='Itálico'>
		<g-icon>&#X3027;</g-icon>
	</button>
	<button tabindex='-1' id='formatUnderline' title='Sublinhado'>
		<g-icon>&#X3028;</g-icon>
	</button>
	<button tabindex='-1' id='formatStrikeThrough' title='Riscado'>
		<g-icon>&#X3029;</g-icon>
	</button>
	<label tabindex='-1' title='Cor do texto' style="color: #006600">
		<g-icon>&#X3025;</g-icon>
		<input id='formatFontColor' type="color" value='#CCCCCC' title='Cor do texto'/>
	</label>
	<label tabindex='-1' title='Cor do fundo' style="color: #006600">
		<g-icon>&#X3067;</g-icon>
		<input id='formatBackColor' type="color" value='#CCCCCC' title='Cor do fundo'/>
	</label>
	<button tabindex='-1' id='formatRemove' title='Remover formatação' style='color: #660000'>
		<g-icon>&#X3030;</g-icon>
	</button>

	<br/>

	<button tabindex='-1' id='attach' title='Anexar arquivo'>
		<g-icon>&#X2079;</g-icon>
	</button>

	<br/>
	<button tabindex='-1' id='undo' title='Desfazer'>
		<g-icon>&#X2023;</g-icon>
	</button>
	<button tabindex='-1' id='redo' title='Refazer'>
		<g-icon>&#X2024;</g-icon>
	</button>
 <style data-element="g-text-editor-toolbar">* {
	box-sizing: border-box;
}
:host(*) {
	gap: 4px;
	padding: 8px;
	display: flex;
	overflow: auto;
	align-items: center;
	background-color: #f2f2f2;
	border-radius: 5px 5px 0 0;
	justify-content: flex-start;
	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.1);
}

button, label {
	gap: 4px;
	padding: 4px;
	color: #333;
	height: 40px;
	border: none;
	display: flex;
	cursor: pointer;
	min-width: 40px;
	font-size: 18px;
	background: #ddd;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	transition: background-color 0.3s;
}

label:hover, button:hover {
	background-color: #ccc;
}

br  {
	flex: 0 0 16px;
	min-width: 16px;
	content: " " !important;
	display: block !important;
}

hr {
	border: none;
	flex-grow: 1;
	display: block;
}

select
{
	width: 120px;
	height: 40px;
	border-radius: 5px;
	border: 1px solid var(--main5);
}

#emoji option {
	font-size: 18px
}

input[type='color']
{
	width: 32px;
	border: none;
	cursor: pointer;
}</style>`;
/* global customElements */
import './g-icon.js';

customElements.define('g-text-editor-toolbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 1;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let editor = this.editor;

		Array.from(this.shadowRoot.querySelectorAll("button > input")).forEach(e => e.parentNode.addEventListener("click", () => e.click()));

		this.shadowRoot.getElementById("formatFontName").addEventListener("change", e => editor.formatFontName(e.target.value));
		this.shadowRoot.getElementById("formatFontSize").addEventListener("change", e => editor.formatFontSize(e.target.value));

		this.shadowRoot.getElementById("formatBold").addEventListener("click", () => editor.formatBold());
		this.shadowRoot.getElementById("formatItalic").addEventListener("click", () => editor.formatItalic());

		this.shadowRoot.getElementById("formatUnderline").addEventListener("click", () => editor.formatUnderline());
		this.shadowRoot.getElementById("formatStrikeThrough").addEventListener("click", () => editor.formatStrikeThrough());

		this.shadowRoot.getElementById("formatFontColor").addEventListener("change", e => editor.formatFontColor(e.target.value));
		this.shadowRoot.getElementById("formatBackColor").addEventListener("change", e => editor.formatBackColor(e.target.value));

		this.shadowRoot.getElementById("formatRemove").addEventListener("click", () => editor.formatRemove());

		this.shadowRoot.getElementById("undo").addEventListener("click", () => editor.undo());
		this.shadowRoot.getElementById("redo").addEventListener("click", () => editor.redo());

		this.shadowRoot.getElementById("attach").addEventListener("click", () =>
		{
			let blob = document.createElement("input");
			blob.setAttribute("type", "file");

			blob.addEventListener("change", () =>
			{
				let file = blob.files[0];
				let reader = new FileReader();
				reader.readAsDataURL(file);
				reader.onloadend = () => editor.attach(file.type, file.name, reader.result);
			});

			blob.click();
		});

		Array.from(this.shadowRoot.querySelectorAll("select")).forEach(e => e.addEventListener("change", () => e.value = ""));
		Array.from(this.shadowRoot.querySelectorAll("input[type='color']")).forEach(e => e.addEventListener("change", () => e.value = "#CCCCCC"));
	}

	separator()
	{
		this.shadowRoot.appendChild(document.createElement("span"));
		return this;
	}

	spacer()
	{
		this.shadowRoot.appendChild(document.createElement("div"));
		return this;
	}

	command(icon, title, action)
	{
		let button = this.appendChild(document.createElement("button"));
		button.title = title;
		button.innerHTML = `&#X${icon}`;
		button.addEventListener("click", action);
		return this;
	}

	get editor()
	{
		return this.getRootNode().host;
	}
});