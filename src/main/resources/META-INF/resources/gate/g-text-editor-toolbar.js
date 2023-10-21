let template = document.createElement("template");
template.innerHTML = `
	<button tabindex='-1' id='bold' title='Negrito'>
		<g-icon>&#X3026;</g-icon>
	</button>
	<button tabindex='-1' id='italic' title='Itálico'>
		<g-icon>&#X3027;</g-icon>
	</button>

	<button tabindex='-1' title='Cor do texto' style='color: #006600'>
		<g-icon>&#X3025;</g-icon>
		<input id='color' type="color" value='#FFFFFF' title='Cor do texto'/>
	</button>
	<button tabindex='-1' title='Cor do fundo' style='color: #006600'>
		<g-icon>&#X3067;</g-icon>
		<input id='background-color' type="color" value='#FFFFFF' title='Cor do fundo'/>
	</button>
	<button tabindex='-1' title='Cor da borda' style='color: #006600'>
		<g-icon>&#X2057;</g-icon>
		<input id='border-color' type="color" value='#FFFFFF' title='Cor da borda'/>
	</button>

	<button tabindex='-1' id='text-align-left' title='Alinha à esquerda' style='color: #000066'>
		<g-icon>&#X3032;</g-icon>
	</button>
	<button tabindex='-1' id='text-align-center' title='Centralizar' style='color: #000066'>
		<g-icon>&#X3034;</g-icon>
	</button>
	<button tabindex='-1' id='text-align-right' title='Alinha à direita' style='color: #000066'>
		<g-icon>&#X3033;</g-icon>
	</button>
	<button tabindex='-1' id='text-align-justify' title='Justificar' style='color: #000066'>
		<g-icon>&#X3031;</g-icon>
	</button>

	<select id="text-decoration" title='Decoração do texto'>
		<option value="" disabled hidden selected>Linha</option>
		<option value="none">Nenhuma</option>
		<option value="underline">Sublinhado</option>
		<option value="line-through">Riscado</option>
		<option value="overline">Sobrelinhado</option>
	</select>

	<select id="font-size" title='Tamanho da fonte'>
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
	</select>

	<select id="padding" title='Enchimento'>
		<option value="" disabled hidden selected>Enchimento</option>
		<option value="0">Nenhum</option>
		<option value="1px">1 Pixels</option>
		<option value="2px">2 Pixels</option>
		<option value="3px">3 Pixels</option>
		<option value="4px">4 Pixels</option>
		<option value="5px">5 Pixels</option>
		<option value="6px">6 Pixels</option>
		<option value="7px">7 Pixels</option>
		<option value="8px">8 Pixels</option>
		<option value="9px">9 Pixels</option>
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
	</select>

	<select id="margin" title='Margem'>
		<option value="" disabled hidden selected>Margem</option>
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
	</select>

	<button tabindex='-1' id='class' title='Estilo predefinido'>
		<g-icon>&#X2044;</g-icon>
	</button>

	<button tabindex='-1' id='remove-format' title='Remover formatação' style='color: #660000'>
		<g-icon>&#X3030;</g-icon>
	</button>

	<br/>
	<select id="emoji">
		<option value="" disabled selected hidden>Emoji</option>
		<option value="&#128578;">&#128578; Felicidade</option>
		<option value="&#128550;">&#128550; Tristeza</option>
		<option value="&#128552;">&#128552; Medo</option>
		<option value="&#128544;">&#128544; Raiva</option>
	</select>
	<br/>
	<button tabindex='-1' id='attach' title='Anexar arquivo'>
		<g-icon>&#X2079;</g-icon>
	</button>
	<hr/>
	<button tabindex='-1' id='createLink' title='Criar link'>
		<g-icon>
			&#X2076;
		</g-icon>
	</button>
	<button tabindex='-1' id='unlink' title='Remover link'>
		<g-icon>
			&#X2233;
		</g-icon>
	</button>
 <style>* {
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

button {
	color: #333;
	height: 40px;
	border: none;
	cursor: pointer;
	min-width: 40px;
	font-size: 18px;
	background: #ddd;
	border-radius: 5px;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: background-color 0.3s;
}

label:hover, button:hover {
	background-color: #ccc;
}

br  {
	flex: 0 0 8px;
	display: block;
	min-width: 8px;
}

hr {
	border: none;
	flex-grow: 1;
	display: block;
}

select
{
	width: 100px;
	height: 40px;
	border-radius: 5px;
	border: 1px solid var(--main5);
}

#emoji option {
	font-size: 18px
}

span {
	width: 100px;
	height: 40px;
	padding: 4px;

	font-size: 12px;

	gap: 4px;
	display: grid;
	align-items: center;
	justify-content: stretch;
	grid-template-columns: 16px 46px;

	background: #ddd;

	border-radius: 5px;
	border: 1px solid var(--main5);

	transition: background-color 0.3s;
}

input[type='color']
{
	display: none;
}
</style>`;

/* global customElements */
import './g-icon.js';
import GHierarchicalOptionPicker from './g-hierarchical-option-picker.js';

const classes =
	[{
			label: "Título",
			title: "Título",
			value: "title"
		},
		{
			label: "Subtítulo",
			title: "Subtítulo",
			value: "subtitle"
		}, {
			label: "Chamada",
			title: "Chamada",
			value: [{
					label: "Sólida",
					title: "Chamada sólida",
					value: [{label: "Alerta", title: "Chamada sólida de alerta", value: "callout warning fill"},
						{label: "Informação", title: "Chamada sólida de informação", value: "callout fill"},
						{label: "Perigo", title: "Chamada sólida de perigo", value: "callout danger fill"},
						{label: "Questionamento", title: "Chamada sólida de questionamento", value: "callout question fill"},
						{label: "Sucesso", title: "Chamada sólida de sucesso", value: "callout success fill"}]
				}, {
					label: "Transparente",
					title: "Chamada transparente",
					value: [{label: "Alerta", title: "Chamada transparente de alerta", value: "callout warning"},
						{label: "Informação", title: "Chamada transparente de informação", value: "callout"},
						{label: "Perigo", title: "Chamada transparente de perigo", value: "callout danger"},
						{label: "Questionamento", title: "Chamada transparente de questionamento", value: "callout question"},
						{label: "Sucesso", title: "Chamada transparente de sucesso", value: "callout success"}]
				}]
		}];

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

		this.shadowRoot.getElementById("bold").addEventListener("click", () => editor.selection.toggleStyle("font-weight", "700", "400"));
		this.shadowRoot.getElementById("italic").addEventListener("click", () => editor.selection.toggleStyle("font-style", "italic", "normal"));

		this.shadowRoot.getElementById("text-decoration").addEventListener("change", e => editor.selection.updateStyle("text-decoration-line", e.target.value));

		this.shadowRoot.getElementById("color").addEventListener("change", e => editor.selection.updateStyle("color", e.target.value));
		this.shadowRoot.getElementById("background-color").addEventListener("change", e => editor.selection.updateStyle("background-color", e.target.value));
		this.shadowRoot.getElementById("border-color").addEventListener("change", e => editor.selection.updateStyle("border", value && value !== '#FFFFFF' ? `1px solid ${value}` : 'none'));

		this.shadowRoot.getElementById("font-size").addEventListener("change", e => editor.selection.updateStyle('font-size', e.target.value));
		this.shadowRoot.getElementById("padding").addEventListener("change", e => editor.selection.updateStyle('padding', e.target.value));
		this.shadowRoot.getElementById("margin").addEventListener("change", e => editor.selection.updateStyle('margin', e.target.value));

		this.shadowRoot.getElementById("class")
			.addEventListener("click", e =>
			{
				let selection = editor.selection;
				GHierarchicalOptionPicker.pick(classes, "Selecione um estilo")
					.then(value => value && selection.updateClass(value))
			});

		this.shadowRoot.getElementById("remove-format").addEventListener("click", () => editor.selection.clearStyles());

		this.shadowRoot.getElementById("emoji").addEventListener("change", e => editor.selection.appendText(e.target.value));

		this.shadowRoot.getElementById("text-align-center").addEventListener("click", () => editor.selection.updateStyle("display", "block").updateStyle("text-align", "center"));
		this.shadowRoot.getElementById("text-align-left").addEventListener("click", () => editor.selection.updateStyle("display", "block").updateStyle("text-align", "left"));
		this.shadowRoot.getElementById("text-align-right").addEventListener("click", () => editor.selection.updateStyle("display", "block").updateStyle("text-align", "right"));
		this.shadowRoot.getElementById("text-align-justify").addEventListener("click", () => editor.selection.updateStyle("display", "block").updateStyle("text-align", "justify"));

		this.shadowRoot.getElementById("insertUnorderedList").addEventListener("click", () => editor.insertUnorderedList());
		this.shadowRoot.getElementById("insertOrderedList").addEventListener("click", () => editor.insertOrderedList());

		this.shadowRoot.getElementById("createLink").addEventListener("click", () => editor.createLink());
		this.shadowRoot.getElementById("unlink").addEventListener("click", () => editor.unlink());

		this.shadowRoot.getElementById("attach").addEventListener("click", () => editor.attach());

		Array.from(this.shadowRoot.querySelectorAll("select")).forEach(e => e.addEventListener("change", () => e.value = ""));
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