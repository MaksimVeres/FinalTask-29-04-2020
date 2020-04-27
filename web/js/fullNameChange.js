let firstNameField;
let lastNameField;
let dataErrorMessage;
const typeOfDisplay = "inline";

/**
 * Validates first name field.
 *
 */
function validateFirstName() {
    let validateRegexp = RegExp("[\\s]|^$");
    firstNameField = document.getElementById("firstName-input");
    return !validateRegexp.test(firstNameField.value);
}

/**
 * Validates last name field.
 *
 */
function validateLastName() {
    let validateRegexp = RegExp("[\\s]|^$");
    lastNameField = document.getElementById("lastName-input");
    return !validateRegexp.test(lastNameField.value);
}

/**
 * Validates form fields.
 *
 */
function validateForm(form) {
    clearErrorMessages();
    let firstNameValidated = validateFirstName();
    let lastNameValidated = validateLastName();

    if (!firstNameValidated || !lastNameValidated) {
        dataErrorMessage = document.getElementById("data-error-message");
        dataErrorMessage.style.display = typeOfDisplay;
        firstNameField.value = "";
        lastNameField.value = "";
    }

    if (firstNameValidated && lastNameValidated) {
        if (confirm("Are you sure?")) {
            form.submit();
        }
    }
}

function clearErrorMessages() {
    if (firstNameField && lastNameField && dataErrorMessage) {
        dataErrorMessage.style.display = "none";
    }
}