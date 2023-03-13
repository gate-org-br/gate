let template = document.createElement("template");
template.innerHTML = `
	<link rel='stylesheet'
	      type='text/css' href='./gate/input.css'/>
	<link rel='stylesheet'
	      type='text/css' href='./gate/fieldset.css'/>

	<form class="Login" method='POST' target='_top' action='Gate'>
		<main>
			<header>
				<slot>
				</slot>
			</header>
			<section>
				<g-tab-control>
					<a href='#' data-selected="true">
						<g-icon>
							&#X2000;
						</g-icon>
						Fazer Logon
					</a>
					<div>
						<form method="post" action="#">
							<fieldset>
								<label>
									Login:
									<span>
										<g-icon>&#X2004;</g-icon>
										<input type="text" required='required' name='$username' maxlength='64' tabindex="1"
										       title='Entre com o seu login.' />
									</span>
								</label>
								<label>
									Senha:
									<span>
										<input type="password" required='required' name='$password' maxlength='32'
										       tabindex="1" title='Entre com a sua senha.' />
									</span>
								</label>
							</fieldset>

							<g-coolbar>
								<button class="primary">
									Entrar<g-icon>&#X1000;</g-icon>
								</button>
							</g-coolbar>
						</form>
					</div>
					<a id='setup-password' href='#'>
						<g-icon>
							&#X2002;
						</g-icon>
						Trocar Senha
					</a>
					<div>
						<form method="post" action="SetupPassword">
							<fieldset>
								<label data-size="8">
									Login:
									<span>
										<g-icon>
											&#X2004;
										</g-icon>
										<input type='TEXT' required='required' name='user.username' maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label data-size="8">
									Senha atual:
									<span>
										<input type='PASSWORD' required='required' name='user.password' maxlength='64' tabindex='1' title='Entre com a sua senha atual.'/>
									</span>
								</label>
								<label  data-size="8">
									Nova senha:
									<span>
										<input type='PASSWORD' required='required' name='user.change' maxlength='64' tabindex='1' title='Entre com a sua nova senha.'/>
									</span>
								</label>
								<label  data-size="8">
									Repita a nova senha:
									<span>
										<input type='PASSWORD' required='required' name='user.repeat' maxlength='64' tabindex='1' title='Repita sua nova senha.'/>
									</span>
								</label>
							</fieldset>

							<g-coolbar>
								<button class='primary' tabindex='2'>
									Trocar
									<g-icon>&#X1000;</g-icon>
								</button>
							</g-coolbar>
						</form>
					</div>
					<a id='reset-password' href='#'>
						<g-icon>&#X2023;</g-icon>Recuperar Senha
					</a>
					<div>
						<form method="post" action="ResetPassword">
							<fieldset>
								<g-message>
									Entre com o seu login ou email na caixa de texto
									abaixo e clique em concluir. Uma nova senha será criada
									e enviada para seu endereço de email. Você
									poderá então permanecer com ela ou trocá-la por outra
									de sua escolha.
								</g-message>
								<fieldset>
									<legend>
										Login ou e-mail
									</legend>
									<label>
										<span>
											<g-icon>
												&#X2004;
											</g-icon>
											<input type='TEXT' required='required' name='user.username'
											       maxlength='64' tabindex='1' title='Entre com o seu login.'/>
										</span>
									</label>
								</fieldset>
							</fieldset>

							<g-coolbar>
								<button class='primary' tabindex='2'>
									Recuperar
									<g-icon>&#X1000;</g-icon>
								</button>
							</g-coolbar>
						</form>
					</div>

					<a id='create-account' href='#'>
						<g-icon>&#X2004;</g-icon>Criar Conta
					</a>
					<div>
						<form method="post" action="CreateAccount">
							<fieldset>
								<label>
									Nome:
									<span>
										<g-icon>&#X2004</g-icon>
										<input type='TEXT' required='required' name='user.name'
										       maxlength='64' tabindex='1' title='Entre com o seu nome.'/>
									</span>
								</label>
								<label>
									Login:
									<span>
										<g-icon>&#X2096</g-icon>
										<input type='TEXT' required='required' name='user.username'
										       maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label data-size="8">
									Senha:
									<span>
										<g-icon>&#X2002</g-icon>
										<input type='PASSWORD' required='required' name='user.password'
										       maxlength='32' tabindex='1' title='Entre com a sua senha.'/>
									</span>
								</label>
								<label data-size="8">
									Repita:
									<span>
										<g-icon>&#X2002</g-icon>
										<input type='PASSWORD' required='required' name='user.repeat'
										       maxlength='32' tabindex='1' title='repita sua senha.'/>
									</span>
								</label>
								<label>
									Empresa:
									<span>
										<g-icon>&#X2048</g-icon>
										<input type='TEXT' required='required' name='user.description'
										       maxlength='256' tabindex='1' title='Entre com o nome da empresa onde trabalha.'/>
									</span>
								</label>
								<label>
									E-Mail:
									<span>
										<g-icon>&#X2034</g-icon>
										<input type='TEXT' required='required' name='user.email'
										       maxlength='64' tabindex='1' title='Entre com o seu endere&ccedil;o de email.'/>
									</span>
								</label>
								<label data-size="8">
									Telefone:
									<span>

										<g-icon>&#X2223</g-icon>
										<input type='TEXT' name='user.phone'
										       maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone fixo.'/>
									</span>
								</label>
								<label data-size="8">
									Celular:
									<span>
										<g-icon>&#X2145</g-icon>
										<input type='TEXT' name='user.cellPhone'
										       maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone celular.'/>
									</span>
								</label>
							</fieldset>
							<g-coolbar>
								<button class='primary' tabindex='2'>
									Criar
									<g-icon>&#X1000;</g-icon>
								</button>
							</g-coolbar>
						</form>
					</div>
				</g-tab-control>
			</section>
		</main>
	</form>
 <style>:host(*)
{
	height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
}

#setup-password,
#reset-password,
#create-account {
	display: none;
}

:host([setup-password]) #setup-password
{
	display: flex;
}

:host([reset-password]) #reset-password
{
	display: flex;
}

:host([create-account]) #create-account
{
	display: flex;
}

main {
	width: 600px;
	min-width: 320px;
	background-color: #F8F8F8;
	max-width: calc(100% - 16px);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

header
{
	gap: 8px;
	padding: 8px;
	height: 50px;
	color: white;
	display: flex;
	align-items: stretch;
	background-color: #3f729b;
	justify-content: space-around;
}

::slotted(img) {
	width: 32px;
	display: flex;
	align-items: center;
	justify-content: center;
}

::slotted(*:nth-child(2)) {
	flex-grow: 1;
	display: flex;
	font-size: 20px;
	align-items: center;
	justify-content: flex-start;
}

::slotted(*:nth-child(3)) {
	display: flex;
	font-size: 12px;
	align-items: flex-start;
	justify-content: flex-end;
}


section
{
	padding: 10px;
}

a
{

	grid-column: span 1;

}

footer
{
	padding: 10px;
}

@media only screen and (min-width: 768px)
{
	a {
		grid-column: span 2;
	}

}</style>`;

/* global customElements, template */

import './g-icon.mjs';
import './g-message.mjs';

customElements.define('g-login', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
	}

	attributeChangedCallback()
	{
		this.shadowRoot.querySelector("form")
			.setAttribute("action", this.getAttribute("action"));
	}

	static get observedAttributes() {
		return ['action'];
	}

});