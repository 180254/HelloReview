/*jslint browser: true*/
/*global $, jQuery, toastr, jqXHRFailToArray*/

/*                  form                              */
function formAddHandler(action) {
    'use strict';

    var $formAdd = $('#form-add-form'),
        serialized = $formAdd.serialize() + '&action=' + action,
        $errorsDiv = $('#form-add-error'),
        $errorsList = $('#form-add-error-list'),
        $buttonPreview = $('#form-add-button-preview'),
        $buttonAdd = $('#form-add-button-add'),
        $previewUrlDiv = $('#form-add-preview-url');

    $errorsDiv.hide();
    $errorsList.html('');
    $buttonPreview.prop('disabled', true);
    $buttonAdd.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: $formAdd.attr('data-add-url'),
        data: serialized

    }).done(function (data) {
        var url;

        if (action === 'add') {
            url = $formAdd.attr('data-forms-url');
            window.location = url;

        } else {
            url = $formAdd.attr('data-preview-url') + data;
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

    $errorsDiv.hide();
    $errorsList.html('');
    $button.prop('disabled', true);

    $.ajax({
        type: 'GET',
        url: $select.attr('data-repolist-url')

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

    var serialized,
        $reviewAdd = $('#review-add-form'),
        $button = $('#review-add-submit'),
        $errorsDiv = $('#review-add-error'),
        $errorsList = $('#review-add-error-list'),
        $warningDiv = $('#review-add-warning'),
        $warningList = $('#review-add-warning-list'),
        $warningIgnore = $('#review-add-ignore-warning'),
        $warningPart1 = $('#review-add-warning-part1'),
        $warningPart2Of1 = $('#review-add-warning-part2-of1'),
        $warningPart2Of2 = $('#review-add-warning-part2-of2'),
        $allInputs = $('#review-add-form select, #review-add-form input');

    $allInputs.prop('disabled', false);
    serialized = $reviewAdd.serialize();
    $warningIgnore.val('0');

    $errorsDiv.hide();
    $warningDiv.hide();

    $errorsList.html('');
    $warningList.html('');
    $button.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: $reviewAdd.attr('data-add-url'),
        data: serialized

    }).done(function () {
        window.location = $reviewAdd.attr('data-reviews-url');


    }).fail(function (jqXHR) {
        var errors = jqXHRFailToArray(jqXHR),
            i,
            errLen;

        // precondition failed
        if (jqXHR.status === 412) {
            $allInputs.prop('disabled', true);

            $warningIgnore.val('1');
            $warningPart1.html(errors[0]);
            $warningPart2Of1.html(errors[1]);
            $warningPart2Of2.html(errors[2]);

            for (i = 3, errLen = errors.length; i < errLen; i += 1) {
                $('<li>').text(errors[i]).appendTo($warningList);
            }

            $warningDiv.fadeIn();

        } else {
            for (i = 0, errLen = errors.length; i < errLen; i += 1) {
                $('<li>').text(errors[i]).appendTo($errorsList);
            }

            $errorsDiv.fadeIn();
        }

    }).always(function () {
        $button.prop('disabled', false);
    });
}
/*             course + participants                */

function courseParticipantAddHandler(prefix, $element) {
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
        url: $element.attr('data-add-url'),
        data: serialized

    }).done(function (data) {
        $errorsDiv.hide();

        toastr.success(data[0]);
        $('tbody').append(data[1]);
        $('input').val('');

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
        courseParticipantAddHandler('course', $(this));
        return false;
    });

    $('#course-add-button-submit').click(function () {
        courseParticipantAddHandler('course', $(this));
    });

    $('#course-add').on('show.bs.modal', function () {
        resetAddCourseParticipantModal('course-add');
    });

    //course participant
    $('#course-participant-add-form').submit(function () {
        courseParticipantAddHandler('course-participant', $(this));
        return false;
    });

    $('#course-participant-add-button-submit').click(function () {
        courseParticipantAddHandler('course-participant', $(this));
    });

    $('#course-participant-add').on('show.bs.modal', function () {
        resetAddCourseParticipantModal('course-participant-add');
    });

    $('#participant-name,#participant-github-name').keyup(function (e) {
        if (e.keyCode === 13) {
            courseParticipantAddHandler('course-participant', $(this));
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
