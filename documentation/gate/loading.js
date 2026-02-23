const d="gate-loading-style",o=`@keyframes loading
	{
		0% { width: 0 }
		100% { width: 100% }
	}`;document.head.insertAdjacentHTML("beforeend",`<style id='${d}'>${o}</style>`);export default function n(e){if(e?.parentNode?.shadowRoot&&!e.parentNode.shadowRoot.getElementById(d)){const t=document.createElement("style");t.id=d,t.innerHTML=o,e.parentNode.shadowRoot.appendChild(t)}}

//# sourceMappingURL=loading.js.map
