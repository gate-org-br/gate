let n=document.createElement("template");n.innerHTML=`
	<fieldset></fieldset>
<style data-element="g-form-view">*{box-sizing:border-box}:host(*){display:flex;align-items:stretch;flex-direction:column;justify-content:stretch}fieldset{padding:0;border:none}span.multiple{padding:4px;text-indent:0;line-height:24px;white-space:pre-wrap;flex-basis:fit-content}
</style>`;import r from"./stylesheets.js";customElements.define("g-form-view",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=n.innerHTML,r("input.css","fieldset.css").forEach(a=>this.shadowRoot.appendChild(a))}set value(a){let i=this.shadowRoot.querySelector("fieldset");Array.from(i.children).forEach(s=>s.remove()),a&&a.forEach(s=>{let t=s.schema,e=i.appendChild(document.createElement("label"));if(t.size)switch(Number(t.size)){case 0:e.setAttribute("data-size",1);break;case 1:e.setAttribute("data-size",2);break;case 2:e.setAttribute("data-size",4);break;case 3:e.setAttribute("data-size",8);break}else t.columns&&e.setAttribute("data-size",t.columns);e.innerText=t.name+": ";let l=e.appendChild(document.createElement("span"));t.multiple&&(l.className="multiple"),s.value&&(l.innerHTML=s.value.join(`
`))})}attributeChangedCallback(){this.value=JSON.parse(this.getAttribute("value"))}static get observedAttributes(){return["value"]}});

//# sourceMappingURL=g-form-view.js.map
