/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@reload", function (event)
{
    let path = event.composedPath();
    let {method, action, form, parameters} = event.detail;

    fetch(RequestBuilder.build(method, action, form))
            .then(ResponseHandler.dataURL)
            .then(result =>
            {
                event.success(path, result);

                let target = null;
                switch (parameters[0] || "_self")
                {
                    case "_self":
                        target = window.location;
                        break;
                    case "_parent":
                        target = window.parent.location;
                        break;
                    case "_top":
                        target = window.top.location;
                        break;
                    default:
                        target = DOM.navigate(event, parameters[0])
                                .orElseThrow(`${parameters[0]} is not a valid selector`);
                        break;
                }

                if (target.reload)
                    target.reload();
            })
            .catch(error => event.failure(path, error));
});