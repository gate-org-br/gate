let n=document.createElement("template");n.innerHTML=`
	<dialog><header>
			Selecione um per\xEDodo
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-month-interval-selector></g-month-interval-selector></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-month-interval-picker">dialog{min-width:320px;max-width:600px;height:fit-content;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-month-interval-selector{flex-grow:1}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import c from"./g-window.js";import"./g-month-interval-selector.js";export default class i extends c{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+n.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),t=this.shadowRoot.querySelector("g-month-interval-selector");e.innerText=t.selection,t.addEventListener("selected",()=>e.innerText=t.selection),e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.innerText}))|this.hide())}static pick(){let e=window.top.document.createElement("g-month-interval-picker");return e.show(),new Promise((t,o)=>{e.addEventListener("cancel",()=>o(new Error("Cancel"))),e.addEventListener("commit",r=>t(r.detail))})}}customElements.define("g-month-interval-picker",i);

//# sourceMappingURL=g-month-interval-picker.js.map
