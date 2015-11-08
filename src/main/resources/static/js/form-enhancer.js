/*jslint browser: true*/
/*global $, jQuery, alert, slider, autosize */

function textareaLettersCounter($textareas) {
    'use strict';

    $textareas.each(function () {
        var textMax = $(this).attr('maxlength');

        jQuery('<h6/>', {
            class: 'pull-right t-letter-counter'
        }).insertAfter($(this));

        $(this).next().html('0 / ' + textMax);

        $(this).keyup(function () {
            var textLength = $(this).val().length;
            $(this).next().html(textLength + ' / ' + textMax);
        });
    });
}

$(document).ready(function () {
    'use strict';

    var $textareas = $('textarea');

    $("input.slider").slider();
    autosize($textareas);
    textareaLettersCounter($textareas);
});
