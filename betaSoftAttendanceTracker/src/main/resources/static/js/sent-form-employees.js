document.addEventListener("DOMContentLoaded", function() {
function toggleDatePickButtons() {
    setTimeout(function() {
        var datePickContainers = document.querySelectorAll('.datepicker-container');
        datePickContainers.forEach(function(container) {
            var input = container.querySelector('.date-pick');
            var clearButton = container.querySelector('.clear-date-button');
            var datePickerButton = container.querySelector('.ui-datepicker-trigger');

            if (!input) console.log("Input element not found in container:", container);
            if (!clearButton) console.log("Clear button not found in container:", container);
            if (!datePickerButton) console.log("Date picker button not found in container:", container);

            if (!input || !clearButton || !datePickerButton) return;

            if (input.disabled || input.getAttribute('disabled') === 'disabled' || input.getAttribute('disabled') === 'true') {
                clearButton.disabled = true;
                datePickerButton.disabled = true;
            } else {
                clearButton.disabled = false;
                datePickerButton.disabled = false;
            }
        });
    }, 100);
}
    toggleDatePickButtons();
});
