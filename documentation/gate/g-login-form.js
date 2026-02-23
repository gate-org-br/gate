let c=document.createElement("template");c.innerHTML=`
	<main><header><img id='logo' src='Logo.svg'><label id='app'></label><label id='version'></label></header><section><g-tab-control><a href='#' id='login-tab'><g-icon>&#X2004;</g-icon>Fazer logon
				</a><div><form id='form'
					      method ='POST'
					      target ='_top'
					      action='#'><fieldset><label>
								Login:
								<span><input type="text" required='required' name='$username' maxlength='64' tabindex="1" title='Entre com o seu login.'></span></label><label>
								Senha:
								<span><input type="password" required='required' name='$password' maxlength='32' tabindex="1" title='Entre com a sua senha.'></span></label></fieldset><g-coolbar><button class="primary">
								Entrar<g-icon>&#X2002;</g-icon></button></g-coolbar></form></div><a id='setup-tab' href='#'><g-icon>&#X2002;</g-icon>Trocar senha
				</a><div><form method ='POST' action="#"><fieldset><label style='grid-column: 1 / span 8'>
								Login:
								<span><input type="text" required='required' maxlength='64' tabindex="1" title='Entre com o seu login.'/></span></label><label style='grid-column: 9 / span 8'>
								Senha:
								<span><input type="password" required='required' maxlength='32' tabindex="1" title='Entre com a sua senha.'/></span></label><label style='grid-column: 1 / span 8'>
								Nova Senha:
								<span><input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no m\xEDnimo 8 caracteres' pattern='^.{8,}$'/></span></label><label style='grid-column: 9 / span 8'>
								Repita:
								<span><input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no m\xEDnimo 8 caracteres' pattern='^.{8,}$'/></span></label></fieldset><g-coolbar><button id='setup-button' class="primary">
								Trocar<g-icon>&#X2057;</g-icon></button></g-coolbar></form></div><a id='forgot-tab' href='#'><g-icon>&#X2034;</g-icon>Esqueci a senha
				</a><div><form method ='POST' action="#"><g-callout><p>
								Entre com o seu login ou email na caixa de texto
								abaixo e clique em <strong>Enviar c\xF3digo</strong>.

								Uma c\xF3digo ser\xE1 enviado para seu endere\xE7o de email
								e dever\xE1 ser utilizado para redefinir sua senha
								clicando em <strong>Redefinir senha</strong>.
							</p></g-callout><fieldset><label>
								Login ou E-Mail:
								<span><input type="text" required='required' maxlength='64'
									       tabindex="1" title='Entre com o seu login ou e-mail'/></span></label></fieldset><g-coolbar><button id='forgot-button' class="primary">
								Enviar c\xF3digo<g-icon>&#X2034;</g-icon></button></g-coolbar></form></div><a id='reset-tab' href='#'><g-icon>&#X2058;</g-icon>Redefinir a senha
				</a><div><form method ='POST' action="#"><fieldset><label>
								Entre com o c\xF3digo recebido no email:
								<span style='flex-basis: 60px'><textarea required='required' tabindex="1" title='Entre com o c\xF3digo recebido no email'></textarea></span></label><label style='grid-column: 1 / span 8'>
								Entre com a nova senha:
								<span><input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no m\xEDnimo 8 caracteres' pattern='^.{8,}$'/></span></label><label style='grid-column: 9 / span 8'>
								Repita a nova senha:
								<span><input type="password" required='required' maxlength='32' tabindex="1" title='A nova senha tem que ter no m\xEDnimo 8 caracteres' pattern='^.{8,}$'/></span></label></fieldset><g-coolbar><button id='reset-button' class="primary">
								Redefinir<g-icon>&#X2058;</g-icon></button></g-coolbar></form></div></g-tab-control></section></main>
<style data-element="g-login-form">*{box-sizing:border-box}g-tab-control>a{flex-grow:1;flex-shrink:0}:host(*){inset:0;position:absolute;display:flex;align-items:center;justify-content:center}main{width:600px;min-width:320px;max-width:calc(100% - 16px);border:1px solid var(--main4);background-color:var(--main3);box-shadow:6px 6px 6px #000000bf}header{gap:8px;padding:8px;height:50px;color:#fff;display:flex;align-items:center;background-color:var(--base2)}#logo{width:32px;height:32px}#app{flex-grow:1;font-size:20px}#version{font-size:12px}section{padding:10px}form{gap:12px;display:flex;flex-direction:column}:host([setup-password="false"]) #setup-tab,:host([setup-password="false"]) #forgot-tab,:host([setup-password="false"]) #reset-tab{display:none}@media screen and (max-width:600px){:host(*){align-items:flex-start}main{width:100%;border:none;height:100%;box-shadow:none;max-width:unset;background-color:var(--main3)}}
</style>`;import"./g-icon.js";import"./g-coolbar.js";import"./g-tab-control.js";import m from"./stylesheets.js";import r from"./g-message-dialog.js";import u from"./response-handler.js";customElements.define("g-login-form",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(c.content.cloneNode(!0)),m("input.css","fieldset.css").forEach(i=>this.shadowRoot.appendChild(i));let n=this.shadowRoot.querySelector("#setup-button");n.addEventListener("click",i=>{i.preventDefault();let e=n.closest("form").querySelectorAll("input")[0];e.value=e.value.trim();let t=n.closest("form").querySelectorAll("input")[1];t.value=t.value.trim();let a=n.closest("form").querySelectorAll("input")[2];a.value=a.value.trim();let s=n.closest("form").querySelectorAll("input")[3];if(s.value=s.value.trim(),!e.reportValidity()||!t.reportValidity()||!a.reportValidity()||!s.reportValidity())return;if(e.value===a.value)return r.error("A nova senha n\xE3o pode ser igual ao login");if(a.value!==s.value)return r.error("As duas senhas n\xE3o conferem");let d=btoa(e.value+":"+t.value);fetch(new Request("SetupPassword",{method:"post",headers:{Authorization:`Basic ${d}`},body:a.value})).then(u.none).then(()=>{this.shadowRoot.querySelector("#login-tab").click(),r.success("Senha alterada com sucesso"),e.value=t.value=a.value=s.value=""}).catch(p=>r.error(p.message))});let l=this.shadowRoot.querySelector("#forgot-button");l.addEventListener("click",i=>{i.preventDefault();let e=l.closest("form").querySelectorAll("input")[0];e.value=e.value.trim(),e.reportValidity()&&fetch(`ResetPassword?username=${e.value}`).then(u.none).then(()=>{this.shadowRoot.querySelector("#reset-tab").click(),r.success("C\xF3digo enviado com sucesso"),e.value=""}).catch(t=>r.error(t.message))});let o=this.shadowRoot.querySelector("#reset-button");o.addEventListener("click",i=>{i.preventDefault();let e=o.closest("form").querySelector("textarea");e.value=e.value.trim();let t=o.closest("form").querySelectorAll("input")[0];t.value=t.value.trim();let a=o.closest("form").querySelectorAll("input")[1];if(a.value=a.value.trim(),!(!e.reportValidity()||!t.reportValidity()||!a.reportValidity())){if(t.value!==a.value)return r.error("As duas senhas n\xE3o conferem");fetch(new Request("ResetPassword",{method:"post",headers:{Authorization:`Bearer ${e.value}`},body:t.value})).then(u.none).then(()=>{this.shadowRoot.querySelector("#login-tab").click(),r.success("Senha alterada com sucesso"),e.value=t.value=a.value=""}).catch(s=>r.error(s.message))}})}attributeChangedCallback(n,l,o){switch(n){case"action":return this.shadowRoot.querySelector("#form").action=o;case"method":return this.shadowRoot.querySelector("#form").method=o;case"target":return this.shadowRoot.querySelector("#form").target=o;case"logo":return this.shadowRoot.querySelector("#logo").src=o;case"app":return this.shadowRoot.querySelector("#app").innerText=o;case"version":return this.shadowRoot.querySelector("#version").innerText=`Vers\xE3o ${o}`;case"tab":return this.shadowRoot.querySelector(`#${o}-tab`)?this.shadowRoot.querySelector(`#${o}-tab`).click():null}}static get observedAttributes(){return["action","method","target","logo","app","version","tab"]}});

//# sourceMappingURL=g-login-form.js.map
