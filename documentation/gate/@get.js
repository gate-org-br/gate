import"./trigger.js";import l from"./resolve.js";import u from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@get",function(t){let r=t.composedPath(),e=r[0]||t.target,{parameters:o}=t.detail,i=o[0]||e.getAttribute("href")||e.getAttribute("action")||e.getAttribute("formaction")||e.getAttribute("data-action");return i=l(e,t.detail.context,i),fetch(u.build("get",i)).then(c.dataURL).then(a=>t.success(r,a)).catch(a=>t.failure(r,a))});

//# sourceMappingURL=@get.js.map
