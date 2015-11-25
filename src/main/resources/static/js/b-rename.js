/*jslint browser: true*/
/*global $, jQuery, toastr, getAjaxParameter, responseJSONToArray*/

$(document).ready(function () {
    'use strict';

    $('.b-rename-link').editable({
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
});
