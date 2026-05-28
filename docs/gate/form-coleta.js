let template = document.createElement("template");
template.innerHTML = `
	<form id="coleta" action="#"><fieldset><label>
				Tipo:
				<span><select id="persistencia" required><option>Tipo</option><option value="TEMPORARIA">Temporária</option><option value="PERMANENTE">Permanente</option></select></span></label></fieldset><g-coolbar><a href="#" class="primary" id="concluir">
				Concluir
				<g-icon>&#X1000;</g-icon></a></g-coolbar></form><form id='campos' action="#"><table><caption>
				CAMPOS
			</caption><colgroup><col style="width: 25%"><col style="width: 25%"><col style="width: 50%"><col style="width: 50px"/></colgroup><thead><tr><td><select id="tipo" required><option>Tipo</option><option value="CEP">CEP</option><option value="CPF">CPF</option><option value="CNPJ">CNPJ</option><option value="EMAIL">E-Mail</option></select></td><td><input type="text" id="nome" required
						       placeholder="Nome"></td><td><input type="text" id="mensagem" required
						       placeholder="Mensagem"></td><td><a class="primary"
						   id='adicionar' href="#"><g-icon>&#X1002;</g-icon></a></td></tr></thead><tbody></tbody></table></form>
<style data-element="form-coleta">* { box-sizing: border-box;}

:host(*) {
}</style>`;
/* global customElements */

import './g-icon.js';
import StyledHTMLElement from './styled-html-element.js';

customElements.define('form-coleta', class extends StyledHTMLElement
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const coleta = this.shadowRoot.getElementById("coleta");
		const campos = this.shadowRoot.getElementById("campos");
		const tipo = this.shadowRoot.getElementById("tipo");
		const nome = this.shadowRoot.getElementById("nome");
		const mensagem = this.shadowRoot.getElementById("mensagem");
		this.shadowRoot.getElementById("adicionar")
			.addEventListener("click", () =>
			{
				if (campos.reportValidity())
					this.campos = [...this.campos, {
						"tipo": tipo.value,
						"nome": nome.value, "mensagem": mensagem.value
					}];
			});

		this.shadowRoot.getElementById("concluir")
			.addEventListener("click", () =>
			{
				if (coleta.reportValidity())
					this.dispatchEvent(new CustomEvent("commit", {
						detail: this.value
					}));
			});
	}

	set value(value)
	{
		this.shadowRoot.getElementById("persistencia").value
			= value.persistencia;
		this.campos = value.campos;
	}

	get value()
	{
		return ({
			persistencia: this.shadowRoot.getElementById("persistencia").value,
			campos: this.campos
		});
	}

	set campos(campos)
	{
		const tbody = this.shadowRoot.querySelector("tbody");
		tbody.innerHTML = "";
		campos.forEach(campo =>
		{
			const tr = tbody.appendChild(document.createElement("tr"));
			tr.appendChild(document.createElement("td")).innerText = campo.tipo;
			tr.appendChild(document.createElement("td")).innerText = campo.nome;
			tr.appendChild(document.createElement("td")).innerText = campo.mensagem;

			const remove = tr.appendChild(document.createElement("td"))
				.appendChild(document.createElement("a"));
			remove.appendChild(document.createElement("g-icon"))
				.innerHTML = "&#X2026;";
			remove.addEventListener("click", () => tr.remove());
		});
	}

	get campos()
	{
		const tbody
			= this.shadowRoot.querySelector("tbody");
		return Array.from(tbody.children)
			.map(e => ({
				"tipo": e.children[0].innerText,
				"nome": e.children[1].innerText,
				"mensagem": e.children[2].innerText
			}));
	}
});