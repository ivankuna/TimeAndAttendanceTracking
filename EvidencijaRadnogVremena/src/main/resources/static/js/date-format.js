function formatDataBeforeSubmit() {
        var dateInputs = document.querySelectorAll(".date-input, .date-pick, .date-no-pick");

        for (var i = 0; i < dateInputs.length; i++) {
            var input = dateInputs[i];
            if (input.value.trim() !== "") {
                var parts = input.value.split(".");
                var day = parseInt(parts[0], 10);
                var month = parseInt(parts[1], 10);
                var year = parseInt(parts[2], 10);
                var date = new Date(year, month - 1, day);
                var formattedDate = date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2);
                input.value = formattedDate;
            }
        }
        enableAllFields();
    }

    function enableAllFields() {
        var inputFields = document.querySelectorAll('input');
        inputFields.forEach(field => {
            if (field.disabled) {
                field.removeAttribute('disabled');
            }
        });
    }