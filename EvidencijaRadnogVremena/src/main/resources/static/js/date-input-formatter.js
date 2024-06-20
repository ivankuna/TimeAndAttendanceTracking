window.addEventListener('DOMContentLoaded', (event) => {
            var dateInputs = document.querySelectorAll(".date-input, .date-pick");

            dateInputs.forEach(function(input) {
                var originalDate = input.value;
                if (originalDate !== '') {
                    var formattedDate = formatDate(originalDate);
                    input.value = formattedDate;
                }
            });

            function formatDate(dateString) {
                var dateParts = dateString.split('-');
                var formattedDate = dateParts[2] + '.' + dateParts[1] + '.' + dateParts[0];
                return formattedDate;
            }
        });