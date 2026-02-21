import"./trigger.js";import n from"./data-url.js";import a from"./formatter.js";import h from"./request-builder.js";import s from"./response-handler.js";window.addEventListener("@json-to-html",function(o){let e=o.composedPath(),{method:r,action:m,form:i}=o.detail;fetch(h.build(r,m,i)).then(s.json).then(a.JSONtoHTML).then(t=>new n("text/html",t).toString()).then(t=>o.success(e,t)).catch(t=>o.failure(e,t))});

//# sourceMappingURL=@json-to-html.js.map
