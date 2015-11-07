/*jslint browser: true*/
/*global $, jQuery*/

$(document).ready(function () {
    'use strict';

    $(document).ajaxSend(function () {
        $('#ajax-loader').show();
    });

    $(document).ajaxComplete(function () {
        $('#ajax-loader').hide();
    });
});