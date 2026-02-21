let s=document.createElement("template");s.innerHTML=`
	<dialog><header part='header'><label>
				Selecione
			</label><a id='close' href="#"><g-icon>&#X1011;</g-icon></a></header><section part='section'><g-selectn id='select' part='selector'></g-selectn></section><footer part='footer'><g-coolbar><button id='commit' class="primary">
					Concluir
					<g-icon>
						&#X1000;
					</g-icon></button><hr/><button id='cancel' class="tertiary">
					Cancelar
					<g-icon>
						&#X1001;
					</g-icon></button></g-coolbar></footer></dialog>
<style data-element="g-selectn-picker">dialog{min-width:320px;max-width:800px;height:fit-content;width:calc(100% - 40px)}section{flex-basis:400px}g-selectn{flex-grow:1}
</style>`;import"./g-icon.js";import"./g-selectn.js";import"./g-coolbar.js";import d from"./g-window.js";import r from"./response-handler.js";export default class c extends d{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.appendChild(s.content.cloneNode(!0)),this.shadowRoot.getElementById("close").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),o=this.shadowRoot.getElementById("select");e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:o.value})))}set caption(e){this.shadowRoot.querySelector("label").innerHTML=e}get caption(){return this.shadowRoot.querySelector("label").innerHTML}set options(e){setTimeout(()=>this.shadowRoot.querySelector("g-selectn").options=e,0)}get options(){return this.shadowRoot.querySelector("g-selectn").options}set values(e){setTimeout(()=>this.shadowRoot.querySelector("g-selectn").value=e,0)}get values(){return this.shadowRoot.querySelector("g-selectn").value}static pick(e,o,i){if(typeof e=="string")return fetch(e).then(t=>r(t)).then(t=>c.pick(t,o,i));if(typeof i=="string")return fetch(i).then(t=>r(t)).then(t=>c.pick(e,o,t));let n=window.top.document.createElement("g-selectn-picker");return n.options=e,i&&(n.values=i),o&&(n.caption=o),n.show(),new Promise((t,l)=>{n.addEventListener("commit",a=>t(a.detail)),n.addEventListener("cancel",()=>l(new Error("Cancel")))})}}customElements.define("g-selectn-picker",c);

//# sourceMappingURL=g-selectn-picker.js.map
