function formatNumberOnInput(input) {
    if (input.value.trim() !== "") {
        input.value = input.value.replace(/,/g, ".");
        input.value = input.value.replace(/[^\d.]/g, (match) => {
            return match !== '.' ? '' : '.';
        });
        input.value = input.value.replace(/^(.*\..*)\./g, '$1');
    }
}