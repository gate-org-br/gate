/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import DataURL from './data-url.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("@value", function (event)
{
    let path = event.composedPath();
    let {method, action, form, parameters: [selector]} = event.detail;

    let target = DOM.navigate(event, selector).orElseThrow(`${selector} is not a valid selector`);

    fetch(RequestBuilder.build(method, action, form))
            .then(response =>
            {
                const contentType = response.headers.get('content-type');
                return ResponseHandler.auto(response)
                        .then(result =>
                        {
                            target.value = result;
                            event.success(path, new DataURL(contentType, result).toString());
                        });
            })
            .catch(error => event.failure(path, error));
});