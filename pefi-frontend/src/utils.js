import { v4 } from 'uuid';

let LEV_FORMAT = new Intl.NumberFormat('bg-BG', {
    style: 'currency',
    currency: 'BGN',
});

let utils = {
    performRequest: async (url, options) => {
        let opts = options ?? { headers: {} };

        let result = await fetch(url, {
            ...opts,
            headers: {
                ...opts.headers,
                'X-Requested-With': 'XMLHttpRequest'
            }
        });

        if (result.ok) {
            return result;
        }

        // If we are unauthorized, refresh the token, and try once more.
        if (result.status === 401) {
            let tokenResponse = await fetch("/refresh-token", {
                method: "POST",
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            });

            // If the token refresh failed, redirect to login
            if (!tokenResponse.ok) {
                window.location.replace(`${window.location.origin}/oauth2/authorization/authentik`);
            }

            // Try again
            let secondAttempt = await fetch(url, {
                ...opts,
                headers: {
                    ...opts.headers,
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            // If our second attempt failed as well after refresh, redirect to login
            if (!secondAttempt.ok && result.status === 401) {
                window.location.replace(`${window.location.origin}/oauth2/authorization/authentik`);
            }
        }

        // If the error wasn't unauthorized, just return the response
        return result;
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
    },
    isNullOrUndefined(obj) {
        return obj === null || obj === undefined;
    }
}

export default utils;