document.querySelectorAll(".date-input, .date-pick").forEach(function(input) {

input.addEventListener("input", function(event) {
    let value = event.target.value;
    let keyPressed = event.inputType;
    if (value.length === 2 || value.length === 5) {
    event.target.value += ".";
    }
});

input.addEventListener("input", function(event) {
    let value = event.target.value;
    let keyPressed = event.inputType;

    if (keyPressed === "deleteContentBackward" || keyPressed === "deleteContentForward") {

      if (value.endsWith(".")) {
        event.target.value = value.slice(0, -1);
      }
    } else {
      if (value.length === 2 || value.length === 5) {
        event.target.value += ".";
      }
    }
});

input.addEventListener("blur", function(event) {
    let value = event.target.value;

    if (!value.match(/^\d{2}\.\d{2}\.\d{4}$/)) {
        event.target.value = "";
    } else {
        let parts = value.split('.');
        let day = parseInt(parts[0], 10);
        let month = parseInt(parts[1], 10) - 1;
        let year = parseInt(parts[2], 10);

        let dateObj = new Date(year, month, day);

        if (dateObj.getDate() !== day || dateObj.getMonth() !== month || dateObj.getFullYear() !== year) {
            event.target.value = "";
        }
    }
    });
});

let activeInput = null;

document.querySelectorAll(".date-input").forEach(function(input) {
  input.addEventListener("click", function(event) {

    if (activeInput !== input) {
      event.target.select();
      activeInput = input;
    }
  });

  input.addEventListener("focus", function(event) {

    if (activeInput === input) {
      event.target.setSelectionRange(0, 0);
    }
  });
});

function setTodayDateIfEmpty(event) {
  let input = event.target;
  if (input.value === "") {
    let today = new Date();
    let day = today.getDate().toString().padStart(2, '0');
    let month = (today.getMonth() + 1).toString().padStart(2, '0');
    let year = today.getFullYear();
    input.value = day + "." + month + "." + year;
  }
}

document.querySelectorAll(".date-input.auto-fill").forEach(function(input) {
  input.addEventListener("click", setTodayDateIfEmpty);
});