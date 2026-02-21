let t=document.createElement("template");t.innerHTML=`
	<dialog><header>
			Selecione uma data
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-date-selector></g-date-selector></section></dialog>
<style data-element="g-date-picker">dialog{height:440px;min-width:320px;max-width:600px;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-date-selector{flex-grow:1}
</style>`;import"./g-icon.js";import"./g-date-selector.js";import o from"./g-window.js";export default class i extends o{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+t.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel"))),this.shadowRoot.querySelector("g-date-selector").addEventListener("selected",e=>this.dispatchEvent(new CustomEvent("commit",{detail:e.detail})))}static pick(){let e=window.top.document.createElement("g-date-picker");return e.show(),new Promise((n,a)=>{e.addEventListener("commit",d=>n(d.detail)),e.addEventListener("cancel",()=>a(new Error("Cancel")))})}}customElements.define("g-date-picker",i);

//# sourceMappingURL=g-date-picker.js.map
