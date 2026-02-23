import"./trigger.js";import l from"./resolve.js";import s from"./data-url.js";import i from"./request-builder.js";import m from"./response-handler.js";import h from"./trigger-extractor.js";window.addEventListener("@post",function(t){let e=t.composedPath(),a=e[0]||t.target,{action:d,method:p,form:c,parameters:n}=t.detail,o=n[0]||h.action(a);return o=l(a,t.detail.context,o),fetch(i.build(p,d,c)).then(m.dataURL).then(s.parse).then(r=>fetch(i.build("post",o,r.data,r.contentType))).then(m.dataURL).then(r=>t.success(e,r)).catch(r=>t.failure(e,r))});

//# sourceMappingURL=@post.js.map
