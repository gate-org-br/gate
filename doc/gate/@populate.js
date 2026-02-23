import c from"./dom.js";import d from"./data-url.js";import u from"./populator.js";import f from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@populate",function(t){let o=t.composedPath(),l=o[0]||t.target,{method:r,action:i,form:p}=t.detail,[a,m="value",n="label"]=t.detail.parameters,s=c.navigate(l,a).orElseThrow(`${a} is not a valid selector`);fetch(f.build(r,i,p)).then(h.json).then(e=>{new u(e).populate(s,m,n),t.success(o,new d("application/json",e).toString())}).catch(e=>t.failure(o,e))});

//# sourceMappingURL=@populate.js.map
