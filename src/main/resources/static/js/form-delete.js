/*jslint browser: true*/
/*global $, jQuery, alert*/

function deleteForm($deleteLink) {
    'use strict';
    var $row = $deleteLink.closest('tr'),
        id = $deleteLink.closest('tr').children('td').html();

    toastr.options = {
        'closeButton': false,
        'debug': false,
        'newestOnTop': false,
        'progressBar': false,
        'positionClass': 'toast-top-center',
        'preventDuplicates': false,
        'onclick': null,
        'showDuration': '300',
        'hideDuration': '1000',
        'timeOut': '1500',
        'extendedTimeOut': '1000',
        'showEasing': 'swing',
        'hideEasing': 'linear',
        'showMethod': 'fadeIn',
        'hideMethod': 'fadeOut'
    };


    $.ajax({
        type: 'POST',
        url: '/m/forms/delete/' + id

    }).done(function (data) {
        toastr.success(data);
        $row.remove();

    }).fail(function (jqXHR) {
        toastr.error(jqXHR.responseText);

    });
}

$(document).ready(function () {
    'use strict';

    $('.forms-delete-link').each(function () {
        var $dis = $(this);

        $(this).confirmation({
            onConfirm: function () {
                deleteForm($dis);
            }
        });
    });

});