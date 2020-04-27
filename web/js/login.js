let loginField;
let passwordField;
let loginErrorMessage;
let passwordErrorMessage;
const typeOfDisplay = "inline";

/**
 * Validates login field.
 *
 */
function validateLogin() {
    let validateRegexp = RegExp("[\\s]|^$");
    loginField = document.getElementById("login-input");
    return !validateRegexp.test(loginField.value);
}

/**
 * Validates password field.
 *
 */
function validatePassword() {
    let validateRegexp = RegExp("[\\s]|^$");
    passwordField = document.getElementById("password-input");
    return !validateRegexp.test(passwordField.value);
}

/**
 * Validates form fields.
 *
 */
function validateForm(form) {
    clearErrorMessages();
    let loginValidated = validateLogin();
    let passwordValidated = validatePassword();

    if (!loginValidated) {
        loginErrorMessage = document.getElementById("login-error-message");
        loginErrorMessage.style.display = typeOfDisplay;
        loginField.value = "";
    }

    if (!passwordValidated) {
        passwordErrorMessage = document.getElementById("password-error-message");
        passwordErrorMessage.style.display = typeOfDisplay;
        passwordField.value = "";
    }

    if (loginValidated && passwordValidated) {
        form.submit();
    }
}

function clearErrorMessages() {
    if (loginField && loginErrorMessage) {
        loginErrorMessage.style.display = "none";
    }
    if (passwordField && passwordErrorMessage) {
        passwordErrorMessage.style.display = "none";
    }
}