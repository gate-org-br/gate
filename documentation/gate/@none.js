import"./trigger.js";import d from"./request-builder.js";window.addEventListener("@none",function(t){let e=t.composedPath(),{method:o,action:i,form:r}=t.detail;return fetch(d.build(o,i,r)).then(()=>t.success(e)).catch(c=>t.failure(e,c))});

//# sourceMappingURL=@none.js.map
