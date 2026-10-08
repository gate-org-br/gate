import n from"./dom.js";import c from"./data-url.js";window.addEventListener("@dataset",function(e){let o=e.composedPath(),i=o[0]||e.target,{parameters:[l,...r]}=e.detail,s=n.navigate(i,l).orElseThrow(`${l} is not a valid selector`);r=new Set(r.map(t=>parseInt(t,10)).filter(t=>Number.isInteger(t)&&t>=0));let m=Array.from(s.querySelectorAll("thead > tr, tbody > tr")).map(t=>Array.from(t.children).filter((a,d)=>!r.size||r.has(d)).map(a=>a.getAttribute("data-value")??a.textContent));e.success(o,c.ofJSON(m))});

//# sourceMappingURL=@dataset.js.map
