/*jslint browser: true*/
/*global $, jQuery, toastr,jqXHRFailToArray*/

function reviewOpenCloseHandler($link) {
    'use strict';

    var ajaxHeaders = {};

    ajaxHeaders[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');


    $.ajax({
        type: 'POST',
        url: $link.attr('data-url'),
        data: 'id=' + $link.attr('data-id'),
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data[0]);
        $link.children().html(data[1]);

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).join("\n"));

    });
}

$(document).ready(function () {
    'use strict';

    $('.review-open-close-link').each(function () {
        var $dis = $(this);

        $(this).confirmation({
            onConfirm: function () {
                reviewOpenCloseHandler($dis);
            }
        });
    });
});
