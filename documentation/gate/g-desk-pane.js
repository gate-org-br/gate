let s=document.createElement("template");s.innerHTML=`
	<slot></slot>
<style data-element="g-desk-pane">*{box-sizing:border-box}:host(*){gap:16px;width:100%;color:#000;display:grid;font-size:16px;grid-auto-rows:200px;background-color:transparent;grid-template-columns:repeat(auto-fill,minmax(200px,1fr))}::slotted(a),::slotted(button),::slotted(.g-command),::slotted(g-desk-pane),::slotted(g-desk-pane-reset){gap:10px;margin:0;padding:16px;color:inherit;font-size:inherit;position:relative;text-align:center;border-radius:3px;font-style:normal;text-decoration:none;border:1px solid #F0F0F0;display:grid;align-items:center;justify-items:center;justify-content:center;grid-template-rows:1fr 1fr}::slotted(:hover){background-color:#fffacd}:host([child]){cursor:pointer}:host([child]) ::slotted(a),:host([child]) ::slotted(button),:host([child]) ::slotted(.g-command),:host([child]) ::slotted(g-desk-pane){display:none}:host([child]):after{right:8px;bottom:4px;font-family:gate;content:"\\3017";position:absolute;color:var(--main6)}::slotted(g-desk-pane-reset):before{color:#600;font-size:48px;cursor:pointer;content:"\\2023";font-family:gate}::slotted(g-desk-pane-reset):after{color:#600;cursor:pointer;content:"Return"}:host(.small){font-size:12px;grid-auto-rows:100px;grid-template-columns:repeat(auto-fill,minmax(100px,1fr))}:host(.small) ::slotted(g-desk-pane-reset):before{font-size:32px}:host(.inline){grid-auto-rows:80px;grid-template-columns:repeat(auto-fill,minmax(320px,1fr))}:host(.inline) ::slotted(a),:host(.inline) ::slotted(button),:host(.inline) ::slotted(.g-command),:host(.inline) ::slotted(g-desk-pane),:host(.inline) ::slotted(g-desk-pane-reset){display:flex;align-items:center;justify-content:flex-start}:host(.inline.small){grid-auto-rows:40px;grid-template-columns:repeat(auto-fill,minmax(160px,1fr))}::slotted(:is(a,button,.g-command)[data-loading]){position:relative}::slotted(:is(a,button,.g-command)[data-loading]):before{inset:0;color:#000;display:flex;padding:32px;font-size:48px;content:"\\2017";font-family:gate;position:absolute;border-radius:inherit;justify-content:center;background-color:#f0f0f0}::slotted(:is(a,button,.g-command)[data-loading]):after{bottom:48px;content:"";height:24px;max-width:80%;position:absolute;animation-fill-mode:both;background-color:var(--base1);animation:loading 2s infinite ease-in-out}:host(.small) ::slotted(:is(a,button,.g-command)[data-loading]):before{padding:16px;font-size:24px}:host(.small) ::slotted(:is(a,button,.g-command)[data-loading]):after{bottom:24px;height:16px}:host(.inline) ::slotted(:is(a,button,.g-command)[data-loading]):before{padding:16px;font-size:32px;align-items:center;justify-content:flex-start}:host(.inline) ::slotted(:is(a,button,.g-command)[data-loading]):after{left:64px;bottom:28px;max-width:calc(100% - 80px)}:host(.inline.small) ::slotted(:is(a,button,.g-command)[data-loading]):before{font-size:24px}:host(.inline.small) ::slotted(:is(a,button,.g-command)[data-loading]):after{left:48px;bottom:12px;max-width:calc(100% - 60px)}
</style>`;import"./g-icon.js";import i from"./loading.js";document.head.insertAdjacentHTML("beforeend",`<style>
		g-desk-pane img {
			order: -1;
			width: 48px;
			height: 48px
		}
		g-desk-pane i, g-desk-pane e, g-desk-pane g-icon {
			order: -1;
			font-size: 48px
		}
		g-desk-pane.small img {
			width: 32px;
			height: 32px
		}
		g-desk-pane.small i, g-desk-pane.small e, g-desk-pane.small g-icon {
			font-size: 24px
		}
	</style>`),customElements.define("g-desk-pane",class extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.appendChild(s.content.cloneNode(!0)),this.addEventListener("click",function(t){let e=this.parentNode;if(e&&e.tagName==="G-DESK-PANE"){t.preventDefault(),t.stopPropagation();let n=e.buttons;this.buttons.forEach(o=>e.appendChild(o)),n.forEach(o=>o.remove());let a=e.appendChild(document.createElement("g-desk-pane-reset"));a.addEventListener("click",()=>{t.preventDefault(),t.stopPropagation(),a.remove(),e.buttons.forEach(o=>this.appendChild(o)),n.forEach(o=>e.appendChild(o))})}},!0)}get buttons(){return Array.from(this.children).filter(t=>t.tagName==="A"||t.tagName==="BUTTON"||t.tagName==="G-DESK-PANE"||t.tagName==="G-DESK-PANE-RESET"||t.classList.contains(".g-command"))}connectedCallback(){i(this.parentNode),this.parentNode.tagName==="G-DESK-PANE"&&this.setAttribute("child","")}}),customElements.define("g-desk-pane-reset",class extends HTMLElement{constructor(){super()}});

//# sourceMappingURL=g-desk-pane.js.map
