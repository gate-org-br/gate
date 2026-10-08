import"./trigger.js";import"./dom.js";import"./event-handler.js";import a from"./request-builder.js";import c from"./response-handler.js";import"./trigger-event.js";window.addEventListener("@redirect",function(e){let t=e.composedPath(),{method:o,action:i,form:n,signal:m}=e.detail;return fetch(a.build(o,i,n),{signal:m}).then(c.text).then(r=>window.location=r).then(()=>e.success(t)).catch(r=>e.failure(t,r))});

//# sourceMappingURL=@redirect.js.map
