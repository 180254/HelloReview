/*jslint browser: true*/
/*global $, jQuery, toastr, getAjaxParameter, responseJSONToArray*/

function makeEditable($elements) {
    'use strict';

    $elements.editable({
        params: function (params) {
            jQuery.extend(params, getAjaxParameter());
            return params;
        },

        error: function (errors) {
            var errors1 = responseJSONToArray(errors.responseJSON).toBrNL();
            $('main').find('.editable-error-block').html(errors1);
        },

        success: function (response) {
            toastr.success(response.toBrNL());
        }
    });
}
$(document).ready(function () {
    'use strict';

    makeEditable($('.b-rename-link'));
});
