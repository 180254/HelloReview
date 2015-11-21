/*jslint browser: true*/
/*global $, jQuery, toastr*/

$(document).ready(function () {
    'use strict';

    $('.b-rename-link').editable({
        params: function (params) {
            params[$("meta[name='_csrf_parameterName']").attr('content')] =
                $("meta[name='_csrf']").attr('content');
            return params;
        },

        error: function (errors) {
            $('main').find('.editable-error-block').html(errors.responseJSON.join("<br/>\n"));
        },

        success: function (response) {
            toastr.success(response.join("<br/>\n"));
        }
    });
});
