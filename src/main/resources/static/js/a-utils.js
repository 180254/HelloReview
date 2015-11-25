/*jslint browser: true*/
/*global $, jQuery*/

function responseJSONToArray(jsonObject) {
    'use strict';

    var errors = [];

    if (jsonObject.status === undefined) {
        errors = errors.concat(jsonObject);
    } else {
        errors = errors.concat('Server error: ' + jsonObject.status + ' ' + jsonObject.error);
    }

    return errors;
}

function jqXHRFailToArray(jqXHR) {
    'use strict';

    var json = jQuery.parseJSON(jqXHR.responseText);
    return responseJSONToArray(json);
}

Array.prototype.toBrNL = function () {
    'use strict';

    return this.join("<br/>\n");
};

function getAjaxHeader() {
    'use strict';

    var ajaxHeader = {};

    ajaxHeader[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');

    return ajaxHeader;
}

function getAjaxParameter() {
    'use strict';

    var ajaxParameter = {};

    ajaxParameter[$("meta[name='_csrf_parameterName']").attr('content')] =
        $("meta[name='_csrf']").attr('content');

    return ajaxParameter;
}
