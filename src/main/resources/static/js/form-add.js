/*jslint browser: true*/
/*global $, jQuery, alert*/

function formAddHandler(action) {
    'use strict';

    var serializedWithAction = $('#form-add').serialize() + '&action=' + action,
        $ulErrors = $('#form-add-error-list'),
        $divErrors = $('#form-add-error');

    $ulErrors.html('');

    $.ajax({
        type: 'POST',
        url: '/m/forms/add',
        data: serializedWithAction

    }).done(function () {
        $divErrors.hide();

    }).fail(function (jqXHR) {
        var errors = [].concat(jQuery.parseJSON(jqXHR.responseText)),
            i,
            tot;

        $divErrors.fadeIn();

        for (i = 0, tot = errors.length; i < tot; i += 1) {
            $('<ul>').text(errors[i]).appendTo($ulErrors);
        }
    });
}

$(document).ready(function () {
    'use strict';

    $('#form-add-preview').click(function () {
        formAddHandler('preview');
    });

    $('#form-add-add').click(function () {
        formAddHandler('add');
    });
});