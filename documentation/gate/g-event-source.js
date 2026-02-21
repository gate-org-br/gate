let d=document.createElement("template");d.innerHTML=`
<style data-element="g-event-source">:host(*){display:none}
</style>`;import u from"./base64.js";export default function c(e,s,o){const t=new EventSource(e);t.onopen=()=>o&&console.log("listening to app events."),t.addEventListener("message",n=>{let a=u.decode(n.data);o&&console.log(a);const r=JSON.parse(a);s.dispatchEvent(new CustomEvent("sse",{bubbles:!0,composed:!0,detail:r})),s.dispatchEvent(new CustomEvent(r.type,{bubbles:!0,composed:!0,detail:r.detail}))}),t.onerror=n=>o&&console.error("Error when listening to app events:",n),window.addEventListener("beforeunload",()=>t.close(),{once:!0})}Array.from(document.querySelectorAll("[data-event-source]")).forEach(e=>c(e.getAttribute("data-event-source"),e,e.hasAttribute("data-event-source:log")));

//# sourceMappingURL=g-event-source.js.map
