/*jslint browser: true*/
/*global $, jQuery, autosize */

function textareaLettersCounter($textareas) {
    'use strict';

    $textareas.each(function () {
        var textMax = $(this).attr('maxlength');

        jQuery('<h6/>', {
            class: 'pull-right t-letter-counter'
        }).insertAfter($(this));

        $(this).next().html($(this).val().length + ' / ' + textMax);

        $(this).keyup(function () {
            var textLength = $(this).val().length;
            $(this).next().html(textLength + ' / ' + textMax);
        });
    });

    $('.modal').on('shown.bs.modal', function () {
        var $textInputs = $('input[type="text"]');
        if ($textInputs.length > 0) {
            $textInputs.first().focus();
        }
    });
}

$(document).ready(function () {
    'use strict';

    var $textareas = $('textarea');

    $("input.slider").slider();

    autosize($textareas);
    textareaLettersCounter($textareas);
});
