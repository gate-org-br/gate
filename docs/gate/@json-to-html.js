import"./trigger.js";import"./dom.js";import n from"./data-url.js";import s from"./formatter.js";import f from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@json-to-html",function(t){let r=t.composedPath(),{method:e,action:m,form:i,signal:a}=t.detail;fetch(f.build(e,m,i),{signal:a}).then(h.json).then(s.JSONtoHTML).then(n.ofHTML).then(o=>t.success(r,o)).catch(o=>t.failure(r,o))});

//# sourceMappingURL=@json-to-html.js.map
