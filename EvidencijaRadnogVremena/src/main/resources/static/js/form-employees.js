document.addEventListener("DOMContentLoaded", function() {
    var employmentContractControl = document.getElementById("employmentContract");
    var reasonForDefiniteControl = document.getElementById("reasonForDefinite");
    var dateOfSignOutControl = document.getElementById("dateOfSignOut");
    var foreignNationalCheckbox = document.getElementById('foreignNational');
    var expiryDateOfWorkPermitInput = document.getElementById('expiryDateOfWorkPermit');
    var workingHoursControl = document.getElementById("workingHours");
    var hoursForPartTimeControl = document.getElementById("hoursForPartTime");
    var additionalWorkCheckbox = document.getElementById("additionalWork");
    var additionalWorkHoursControl = document.getElementById("additionalWorkHours");

    function toggleReasonForDefinite() {
        if (employmentContractControl.value === "Određeno") {
            reasonForDefiniteControl.disabled = false;
            dateOfSignOutControl.disabled = false;
        } else {
            reasonForDefiniteControl.disabled = true;
            dateOfSignOutControl.disabled = true;
            reasonForDefiniteControl.value = "";
            dateOfSignOutControl.value = "";
        }
    }

    function toggleExpiryDateOfWorkPermitInput() {
        if (foreignNationalCheckbox.checked) {
            expiryDateOfWorkPermitInput.disabled = false;
        } else {
            expiryDateOfWorkPermitInput.disabled = true;
            expiryDateOfWorkPermitInput.value = "";
        }
    }

    function toggleReasonForHoursForPartTime() {
        if (workingHoursControl.value === "Nepuno") {
            hoursForPartTimeControl.disabled = false;
        } else {
            hoursForPartTimeControl.disabled = true;
            hoursForPartTimeControl.value = "";
        }
    }

    function toggleAdditionalWorkInput() {
            if (additionalWorkCheckbox.checked) {
                additionalWorkHoursControl.disabled = false;
                workingHoursControl.disabled = true;
                workingHoursControl.value = "";
                hoursForPartTimeControl.disabled = true;
                hoursForPartTimeControl.value = "";
            } else {
                additionalWorkHoursControl.disabled = true;
                additionalWorkHoursControl.value = "";
                workingHoursControl.disabled = false;
                if (workingHoursControl.value === "") {
                    workingHoursControl.value = "Puno";
                }
            }
        }

      function enableHiddenInputs() {
          var hiddenInputs = document.querySelectorAll('input[type="hidden"]');
          hiddenInputs.forEach(function(input) {
              input.disabled = false;
          });
      }

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

      enableHiddenInputs();
      toggleReasonForDefinite();
      toggleExpiryDateOfWorkPermitInput();
      toggleReasonForHoursForPartTime();
      toggleAdditionalWorkInput();
      toggleDatePickButtons();

       employmentContractControl.addEventListener("change", function() {
              toggleReasonForDefinite();
              toggleDatePickButtons();
          });
       foreignNationalCheckbox.addEventListener("change", function() {
                    toggleExpiryDateOfWorkPermitInput();
                    toggleDatePickButtons();
                });

      workingHoursControl.addEventListener("change", toggleReasonForHoursForPartTime);
      additionalWorkCheckbox.addEventListener('change', toggleAdditionalWorkInput);

});
