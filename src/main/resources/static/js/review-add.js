/*jslint browser: true*/
/*global $, jQuery */

function downloadRepoList() {
    'use strict';

    var $select = $('#review-add-repository'),
        $button = $('#review-add-submit');

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
        var $form = $('<form>', {
                action: '/github-issue',
                method: 'post'
            }),
            $input = $('<input>', {
                type: 'hidden',
                name: 'message',
                value: jqXHR.responseText
            }),
            $csrfInput = $('<input>', {
                name: $("meta[name='_csrf_parameterName']").attr('content'),
                value: $("meta[name='_csrf']").attr('content'),
                type: 'hidden'
            });

        $form.append($input);
        $form.append($csrfInput);
        $('body').append($form);
        $form.submit();
    });
}

$(document).ready(function () {
    'use strict';

    downloadRepoList();
});
