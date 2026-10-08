import"./trigger.js";import l from"./data-url.js";import f from"./property.js";import h from"./request-builder.js";import u from"./response-handler.js";function i(t,o){return o&&o.length?o.map(a=>f(t,a)):Object.values(t)}window.addEventListener("@values",function(t){let o=t.composedPath(),{method:a,action:n,form:m,parameters:e,signal:p}=t.detail;fetch(h.build(a,n,m),{signal:p}).then(u.json).then(r=>Array.isArray(r)?r.map(s=>i(s,e)):i(r,e)).then(l.ofJSON).then(r=>t.success(o,r)).catch(r=>t.failure(o,r))});

//# sourceMappingURL=@values.js.map
