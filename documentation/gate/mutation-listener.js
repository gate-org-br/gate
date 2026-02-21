export default class e{#t;#e;#s=new Set;constructor(t,s={childList:!0,subtree:!0,attributes:!0}){this.#t=new MutationObserver(t),this.#e={...s}}has(t){return this.#s.has(t)}listen(t){this.#s.has(t)||(this.#s.add(t),this.#t.observe(t,this.#e))}pause(){this.#t.disconnect()}resume(){this.#s.forEach(t=>this.#t.observe(t,this.#e))}}

//# sourceMappingURL=mutation-listener.js.map
