import n from"./trigger-core.js";import i from"./trigger-extractor.js";window.addEventListener("load",function(o){let e=window.location.hash;if(!e)return;let t=document.querySelector(e);if(!t)return;if(i.isNative(t))return t.click();let r=i.target(t);r&&r.startsWith("@")&&n(o,t,t)});

//# sourceMappingURL=trigger-hash.js.map
