/*jslint browser: true*/
/*global $, jQuery, toastr, jqXHRFailToArray, getAjaxHeader*/

function reviewOpenCloseHandler($link) {
    'use strict';

    var ajaxHeaders = getAjaxHeader();

    $.ajax({
        type: 'POST',
        url: $link.attr('data-url'),
        data: 'id=' + $link.attr('data-id'),
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data[0]);
        $link.children().html(data[1]);

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).toBrNL());

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
