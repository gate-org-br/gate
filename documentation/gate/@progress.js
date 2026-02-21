import c from"./process.js";import i from"./response-handler.js";let l=1;window.addEventListener("@progress",function(o){let r=o.composedPath(),s=r[0]||o.target,t=s.title||"Progresso",a=s.id||"proccess@"+l++;c(a,t,o.detail.method,o.detail.action,o.detail.form).then(e=>e?i.dataURL(e):null).then(e=>e?o.success(r,e):o.resolve(r)).catch(e=>o.failure(r,e))});

//# sourceMappingURL=@progress.js.map
