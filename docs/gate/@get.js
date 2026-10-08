import"./trigger.js";import u from"./resolve.js";import c from"./request-builder.js";import d from"./response-handler.js";window.addEventListener("@get",function(t){let r=t.composedPath(),e=r[0]||t.target,{parameters:o,signal:l}=t.detail,i=o[0]||e.getAttribute("href")||e.getAttribute("action")||e.getAttribute("formaction")||e.getAttribute("data-action");return i=u(e,t.detail.context,i),fetch(c.build("get",i),{signal:l}).then(d.dataURL).then(a=>t.success(r,a)).catch(a=>t.failure(r,a))});

//# sourceMappingURL=@get.js.map
