import i from"./trigger-core.js";import s from"./trigger-extractor.js";window.addEventListener("mouseover",function(r){let e=r.target||r.composedPath()[0],t=s.trigger(e);if(!t)return!1;if(t.startsWith("hover(")&&t.endsWith(")")){const o=setTimeout(()=>i(r,e),Number(t.slice(6,-1))*1e3);e.addEventListener("mouseleave",()=>clearTimeout(o),{once:!0})}else if(t==="hover"){const o=setTimeout(()=>i(r,e),1e3);e.addEventListener("mouseleave",()=>clearTimeout(o),{once:!0})}else t==="mouseenter"&&i(r,e)});

//# sourceMappingURL=trigger-hover.js.map
