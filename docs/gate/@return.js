let r;export default class a{static bind(e){e.iframe?e.iframe._GReturnTrigger=e:r=e}static free(e){e.iframe?delete e.iframe._GReturnTrigger:r=null}static update(e,n){let f=e.length;for(var t=0;t<f;t++)e[t]&&(e[t].value=n[t]&&n[t]!=="_"?n[t]:"");for(var t=0;t<f;t++)e[t]&&e[t].dispatchEvent(new CustomEvent("changed",{bubbles:!0}));return n}}window.addEventListener("@return",function(i){(window?.frameElement?._GReturnTrigger||r).dispatchEvent(new CustomEvent("commit",{detail:i.detail.parameters}))});

//# sourceMappingURL=@return.js.map
