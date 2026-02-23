let n=document.createElement("template");n.innerHTML=`
	<dialog><header>
			Selecione um m\xEAs
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-month-selector></g-month-selector></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-month-picker">dialog{height:440px;min-width:320px;max-width:600px;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-month-selector{flex-grow:1}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import"./g-month-selector.js";import s from"./g-window.js";export default class o extends s{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+n.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),t=this.shadowRoot.querySelector("g-month-selector");e.innerText=t.selection,t.addEventListener("selected",()=>e.innerText=t.selection),e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.innerText})))}static pick(){let e=window.top.document.createElement("g-month-picker");return e.show(),new Promise((t,i)=>{e.addEventListener("cancel",()=>i(new Error("Cancel"))),e.addEventListener("commit",c=>t(c.detail))})}}customElements.define("g-month-picker",o);

//# sourceMappingURL=g-month-picker.js.map
