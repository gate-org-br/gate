function l(e,o=new Set){const n=e.href||e.ownerNode?.textContent;if(o.has(n))return"";o.add(n);let s="";try{for(const t of e.cssRules)t instanceof CSSImportRule?s+=l(t.styleSheet,o):s+=t.cssText+`
`}catch{}return s}let c="";for(const e of document.styleSheets)c+=l(e);const r=new CSSStyleSheet;r.replaceSync(c);export default class S extends HTMLElement{constructor(){super(),this.attachShadow({mode:"open"}),this.shadowRoot.adoptedStyleSheets=[r]}}

//# sourceMappingURL=styled-html-element.js.map
