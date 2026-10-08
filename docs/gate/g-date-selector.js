let l=document.createElement("template");l.innerHTML=`
	<g-calendar max="1"></g-calendar>
<style data-element="g-date-selector">:host(*){display:flex;align-items:stretch;align-content:stretch;justify-content:center}g-calendar{flex-grow:1}
</style>`;import"./g-calendar.js";import n from"./date-format.js";customElements.define("g-date-selector",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.innerHTML=l.innerHTML;let t=this.shadowRoot.firstElementChild;t.addEventListener("remove",e=>e.preventDefault()),t.addEventListener("update",()=>this.dispatchEvent(new CustomEvent("selected",{detail:this.selection})))}get selection(){let e=this.shadowRoot.firstElementChild.selection();return e.length?n.DATE.format(e[0]):null}set date(t){let e=this.shadowRoot.firstElementChild;e.clear(),e.select(t)}get date(){let e=this.shadowRoot.firstElementChild.selection();return e.length?e[0]:null}});

//# sourceMappingURL=g-date-selector.js.map
