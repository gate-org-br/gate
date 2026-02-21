export default function o(t,n){for(let e=t.parentNode;e;e=e.parentNode)if(e===document){document.head.insertAdjacentHTML("beforeend",n.innerHTML);return}else if(e instanceof ShadowRoot){e.querySelector(`[data-element='${t.getAttribute("is")||t.tagName}]'`)||e.appendChild(n.content.cloneNode(!0));return}}

//# sourceMappingURL=apply-template.js.map
