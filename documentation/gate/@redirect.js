import"./trigger.js";import n from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@redirect",function(t){let o=t.composedPath(),{method:i,action:r,form:d}=t.detail;return fetch(n.build(i,r,d)).then(c.text).then(e=>window.location=e).then(()=>t.success(o)).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@redirect.js.map
