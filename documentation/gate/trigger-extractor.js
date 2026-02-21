export default class r{static method(t){return(t.getAttribute("formmethod")||t.getAttribute("data-method")||t.getAttribute("method")||t.form?.method||"get").toLowerCase()}static action(t){return t.getAttribute("href")||t.getAttribute("formaction")||t.getAttribute("data-action")||t.getAttribute("action")||t.form?.action||""}static target(t){return t.getAttribute("target")||t.getAttribute("formtarget")||t.getAttribute("data-target")||t.form?.target||"_self"}}

//# sourceMappingURL=trigger-extractor.js.map
