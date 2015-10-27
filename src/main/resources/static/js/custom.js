$(document).ready(function () {
    'use strict';

    $("input.slider").slider();

    formsTextareaLettersCounter();
    autosize($('textarea'));
});

function formsTextareaLettersCounter() {
    'use strict';

    var textMax = 1024;
    var textareas = $('.form-main').find('textarea');

    textareas.each(function () {
        $(this).next().html('0 / ' + textMax);

        $(this).keyup(function() {
            var textLength = $(this).val().length;

            $(this).next().html(textLength + ' / ' + textMax);
        });
    });


}