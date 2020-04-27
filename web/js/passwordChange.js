let newPasswordField;
let oldPasswordField;
let dataErrorMessage;
const typeOfDisplay = "inline";

/**
 * Validates new password field.
 *
 */
function validateNewPassword() {
    let validateRegexp = RegExp("[\\s]|^$");
    newPasswordField = document.getElementById("new-password-input");
    return !validateRegexp.test(newPasswordField.value);
}

/**
 * Validates old password field.
 *
 */
function validateOldPassword() {
    let validateRegexp = RegExp("[\\s]|^$");
    oldPasswordField = document.getElementById("old-password-input");
    return !validateRegexp.test(oldPasswordField.value);
}

/**
 * Validates form fields.
 *
 */
function validateForm(form) {
    clearErrorMessages();
    let newPasswordValidated = validateNewPassword();
    let oldPasswordValidated = validateOldPassword();

    if (!newPasswordValidated || !oldPasswordValidated) {
        dataErrorMessage = document.getElementById("data-error-message");
        dataErrorMessage.style.display = typeOfDisplay;
        newPasswordField.value = "";
        oldPasswordField.value = "";
    }

    if (newPasswordValidated && oldPasswordValidated) {
        if (confirm("Are you sure?")) {
            form.submit();
        }
    }
}

function clearErrorMessages() {
    if (newPasswordField && oldPasswordField && dataErrorMessage) {
        dataErrorMessage.style.display = "none";
    }
}