/*jslint browser: true*/
/*global $, jQuery, alert*/

function formAddHandler(action) {
    'use strict';

    var serializedWithAction = $('#form-add').serialize() + '&action=' + action,
        $ulErrors = $('#form-add-error-list'),
        $divErrors = $('#form-add-error'),
        $divPreview = $('#form-add-preview-url'),
        $previewButton = $('#form-add-preview'),
        $addButton = $('#form-add-add'),
        ajaxHeaders = {};

    ajaxHeaders[$("meta[name='_csrf_header']").attr('content')] =
        $("meta[name='_csrf']").attr('content');

    $ulErrors.html('');
    $previewButton.prop('disabled', true);
    $addButton.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: '/m/forms/add',
        data: serializedWithAction,
        headers: ajaxHeaders

    }).done(function (data) {
        var url;

        if (action === 'add') {
            url = '/m/forms';
            window.location = url;

        } else {
            url = '/m/forms/preview/' + data;
            $divPreview.find('a').attr('href', url).text(url);
            $divErrors.hide();
            $divPreview.fadeIn();
        }

    }).fail(function (jqXHR) {
        var errors = [].concat(jQuery.parseJSON(jqXHR.responseText)),
            i,
            errLen;

        $divErrors.fadeIn();
        $divPreview.hide();

        for (i = 0, errLen = errors.length; i < errLen; i += 1) {
            $('<ul>').text(errors[i]).appendTo($ulErrors);
        }

    }).always(function () {
        $previewButton.prop('disabled', false);
        $addButton.prop('disabled', false);
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
