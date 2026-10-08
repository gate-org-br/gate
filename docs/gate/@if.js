import"./trigger.js";import p from"./data-url.js";import f from"./request-builder.js";import m from"./response-handler.js";window.addEventListener("@if",function(t){let e=t.composedPath(),o=e[0]||t.target,{method:s,action:n,parameters:[l],form:c,signal:d}=t.detail;fetch(f.build(s,n,c),{signal:d}).then(m.dataURL).then(a=>{let r=p.parse(a),i=new Function("result",`return ${l}`).bind(o)();(r.contentType.startsWith("application/json")?JSON.stringify(i(JSON.parse(r.data))):i(r.data))?t.success(e,a):t.resolve(e)}).catch(a=>t.failure(e,a))});

//# sourceMappingURL=@if.js.map
