/*jslint browser: true*/
/*global $, jQuery, toastr,jqXHRFailToArray*/

function commissionRetryHandler($retryLink) {
    'use strict';

    var ajaxHeaders = {};

    ajaxHeaders[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');

    $.ajax({
        type: 'POST',
        url: '/m/reviews/commissions/retry',
        data: 'commission-id=' + $retryLink.attr('data-id'),
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data[0]);
        $retryLink.closest('tr').addClass('stale');
        $retryLink.remove();

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).join("<br/>\n"));

    });
}

$(document).ready(function () {
    'use strict';

    $('.commission-retry').click(function () {
        commissionRetryHandler($(this));
    });
});
