let n=document.createElement("template");n.innerHTML=`
	<dialog><header>
			Selecione um per\xEDodo
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-date-interval-selector></g-date-interval-selector></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-date-interval-picker">dialog{height:440px;min-width:320px;max-width:600px;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-date-interval-selector{flex-grow:1}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import r from"./g-window.js";import"./g-date-interval-selector.js";export default class o extends r{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+n.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),t=this.shadowRoot.querySelector("g-date-interval-selector"),i=new Date;t.min=i,t.max=i,e.innerText=t.selection,t.addEventListener("selected",()=>e.innerText=t.selection),e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.innerText})))}static pick(){let e=window.top.document.createElement("g-date-interval-picker");return e.show(),new Promise((t,i)=>{e.addEventListener("cancel",()=>i(new Error("Cancel"))),e.addEventListener("commit",a=>t(a.detail))})}}customElements.define("g-date-interval-picker",o);

//# sourceMappingURL=g-date-interval-picker.js.map
