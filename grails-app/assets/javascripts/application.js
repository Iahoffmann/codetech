// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-2.2.0.min
//= require jquery-ui-1.11.4/jquery-ui.js
//= require bootstrap
//= require_tree .
//= require_self

if (typeof jQuery !== 'undefined') {
    (function($) {
        $('#spinner').ajaxStart(function() {
            $(this).fadeIn();
        }).ajaxStop(function() {
            $(this).fadeOut();
        });

        var widgetNameInput = $('#widgieNameInput');
        widgetNameInput.autocomplete({source: function(request, response) {
                $.ajax({
                    url: "/widgie/autocomplete",
                    dataType: "json",
                    data: {},
                    success: function(data) {
                        var names = data.map(function(x) { return x.name});
                        response(names);
                    }
                });
            }});
    })(jQuery);
}
