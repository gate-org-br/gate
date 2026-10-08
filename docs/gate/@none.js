import"./trigger.js";import d from"./request-builder.js";import s from"./response-handler.js";window.addEventListener("@none",function(e){let t=e.composedPath(),{method:o,action:n,form:i,signal:r}=e.detail;return fetch(d.build(o,n,i),{signal:r}).then(s.none).then(()=>e.success(t)).catch(a=>e.failure(t,a))});

//# sourceMappingURL=@none.js.map
