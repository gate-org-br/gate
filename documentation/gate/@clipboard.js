import"./trigger.js";import e from"./data-url.js";import m from"./clipboard.js";import p from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@clipboard",function(o){let a=o.composedPath(),{method:r,action:i,form:d}=o.detail;fetch(p.build(r,i,d)).then(c.dataURL).then(t=>{m.copy(e.parse(t).data),o.success(a,t)}).catch(t=>o.failure(a,t))});

//# sourceMappingURL=@clipboard.js.map
