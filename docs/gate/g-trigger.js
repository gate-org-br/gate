let o=document.createElement("template");o.innerHTML=`
	<slot></slot>
`;export default class s extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(o.content.cloneNode(!0)),this.addEventListener("mouseenter",()=>{Array.from(this.children).filter(e=>e.issubmenu).forEach(e=>e.show(this))}),this.addEventListener("mouseleave",()=>{Array.from(this.children).filter(e=>e.issubmenu).forEach(e=>e.hide())}),this.addEventListener("click",function(e){const t=Array.from(this.children).find(r=>r.show&&!e.composedPath().includes(r));t&&(t.show(e),e.preventDefault(),e.stopPropagation(),e.stopImmediatePropagation())},{capture:!0})}}customElements.define("g-trigger",s);

//# sourceMappingURL=g-trigger.js.map
