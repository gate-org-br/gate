export default class n{static decode(t){const o=new Uint8Array(atob(t).split("").map(e=>e.charCodeAt(0)));return new TextDecoder("utf-8").decode(o)}static encode(t){const r=new TextEncoder("utf-8").encode(t);let e="";return r.forEach(c=>e+=String.fromCharCode(c)),btoa(e)}}

//# sourceMappingURL=base64.js.map
