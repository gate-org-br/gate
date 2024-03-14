let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			<img id='logo' src='Logo.svg'>
			<label id='app'></label>
			<label id='version'></label>
		</header>
		<section>
			<g-tab-control>
				<a href='#' id='login-tab'>
					<g-icon>&#X2004;</g-icon>Fazer logon
				</a>
				<div>
					<form id='form'
					      method ='POST'
					      target ='_top'
					      action='#'>
						<fieldset>
							<label>
								Login:
								<span>
									<input type="text" required='required' name='$username' maxlength='64' tabindex="1" title='Entre com o seu login.'>
								</span>
							</label>
							<label>
								Senha:
								<span>
									<input type="password" required='required' name='$password' maxlength='32' tabindex="1" title='Entre com a sua senha.'>
								</span>
							</label>
						</fieldset>
						<g-coolbar>
							<button class="primary">
								Entrar<g-icon>&#X2002;</g-icon>
							</button>
						</g-coolbar>
					</form>
				</div>

				<a id='setup-tab' href='#'>
					<g-icon>&#X2002;</g-icon>Trocar senha
				</a>
				<div>
					<form method ='POST' action="#">
						<fieldset>
							<label style='grid-column: 1 / span 8'>
								Login:
								<span>
									<input type="text" required='required' maxlength='64' tabindex="1" title='Entre com o seu login.'/>
								</span>
							</label>
							<label style='grid-column: 9 / span 8'>
								Senha:
								<span>
									<input type="password" required='required' maxlength='32' tabindex="1" title='Entre com a sua senha.'/>
								</span>
							</label>
							<label style='grid-column: 1 / span 8'>
								Nova Senha:
								<span>
									<input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no mínimo 8 caracteres' pattern='^.{8,}$'/>
								</span>
							</label>
							<label style='grid-column: 9 / span 8'>
								Repita:
								<span>
									<input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no mínimo 8 caracteres' pattern='^.{8,}$'/>
								</span>
							</label>
						</fieldset>
						<g-coolbar>
							<button id='setup-button' class="primary">
								Trocar<g-icon>&#X2057;</g-icon>
							</button>
						</g-coolbar>
					</form>
				</div>
				<a id='forgot-tab' href='#'>
					<g-icon>&#X2034;</g-icon>Esqueci a senha
				</a>
				<div>
					<form method ='POST' action="#">
						<g-callout>
							<p>
								Entre com o seu login ou email na caixa de texto
								abaixo e clique em <strong>Enviar código</strong>.

								Uma código será enviado para seu endereço de email
								e deverá ser utilizado para redefinir sua senha
								clicando em <strong>Redefinir senha</strong>.
							</p>
						</g-callout>
						<fieldset>
							<label>
								Login ou E-Mail:
								<span>
									<input type="text" required='required' maxlength='64'
									       tabindex="1" title='Entre com o seu login ou e-mail'/>
								</span>
							</label>
						</fieldset>
						<g-coolbar>
							<button id='forgot-button' class="primary">
								Enviar código<g-icon>&#X2034;</g-icon>
							</button>
						</g-coolbar>
					</form>
				</div>
				<a id='reset-tab' href='#'>
					<g-icon>&#X2058;</g-icon>Redefinir a senha
				</a>
				<div>
					<form method ='POST' action="#">
						<fieldset>
							<label>
								Entre com o código recebido no email:
								<span style='flex-basis: 60px'>
									<textarea required='required' tabindex="1" title='Entre com o código recebido no email'></textarea>
								</span>
							</label>
							<label style='grid-column: 1 / span 8'>
								Entre com a nova senha:
								<span>
									<input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no mínimo 8 caracteres' pattern='^.{8,}$'/>
								</span>
							</label>
							<label style='grid-column: 9 / span 8'>
								Repita a nova senha:
								<span>
									<input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no mínimo 8 caracteres' pattern='^.{8,}$'/>
								</span>
							</label>
						</fieldset>
						<g-coolbar>
							<button id='reset-button' class="primary">
								Redefinir<g-icon>&#X2058;</g-icon>
							</button>
						</g-coolbar>
					</form>
				</div>
			</g-tab-control>
		</section>
	</main>
 <style data-element="g-login-form">* {
	box-sizing: border-box;
}

g-tab-control > a {
	flex-grow: 1;
	flex-shrink: 0;
}

:host(*)
{
	top: 0;
	left: 0;
	bottom: 0;
	right: 0;
	position: absolute;
	display: flex;
	align-items: center;
	justify-content: center;
}

main
{
	width: 600px;
	min-width: 320px;
	max-width: calc(100% - 16px);
	border: 1px solid var(--main4);
	background-color: var(--main3);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

header
{
	gap: 8px;
	padding: 8px;
	height: 50px;
	color: white;
	display: flex;
	align-items: center;
	background-color: var(--base2);
}

#logo {
	width: 32px;
	height: 32px;
}

#app
{
	flex-grow: 1;
	font-size: 20px;
}

#version {
	font-size: 12px;
}

section
{
	padding: 10px;
}

form
{
	gap: 12px;
	display: flex;
	flex-direction: column;
}

:host([setup-password='false']) #setup-tab,
:host([setup-password='false']) #forgot-tab,
:host([setup-password='false']) #reset-tab
{
	display: none;
}

@media screen and (max-width: 600px)
{


	:host(*)
	{
		align-items: flex-start;
	}

	main
	{
		width: 100%;
		border: none;
		height: 100%;
		box-shadow: none;
		max-width: unset;
		background-color: var(--main3);
	}
}</style>`;
/* global customElements */

import './g-icon.js';
import './g-coolbar.js';
import './g-tab-control.js';
import stylesheets from './stylesheets.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

customElements.define('g-login-form', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		stylesheets('input.css', 'fieldset.css').forEach(e => this.shadowRoot.appendChild(e));

		let setup = this.shadowRoot.querySelector("#setup-button");
		setup.addEventListener("click", event =>
		{
			event.preventDefault();
			let username = setup.closest("form").querySelectorAll("input")[0];
			username.value = username.value.trim();
			let password = setup.closest("form").querySelectorAll("input")[1];
			password.value = password.value.trim();
			let change = setup.closest("form").querySelectorAll("input")[2];
			change.value = change.value.trim();
			let repeat = setup.closest("form").querySelectorAll("input")[3];
			repeat.value = repeat.value.trim();

			if (!username.reportValidity()
				|| !password.reportValidity()
				|| !change.reportValidity()
				|| !repeat.reportValidity())
				return;

			if (username.value === change.value)
				return GMessageDialog.error("A nova senha não pode ser igual ao login");

			if (change.value !== repeat.value)
				return GMessageDialog.error("As duas senhas não conferem");

			let creadentials = btoa(username.value + ':' + password.value);
			fetch(new Request("SetupPassword", {method: "post",
				headers: {Authorization: `Basic ${creadentials}`}, body: change.value}))
				.then(ResponseHandler.none)
				.then(() =>
				{
					this.shadowRoot.querySelector("#login-tab").click();
					GMessageDialog.success("Senha alterada com sucesso");
					username.value = password.value = change.value = repeat.value = "";
				})
				.catch(error => GMessageDialog.error(error.message));
		});

		let forgot = this.shadowRoot.querySelector("#forgot-button");
		forgot.addEventListener("click", event =>
		{
			event.preventDefault();
			let username = forgot.closest("form").querySelectorAll("input")[0];
			username.value = username.value.trim();

			if (!username.reportValidity())
				return;

			fetch(`ResetPassword?username=${username.value}`)
				.then(ResponseHandler.none)
				.then(() =>
				{
					this.shadowRoot.querySelector("#reset-tab").click();
					GMessageDialog.success("Código enviado com sucesso");
					username.value = "";
				})
				.catch(error => GMessageDialog.error(error.message));
		});

		let reset = this.shadowRoot.querySelector("#reset-button");
		reset.addEventListener("click", event =>
		{
			event.preventDefault();

			let code = reset.closest("form").querySelector("textarea");
			code.value = code.value.trim();
			let change = reset.closest("form").querySelectorAll("input")[0];
			change.value = change.value.trim();
			let repeat = reset.closest("form").querySelectorAll("input")[1];
			repeat.value = repeat.value.trim();

			if (!code.reportValidity()
				|| !change.reportValidity()
				|| !repeat.reportValidity())
				return;

			if (change.value !== repeat.value)
				return GMessageDialog.error("As duas senhas não conferem");

			fetch(new Request("ResetPassword", {method: "post",
				headers: {Authorization: `Bearer ${code.value}`}, body: change.value}))
				.then(ResponseHandler.none)
				.then(() =>
				{
					this.shadowRoot.querySelector("#login-tab").click();
					GMessageDialog.success("Senha alterada com sucesso");
					code.value = change.value = repeat.value = "";
				})
				.catch(error => GMessageDialog.error(error.message));
		});
	}

	attributeChangedCallback(name, _, val)
	{
		switch (name)
		{
			case "action":
				return this.shadowRoot.querySelector("#form").action = val;
			case "method":
				return this.shadowRoot.querySelector("#form").method = val;
			case "target":
				return this.shadowRoot.querySelector("#form").target = val;
			case "logo":
				return this.shadowRoot.querySelector("#logo").src = val;
			case "app":
				return this.shadowRoot.querySelector("#app").innerText = val;
			case "version":
				return this.shadowRoot.querySelector("#version").innerText = `Versão ${val}`;
		}
	}

	static get observedAttributes()
	{
		return ["action", "method", "target", "logo", "app", "version"];
	}
});
