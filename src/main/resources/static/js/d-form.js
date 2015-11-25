/*jslint browser: true*/
/*global $, jQuery, toastr, jqXHRFailToArray, getAjaxHeader*/

function responseSubmitHandler() {
    'use strict';
    var $form = $('#p-form'),
        $button = $('#p-form-submit'),
        serialize = {},
        $input,
        ajaxHeaders = getAjaxHeader();

    $button.prop('disabled', true);


    serialize.commID = $form.attr('data-comm-id');
    serialize.answers = [];

    $form.find('input,textarea').each(function () {
        var $dis = $(this),
            answer = {};

        answer.inputID = $dis.attr('data-input-id');
        answer.answer = $dis.val() || $dis.html();
        serialize.answers.push(answer);
    });

    $input = $('<input>', {
        name: 'response',
        value: JSON.stringify(serialize)
    });

    $.ajax({
        type: 'POST',
        url: $form.attr('data-form-action'),
        data: $input.serialize(),
        headers: ajaxHeaders

    }).done(function (data) {

        toastr.success(data[0], '', {
            onHidden: function () {
                window.location = $form.attr('data-redirect-to');
            }
        });

    }).fail(function (jqXHR) {
        toastr.error(jqXHRFailToArray(jqXHR).toBrNL());
        $button.prop('disabled', false);
    });

}

function disableResponseForm() {
    'use strict';

    $('input.slider').slider("disable");
    $('textarea,input').prop('readonly', true);
    $('button').remove();
}

$(document).ready(function () {
    'use strict';

    $('#p-form-submit').click(function () {
        responseSubmitHandler();
        return false;
    });

    if ($('#p-form.f-disabled').length === 1) {
        disableResponseForm();
    }

});
