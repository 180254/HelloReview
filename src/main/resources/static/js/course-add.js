/*jslint browser: true*/
/*global $, jQuery, alert*/

function courseAddHandler() {
    'use strict';

    var serialized = $('#course-add-form').serialize(),
        $errorsDiv = $('#course-add-error'),
        $errorsList = $('#course-add-error-list'),
        $buttonSubmit = $('#course-add-button-submit'),
        $buttonClose = $('#course-add-button-close');

    $errorsList.html('');
    $buttonSubmit.prop('disabled', true);
    $buttonClose.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: '/m/courses/add',
        data: serialized

    }).done(function () {
        location.reload();

    }).fail(function (jqXHR) {
        var json = jQuery.parseJSON(jqXHR.responseText),
            errors = [],
            i,
            errLen;

        if (json.status === undefined) {
            errors = errors.concat(json);
        } else {
            errors = errors.concat('Server error: ' + json.status + ' ' + json.error);
        }

        $errorsDiv.fadeIn();

        for (i = 0, errLen = errors.length; i < errLen; i += 1) {
            $('<ul>').text(errors[i]).appendTo($errorsList);
        }

    }).always(function () {
        $buttonSubmit.prop('disabled', false);
        $buttonClose.prop('disabled', false);
    });
}

$(document).ready(function () {
    'use strict';

    $('#course-add-form').submit(function () {
        courseAddHandler();
        return false;
    });

    $('#course-add-button-submit').click(function () {
        courseAddHandler();
    });
});
