let n=document.createElement("template");n.innerHTML=`
	<dialog><header>
			Selecione um per\xEDodo
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-date-time-interval-selector></g-date-time-interval-selector></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-date-time-interval-picker">dialog{min-width:320px;max-width:600px;height:fit-content;width:calc(100% - 40px)}dialog>section{align-items:stretch}g-date-time-interval-selector{flex-grow:1}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import r from"./g-window.js";import"./g-date-time-interval-selector.js";export default class o extends r{constructor(){super(),this.addEventListener("cancel",()=>this.hide()),this.addEventListener("commit",()=>this.hide()),this.shadowRoot.innerHTML=this.shadowRoot.innerHTML+n.innerHTML,this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.dispatchEvent(new CustomEvent("cancel")));let e=this.shadowRoot.getElementById("commit"),t=this.shadowRoot.querySelector("g-date-time-interval-selector"),i=new Date;t.min=i,t.max=i,e.innerText=t.selection,t.addEventListener("selected",()=>e.innerText=t.selection),e.addEventListener("click",()=>this.dispatchEvent(new CustomEvent("commit",{detail:e.innerText}))|this.hide())}static pick(){let e=window.top.document.createElement("g-date-time-interval-picker");return e.show(),new Promise((t,i)=>{e.addEventListener("cancel",()=>i(new Error("Cancel"))),e.addEventListener("commit",a=>t(a.detail))})}}customElements.define("g-date-time-interval-picker",o);

//# sourceMappingURL=g-date-time-interval-picker.js.map
