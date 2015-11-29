/*jslint browser: true*/
/*global $, jQuery, toastr, jqXHRFailToArray, getAjaxHeader*/

function deleteB($deleteLink) {
    'use strict';

    var $row = $deleteLink.closest('tr'),
        id = $deleteLink.attr('data-id'),
        ajaxHeaders = getAjaxHeader();

    $.ajax({
        type: 'POST',
        url: $deleteLink.attr('data-delete-url'),
        data: 'id=' + id,
        headers: ajaxHeaders

    }).done(function (data) {
        toastr.success(data.toBrNL());
        $row.remove();

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).toBrNL());

    });
}

function makeDeleteable($elements) {
    'use strict';

    $elements.each(function () {
        var $dis = $(this);

        $(this).confirmation({
            onConfirm: function () {
                deleteB($dis);
            }
        });
    });
}

$(document).ready(function () {
    'use strict';

    makeDeleteable($('.b-delete-link'));

});

