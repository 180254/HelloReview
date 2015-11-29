/*jslint browser: true*/
/*global $, jQuery, toastr, jqXHRFailToArray, getAjaxHeader*/

function commissionRetryHandler($retryLink) {
    'use strict';

    var ajaxHeaders = getAjaxHeader();

    $.ajax({
        type: 'POST',
        url: $retryLink.attr('data-retry-url'),
        data: 'commission-id=' + $retryLink.attr('data-id'),
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data[0]);
        $retryLink.closest('tr').replaceWith(data[1]);
        $retryLink.remove();

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).toBrNL());

    });
}

$(document).ready(function () {
    'use strict';

    $('.commission-retry').click(function () {
        commissionRetryHandler($(this));
    });
});
