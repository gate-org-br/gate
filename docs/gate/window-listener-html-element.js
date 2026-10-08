export default class o extends HTMLElement{#e=[];addWindowListener(n,t,e){this.#e.push({event:n,listener:t,options:e}),this.isConnected&&window.addEventListener(n,t,e)}connectedCallback(){for(const{event:n,listener:t,options:e}of this.#e)window.addEventListener(n,t,e)}disconnectedCallback(){for(const{event:n,listener:t,options:e}of this.#e){const s=typeof e=="boolean"?e:e?.capture??!1;window.removeEventListener(n,t,s)}}}

//# sourceMappingURL=window-listener-html-element.js.map
