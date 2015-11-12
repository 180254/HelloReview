/*jslint browser: true*/
/*global $, jQuery, toastr*/

function deleteB($deleteLink) {
    'use strict';

    var $row = $deleteLink.closest('tr'),
        id = $deleteLink.attr('data-id'),
        ajaxHeaders = {};

    ajaxHeaders[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');


    $.ajax({
        type: 'POST',
        url: $deleteLink.attr('data-delete-url'),
        data: 'id=' + id,
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data);
        $row.remove();

    }).fail(function (jqXHR) {
        toastr.error(jqXHR.responseText);

    });
}

$(document).ready(function () {
    'use strict';

    $('.b-delete-link').each(function () {
        var $dis = $(this);

        $(this).confirmation({
            onConfirm: function () {
                deleteB($dis);
            }
        });
    });

});

