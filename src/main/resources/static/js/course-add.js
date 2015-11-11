/*jslint browser: true*/
/*global $, jQuery, toastr*/

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

    }).done(function (data) {
        $errorsDiv.hide();
        toastr.success(data);

        window.setTimeout(function () {
            location.reload();
        }, 1200);

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
            $('<li>').text(errors[i]).appendTo($errorsList);
        }

    }).always(function () {
        $buttonSubmit.prop('disabled', false);
        $buttonClose.prop('disabled', false);
    });
}

function resetAddCourseModal() {
    'use strict';

    $('#course-add-name').val('');
    $('#course-add-error').hide();
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

    $('#course-add').on('show.bs.modal', function () {
        resetAddCourseModal();
    });
});
