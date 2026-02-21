import n from"./dom.js";import c from"./data-url.js";window.addEventListener("@dataset",function(t){let a=t.composedPath(),i=a[0]||t.target,{action:e,parameters:[r]}=t.detail,l=n.navigate(i,r||e).orElseThrow(`${r||e} is not a valid selector`),s=Array.from(l.querySelectorAll("thead > tr, tbody > tr")).map(d=>Array.from(d.children).map(o=>o.getAttribute("data-value")||o.innerText));t.success(a,new c("application/json",JSON.stringify(s)))});

//# sourceMappingURL=@dataset.js.map
