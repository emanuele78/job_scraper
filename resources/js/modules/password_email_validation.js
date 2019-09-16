export const isEmailValid = function (email) {
    if (email == null) {
        return false;
    }
    let reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return reg.test(String(email).toLowerCase());
};

export const isPasswordValid = function (password) {
    if (password == null) {
        return false;
    }
    password = password.trim();
    //here can be added more rules
    return password.length >= 6;
};

export const passwordRuleDescription = function () {
    return "La password deve avere almeno 6 caratteri";
};
