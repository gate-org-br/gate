let r=document.createElement("template");r.innerHTML=`
	<dialog><header>
			Progresso
			<a id='close' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-progress-status></g-progress-status></section><footer><button id='commit'>
				Processando
			</button></footer></dialog>
<style data-element="g-progress-dialog">dialog{height:fit-content;min-width:320px;max-width:800px;width:calc(100% - 40px)}dialog>footer>button{flex-grow:1;justify-content:center}
</style>`;import"./g-icon.js";import"./trigger.js";import"./g-progress-status.js";import i from"./g-window.js";customElements.define("g-progress-dialog",class extends i{constructor(){super(),this.shadowRoot.appendChild(r.content.cloneNode(!0));let t=this.shadowRoot.getElementById("close"),e=this.shadowRoot.getElementById("commit");e.onclick=t.onclick=o=>{o.preventDefault(),o.stopPropagation(),confirm("Tem certeza de que deseja fechar o progresso?")&&this.hide()},window.addEventListener("ProcessCommited",o=>{o.detail.id===this.process&&(e.innerHTML="Ok",e.style.color=getComputedStyle(document.documentElement).getPropertyValue("--question1"),e.onclick=t.onclick=s=>{s.preventDefault(),s.stopPropagation(),this.hide()})}),window.addEventListener("ProcessCanceled",o=>{o.detail.id===this.process&&(e.innerHTML="OK",e.style.color=getComputedStyle(document.documentElement).getPropertyValue("--error1"),e.onclick=t.onclick=s=>{s.preventDefault(),s.stopPropagation(),this.hide()})})}set process(t){this.setAttribute("process",t)}get process(){return this.getAttribute("process")}attributeChangedCallback(t){this.shadowRoot.querySelector("g-progress-status").process=this.process}static get observedAttributes(){return["process"]}}),window.addEventListener("ProcessRequest",function(t){let e=window.top.document.createElement("g-progress-dialog");e.process=t.detail.id,e.caption=t.detail.name,e.show()});

//# sourceMappingURL=g-progress-dialog.js.map
