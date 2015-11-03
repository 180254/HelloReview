/*jslint browser: true*/
/*global $, jQuery, alert*/

function formsTextareaLettersCounter() {
    'use strict';

    $('.form-main').find('textarea').each(function () {

        var textMax = $(this).attr('maxlength');
        $(this).next().html('0 / ' + textMax);

        $(this).keyup(function () {
            var textLength = $(this).val().length;

            $(this).next().html(textLength + ' / ' + textMax);
        });
    });
}

$(document).ready(function () {
    'use strict';

    $("input.slider").slider();
    autosize($('textarea'));
    formsTextareaLettersCounter();
});