import d from"./g-file-picker.js";import c from"./request-builder.js";import l from"./response-handler.js";window.addEventListener("@save",function(e){let a=e.composedPath(),{method:i,action:o,form:r,signal:s}=e.detail;fetch(c.build(i,o,r),{signal:s}).then(l.dataURL).then(d.saveDataURL).then(t=>e.success(a,t)).catch(t=>e.failure(a,t))});

//# sourceMappingURL=@save.js.map
