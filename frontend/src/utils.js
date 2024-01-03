import { v4 } from 'uuid';

let LEV_FORMAT = new Intl.NumberFormat('bg-BG', {
    style: 'currency',
    currency: 'BGN',
});

let utils = {
    performRequest: async (url, options) => {
        return await fetch(url, options).then(resp => {
            if (resp.status === 401) {
                window.location.replace(`${window.location.origin}/oauth2/authorization/authentik`)

                throw "Unauthorized, please login.";
            }

            if (!resp.ok) {
                throw resp.status;
            }

            return resp;
        });
    },
    isSpinnerShown: () => {
        return localStorage.getItem("SpinnerShowing") === "true";
    },
    showSpinner: () => {
        localStorage.setItem("SpinnerShowing", "true");
        window.dispatchEvent(new Event("onSpinnerStatusChange"));
    },
    hideSpinner: () => {
        localStorage.removeItem("SpinnerShowing");
        window.dispatchEvent(new Event("onSpinnerStatusChange"));
    },
    toPascalCase: (s) => {
        return s.replace(/(\w)(\w*)/g, (g0,g1,g2) => g1.toUpperCase() + g2.toLowerCase());
    },
    generateUUID: () => v4(),
    isNumeric: (value) => {
        return /^-?\d+(\.\d+)?$/.test(value);
    },
    formatCurrency(number) {
        return LEV_FORMAT.format(number);
    }
}

export default utils;