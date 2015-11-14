/*jslint browser: true*/
/*global $, jQuery, toastr*/

function jqXHRFailToArray(jqXHR) {
    'use strict';

    var json = jQuery.parseJSON(jqXHR.responseText),
        errors = [];

    if (json.status === undefined) {
        errors = errors.concat(json);
    } else {
        errors = errors.concat('Server error: ' + json.status + ' ' + json.error);
    }

    return errors;
}

/*                  form                              */
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
        var errors = jqXHRFailToArray(jqXHR),
            i,
            errLen;

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

/*                  review                          */

function reviewDownloadRepoList() {
    'use strict';

    var $select = $('#review-add-repository'),
        $button = $('#review-add-submit'),
        $errorsDiv = $('#review-add-error'),
        $errorsList = $('#review-add-error-list');

    $button.prop('disabled', true);

    $.ajax({
        type: 'GET',
        url: '/m/reviews/add/repolist'

    }).done(function (data) {
        var i, len;

        for (i = 0, len = data.length; i < len; i += 1) {
            $('<option>', {
                value: data[i],
                text: data[i]
            }).appendTo($select);
        }

        $button.prop('disabled', false);

    }).fail(function (jqXHR) {
        var errors = jqXHRFailToArray(jqXHR),
            i,
            errLen;

        $errorsDiv.fadeIn();

        for (i = 0, errLen = errors.length; i < errLen; i += 1) {
            $('<li>').text(errors[i]).appendTo($errorsList);
        }

    });
}

function reviewAddHandler() {
    'use strict';

    var serialized = $('#review-add-form').serialize(),
        $button = $('#review-add-submit'),
        $errorsDiv = $('#review-add-error'),
        $errorsList = $('#review-add-error-list');

    $errorsList.html('');
    $button.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: '/m/reviews/add',
        data: serialized

    }).done(function () {
        window.location = '/m/reviews';


    }).fail(function (jqXHR) {
        var errors = jqXHRFailToArray(jqXHR),
            i,
            errLen;

        $errorsDiv.fadeIn();

        for (i = 0, errLen = errors.length; i < errLen; i += 1) {
            $('<li>').text(errors[i]).appendTo($errorsList);
        }

    }).always(function () {
        $button.prop('disabled', false);
    });
}
/*             course + participants                */

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
        toastr.success(data[0]);

        window.setTimeout(function () {
            location.reload();
        }, 1200);

    }).fail(function (jqXHR) {
        var errors = jqXHRFailToArray(jqXHR),
            i,
            errLen;

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

    $('#course-participant-name,#course-participant-github-name').keyup(function (e) {
        if (e.keyCode === 13) {
            courseParticipantAddHandler('course-participant', '/m/courses/participants/add');
        }
    });

    //review

    $('.refresh').click(function () {
        location.reload();
    });

    if ($('#review-add-repository')[0] !== undefined) {
        reviewDownloadRepoList();
    }

    $("#review-add-form").submit(function () {
        reviewAddHandler();
        return false;
    });

});
