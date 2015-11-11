/*jslint browser: true*/
/*global $, jQuery*/

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

$(document).ready(function () {
    'use strict';

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
});
