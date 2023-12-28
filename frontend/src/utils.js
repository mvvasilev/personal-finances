let utils = {
    performRequest: async (url, options) => {
        return await fetch(url, options).then(resp => {
            if (resp.status === 401) {
                window.location.replace("https://localhost:8080/oauth2/authorization/authentik")

                throw "Unauthorized, please login.";
            }

            if (!resp.ok) {
                throw resp.status;
            }

            return resp;
        });
    },
    toPascalCase: (s) => {
        return s.replace(/(\w)(\w*)/g, (g0,g1,g2) => g1.toUpperCase() + g2.toLowerCase());
    }
}

export default utils;