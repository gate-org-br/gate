export default class s extends HTMLElement{static cancel(t){t.preventDefault(),t.stopPropagation(),t.stopImmediatePropagation()}static dispatch(t,...i){let e=t.filter(a=>a.isConnected&&a.dispatchEvent)[0];e&&i.forEach(a=>e.dispatchEvent(a))}}

//# sourceMappingURL=event-handler.js.map
