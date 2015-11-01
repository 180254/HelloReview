$(document).ready(function () {
    'use strict';

    prettyPrint();
    $("input.slider").slider();
    formsTextareaLettersCounter();
    autosize($('textarea'));
});

function formsTextareaLettersCounter() {
    'use strict';

    $('.form-main').find('textarea').each(function () {

        var textMax = $(this).attr('maxlength');
        $(this).next().html('0 / ' + textMax);

        $(this).keyup(function() {
            var textLength = $(this).val().length;

            $(this).next().html(textLength + ' / ' + textMax);
        });
    });


}