import"./trigger.js";import"./dom.js";import n from"./data-url.js";import s from"./sanitize.js";import f from"./request-builder.js";import d from"./response-handler.js";window.addEventListener("@sanitize",function(t){let i=t.composedPath(),{method:e,action:r,form:m,signal:a}=t.detail;fetch(f.build(e,r,m),{signal:a}).then(d.text).then(s).then(n.ofHTML).then(o=>t.success(i,o)).catch(o=>t.failure(i,o))});

//# sourceMappingURL=@sanitize.js.map
