import e from"./trigger-core.js";import i from"./trigger-extractor.js";window.addEventListener("change",function(t){let r=t.target||t.composedPath()[0];i.trigger(r)==="change"&&e(t,r,r)}),window.addEventListener("input",function(t){let r=t.target||t.composedPath()[0];i.trigger(r)==="input"&&e(t,r,r)});

//# sourceMappingURL=trigger-change.js.map
