/*jslint browser: true*/
/*global $, jQuery, toastr*/

function jqXHRFailToArray(jqXHR) {
    'use strict';

    var json = jQuery.parseJSON(jqXHR.responseText),
        errors = [];

    if (json.status === undefined) {
        errors = errors.concat(json);
    } else {
        errors = errors.concat('Server error: ' + json.status + ' ' + json.error);
    }

    return errors;
}
