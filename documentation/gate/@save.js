import d from"./g-file-picker.js";import s from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@save",function(e){let a=e.composedPath(),{method:o,action:i,form:r}=e.detail;fetch(s.build(o,i,r)).then(c.dataURL).then(d.saveDataURL).then(t=>e.success(a,t)).catch(t=>e.failure(a,t))});

//# sourceMappingURL=@save.js.map
