let m=document.createElement("template");m.innerHTML=`
	<dialog><header><label id="title"></label><a id="cancel" href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><main><section><fieldset><label data-size="2">
							Date
							<span id="timestamp"></span></label><label data-size="2">
							Status
							<span id="status"></span></label><label data-size="4">
							Method
							<span id="method"></span></label><label data-size="8">
							URI
							<span id="uri"></span></label></fieldset><table is="g-tree-grid" class="r7"><caption>ERRORS</caption><thead><tr><th>Exception</th><th>Message</th><th>Class</th><th>Method</th><th>File</th><th style="width: 80px">Line</th></tr></thead><tbody></tbody></table></section></main></section></dialog>
<style data-element="g-error-dialog">dialog{width:calc(100vw - 32px);height:calc(100vh - 32px)}th,td{overflow-wrap:anywhere}
</style>`;import"./g-icon.js";import"./g-tree-grid.js";import u from"./g-window.js";function b(n){return n==null?"":String(n)}function t(n,e,o){const s=n.insertCell();return s.innerText=b(e),o&&(s.dataset.column=o),s}export default class p extends u{constructor(){super(),this.shadowRoot.appendChild(m.content.cloneNode(!0)),this.shadowRoot.getElementById("cancel").addEventListener("click",()=>this.hide())}set detail(e){this.#e(e||{})}#e(e){const o=Array.isArray(e.errors)?e.errors:[];this.shadowRoot.getElementById("title").innerText="Erro",this.shadowRoot.getElementById("method").innerText=e.method,this.shadowRoot.getElementById("status").innerText=e.status,this.shadowRoot.getElementById("timestamp").innerText=new Date(e.timestamp).toLocaleString("pt-BR",{day:"2-digit",month:"2-digit",year:"numeric",hour:"2-digit",minute:"2-digit",second:"2-digit",hour12:!1}),this.shadowRoot.getElementById("uri").innerText=`${e.path}?${e.queryString}`;const s=this.shadowRoot.querySelector("table"),c=s.cloneNode(!0),h=c.querySelector("tbody");h.innerHTML="",o.forEach((d,g)=>{const r=d?.stackTrace?.[0]||{},a=h.insertRow();a.dataset.depth=0,a.toggleAttribute("data-expanded",g===0),t(a,d.type||d.simpleType),t(a,d.message),t(a,r.className),t(a,r.methodName),t(a,r.fileName),t(a,r.lineNumber>0?r.lineNumber:"","line"),(d.stackTrace||[]).forEach(l=>{const i=h.insertRow();i.dataset.depth=1,t(i,""),t(i,l.text),t(i,l.className),t(i,l.methodName),t(i,l.fileName),t(i,l.lineNumber>0?l.lineNumber:"","line")})}),s.replaceWith(c)}static error(e){const o=window.top.document.createElement("g-error-dialog");o.detail=e,o.show()}}customElements.define("g-error-dialog",p);

//# sourceMappingURL=g-error-dialog.js.map
