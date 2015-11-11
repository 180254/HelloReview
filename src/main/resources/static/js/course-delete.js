/*jslint browser: true*/
/*global $, jQuery, alert*/

function deleteCourse($deleteLink) {
    'use strict';

    var $row = $deleteLink.closest('tr'),
        id = $deleteLink.closest('tr').children('td').html(),
        ajaxHeaders = {};

    ajaxHeaders[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');


    $.ajax({
        type: 'POST',
        url: '/m/courses/delete/' + id,
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

    $('.course-delete-link').each(function () {
        var $dis = $(this);

        $(this).confirmation({
            onConfirm: function () {
                deleteCourse($dis);
            }
        });
    });

});
