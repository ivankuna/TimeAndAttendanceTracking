    function setDropdownHeight() {
    var reasonForUpdateElement = document.getElementById("reasonForUpdate");
    if (reasonForUpdateElement && reasonForUpdateElement.type !== 'hidden') {
        var dropdown = document.getElementById('reasonForUpdate');
        var numOptions = dropdown.options.length;
        dropdown.size = numOptions;
        }
    }

    window.onload = setDropdownHeight;