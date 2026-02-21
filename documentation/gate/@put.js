import"./trigger.js";import l from"./resolve.js";import h from"./data-url.js";import i from"./request-builder.js";import m from"./response-handler.js";import f from"./trigger-extractor.js";window.addEventListener("@put",function(t){let e=t.composedPath(),a=e[0]||t.target,{action:d,method:p,form:c,parameters:n}=t.detail,o=n[0]||f.action(a);return o=l(a,t.detail.context,o),fetch(i.build(p,d,c)).then(m.dataURL).then(h.parse).then(r=>fetch(i.build("put",o,r.data,r.contentType))).then(m.dataURL).then(r=>t.success(e,r)).catch(r=>t.failure(e,r))});

//# sourceMappingURL=@put.js.map
