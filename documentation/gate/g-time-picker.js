let i=document.createElement("template");i.innerHTML=`
	<dialog><header>
			Selecione uma hora
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-time-selector></g-time-selector></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-time-picker">dialog{height:440px;min-width:320px;max-width:600px;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-time-selector{flex-grow:1}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import"./g-time-selector.js";import r from"./g-window.js";export default class n extends r{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+i.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),t=this.shadowRoot.querySelector("g-time-selector");e.innerText=t.selection,t.addEventListener("selected",()=>e.innerText=t.selection),e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.innerText})))}static pick(){let e=window.top.document.createElement("g-time-picker");return e.show(),new Promise((t,o)=>{e.addEventListener("commit",c=>t(c.detail)),e.addEventListener("cancel",()=>o(new Error("Cancel")))})}}customElements.define("g-time-picker",n);

//# sourceMappingURL=g-time-picker.js.map
