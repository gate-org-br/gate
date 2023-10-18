let template = document.createElement("template");
template.innerHTML = `
	<button tabindex='-1' id='bold' title='Negrito'>
		&#X3026;
	</button>
	<button tabindex='-1' id='italic' title='Itálico'>
		&#X3027;
	</button>
	<span></span>
	<select id="textDecoration" title='Decoração do texto'>
		<option value="">Estilo</option>
		<option value="none">Nenhum</option>
		<option value="underline">Sublinhado</option>
		<option value="line-through">Riscado</option>
		<option value="overline">Sobrelinhado</option>
	</select>
	<input id='foreColor' type="color" value='#FFFFFF' title='Cor do texto'/>
	<select id="fontSize" title='Tamanho da fonte'>
		<option value="">Tamanho</option>
		<option value="1px">1</option>
		<option value="2px">2</option>
		<option value="3px">3</option>
		<option value="4px">4</option>
		<option value="5px">5</option>
		<option value="6px">6</option>
		<option value="7px">7</option>
		<option value="8px">8</option>
		<option value="9px">9</option>
		<option value="10px">10</option>
		<option value="11px">11</option>
		<option value="12px">12</option>
		<option value="13px">13</option>
		<option value="14px">14</option>
		<option value="15px">15</option>
		<option value="16px">16</option>
		<option value="17px">17</option>
		<option value="18px">18</option>
		<option value="19px">19</option>
		<option value="20px">20</option>
		<option value="21px">21</option>
		<option value="22px">22</option>
		<option value="23px">23</option>
		<option value="24px">24</option>
		<option value="25px">25</option>
		<option value="26px">26</option>
		<option value="27px">27</option>
		<option value="28px">28</option>
		<option value="29px">29</option>
		<option value="30px">30</option>
	</select>
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
	<button tabindex='-1' id='removeFormat' title='Remover formatação'>
		&#X3030;
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
 <style>:host(*) {
	gap: 8px;
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
	min-width: 40px;
	font-size: 18px;
	background: #ddd;
	border: none;
	border-radius: 5px;
	font-family: "gate";
	cursor: pointer; /* Add a pointer cursor on hover */
	transition: background-color 0.3s; /* Smooth transition for background color */
}

button:hover {
	background-color: #ccc;  /* Darker background on hover */
}

span  {
	flex: 0 0 8px;
	display: block;
}

div {
	flex-grow: 1;
	display: block;
}

#foreColor, #fontSize, #textDecoration
{
	width: 80px;
	height: 40px;
	border-radius: 5px;
	border: 1px solid var(--main5);
}</style>`;

/* global customElements */

customElements.define('g-text-editor-toolbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 1;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let editor = this.editor;

		this.shadowRoot.getElementById("bold").addEventListener("click", () => editor.bold());
		this.shadowRoot.getElementById("italic").addEventListener("click", () => editor.italic());

		this.shadowRoot.getElementById("textDecoration").addEventListener("change", e => e.target.value && editor.textDecoration(e.target.value) & (e.target.value = ""));

		this.shadowRoot.getElementById("foreColor").addEventListener("change", e => editor.foreColor(e.target.value) & (e.target.value = "#FFFFFF"));

		this.shadowRoot.getElementById("fontSize").addEventListener("change", e => e.target.value && editor.fontSize(e.target.value) & (e.target.value = ""));

		this.shadowRoot.getElementById("removeFormat").addEventListener("click", () => editor.removeFormat());

		this.shadowRoot.getElementById("justifyCenter").addEventListener("click", () => editor.justifyCenter());
		this.shadowRoot.getElementById("justifyLeft").addEventListener("click", () => editor.justifyLeft());
		this.shadowRoot.getElementById("justifyRight").addEventListener("click", () => editor.justifyRight());
		this.shadowRoot.getElementById("justifyFull").addEventListener("click", () => editor.justifyFull());

		this.shadowRoot.getElementById("indent").addEventListener("click", () => editor.indent());
		this.shadowRoot.getElementById("outdent").addEventListener("click", () => editor.outdent());

		this.shadowRoot.getElementById("insertUnorderedList").addEventListener("click", () => editor.insertUnorderedList());
		this.shadowRoot.getElementById("insertOrderedList").addEventListener("click", () => editor.insertOrderedList());

		this.shadowRoot.getElementById("createLink").addEventListener("click", () => editor.createLink());
		this.shadowRoot.getElementById("unlink").addEventListener("click", () => editor.unlink());

		this.shadowRoot.getElementById("happyFace").addEventListener("click", () => editor.happyFace());
		this.shadowRoot.getElementById("sadFace").addEventListener("click", () => editor.sadFace());
		this.shadowRoot.getElementById("insertIcon").addEventListener("click", () => editor.insertIcon());

		this.shadowRoot.getElementById("attach").addEventListener("click", () => editor.attach());
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