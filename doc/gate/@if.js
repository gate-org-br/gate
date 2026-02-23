import"./trigger.js";import d from"./data-url.js";import p from"./request-builder.js";import f from"./response-handler.js";window.addEventListener("@if",function(t){let e=t.composedPath(),o=e[0]||t.target,{method:s,action:n,parameters:[l],form:c}=t.detail;fetch(p.build(s,n,c)).then(f.dataURL).then(a=>{let r=d.parse(a),i=new Function("result",`return ${l}`).bind(o)();(r.contentType.startsWith("application/json")?JSON.stringify(i(JSON.parse(r.data))):i(r.data))?t.success(e,a):t.resolve(e)}).catch(a=>t.failure(e,a))});

//# sourceMappingURL=@if.js.map
