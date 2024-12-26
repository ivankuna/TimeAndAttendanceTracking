$(document).ready(function() {
        $(".date-pick").datepicker({
            dateFormat: "dd.mm.yy",
            showOn: "both",
            buttonText: "<i class='far fa-calendar-alt fa-1x'></i>",
            onClose: function() {
                $(this).next('.date-trigger').focus();
            }
        });
        $(".date-trigger").click(function() {
            $(this).prev('.date-pick').datepicker('show');
        });

    $(".ui-datepicker-trigger").addClass("btn btn-outline-light text-primary").attr("style", function(i, style) {
            return (style || "") + "padding-left: 5px !important;";
        });
    });