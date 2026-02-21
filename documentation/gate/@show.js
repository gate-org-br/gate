import"./trigger.js";import m from"./dom.js";import h from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@show",function(e){let t=e.composedPath(),a=t[0]||e.target,{method:s,action:l,form:d,parameters:[i]}=e.detail,o=m.navigate(a,i).orElseThrow(`${i} is not a valid selector`);fetch(h.build(s,l,d)).then(c.dataURL).then(r=>{o.show?o.show():o.removeAttribute("hidden"),e.success(t,r)}).catch(r=>e.failure(t,r))});

//# sourceMappingURL=@show.js.map
