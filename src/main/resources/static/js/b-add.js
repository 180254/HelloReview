/*jslint browser: true*/
/*global $, jQuery, toastr*/

function formAddHandler(action) {
    'use strict';

    var serialized = $('#form-add-form').serialize() + '&action=' + action,
        $errorsDiv = $('#form-add-error'),
        $errorsList = $('#form-add-error-list'),
        $buttonPreview = $('#form-add-button-preview'),
        $buttonAdd = $('#form-add-button-add'),
        $previewUrlDiv = $('#form-add-preview-url');

    $errorsList.html('');
    $buttonPreview.prop('disabled', true);
    $buttonAdd.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: '/m/forms/add',
        data: serialized

    }).done(function (data) {
        var url;

        if (action === 'add') {
            url = '/m/forms';
            window.location = url;

        } else {
            url = '/m/forms/preview/' + data;
            $previewUrlDiv.find('a').attr('href', url).text(url);
            $errorsDiv.hide();
            $previewUrlDiv.fadeIn();
        }

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
        $previewUrlDiv.hide();

        for (i = 0, errLen = errors.length; i < errLen; i += 1) {
            $('<li>').text(errors[i]).appendTo($errorsList);
        }

    }).always(function () {
        $buttonPreview.prop('disabled', false);
        $buttonAdd.prop('disabled', false);
    });
}

function courseParticipantAddHandler(prefix, url) {
    'use strict';

    var serialized = $('#' + prefix + '-add-form').serialize(),
        $errorsDiv = $('#' + prefix + '-add-error'),
        $errorsList = $('#' + prefix + '-add-error-list'),
        $buttonSubmit = $('#' + prefix + '-add-button-submit'),
        $buttonClose = $('#' + prefix + '-add-button-close');

    $errorsList.html('');
    $buttonSubmit.prop('disabled', true);
    $buttonClose.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: url,
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

function resetAddCourseParticipantModal(prefix) {
    'use strict';

    $('#' + prefix + '-name').val('');
    $('#' + prefix + '-error').hide();
}

$(document).ready(function () {
    'use strict';

    // form
    $('#form-add-form').submit(function () {
        formAddHandler('preview');
        return false;
    });

    $('#form-add-button-preview').click(function () {
        formAddHandler('preview');
    });

    $('#form-add-button-add').click(function () {
        formAddHandler('add');
    });

    //course
    $('#course-add-form').submit(function () {
        courseParticipantAddHandler('course', '/m/courses/add');
        return false;
    });

    $('#course-add-button-submit').click(function () {
        courseParticipantAddHandler('course', '/m/courses/add');
    });

    $('#course-add').on('show.bs.modal', function () {
        resetAddCourseParticipantModal('course-add');
    });

    //course participant
    $('#course-participant-add-form').submit(function () {
        courseParticipantAddHandler('course-participant', '/m/courses/participants/add');
        return false;
    });

    $('#course-participant-add-button-submit').click(function () {
        courseParticipantAddHandler('course-participant', '/m/courses/participants/add');
    });

    $('#course-participant-add').on('show.bs.modal', function () {
        resetAddCourseParticipantModal('course-participant-add');
    });
});