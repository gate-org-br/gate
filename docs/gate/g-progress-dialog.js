let e=document.createElement("template");e.innerHTML=`
	<dialog><header>
			Progresso
			<a id='close' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-progress-status></g-progress-status></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-progress-dialog">dialog{height:fit-content;min-width:320px;max-width:800px;width:calc(100% - 40px)}dialog>footer>button{flex-grow:1;justify-content:center}#commit:after{content:"Processando"}:host([status="commited"]) #commit,:host([status="redirect"]) #commit{color:var(--question1)}:host([status="canceled"]) #commit{color:var(--error1)}:host([status="commited"]) #commit:after,:host([status="canceled"]) #commit:after,:host([status="redirect"]) #commit:after{content:"Ok"}
</style>`;import"./g-icon.js";import"./trigger.js";import"./g-progress-status.js";import o from"./g-window.js";customElements.define("g-progress-dialog",class extends o{#t;#e;#s;constructor(){super(),this.shadowRoot.appendChild(e.content.cloneNode(!0)),this.#t=this.shadowRoot.querySelector("g-progress-status"),this.#e=this.shadowRoot.getElementById("commit");const s=this.shadowRoot.getElementById("close");this.#e.onclick=s.onclick=t=>{switch(t.preventDefault(),t.stopPropagation(),this.getAttribute("status")){case"commited":case"canceled":this.hide();break;case"redirect":window.location=this.#s;break;default:confirm("Tem certeza de que deseja fechar o progresso?")&&this.hide()}},this.addEventListener("Progress",t=>{this.#t.dispatchEvent(new CustomEvent("Progress",{detail:t.detail})),t.detail.status==="COMMITED"?this.setAttribute("status","commited"):t.detail.status==="CANCELED"&&this.setAttribute("status","canceled")}),this.addEventListener("Redirect",t=>{this.#s=t.detail.url,this.setAttribute("status","redirect")}),this.addEventListener("error",t=>this.#t.dispatchEvent(new CustomEvent("error",{detail:t.detail})))}});

//# sourceMappingURL=g-progress-dialog.js.map
