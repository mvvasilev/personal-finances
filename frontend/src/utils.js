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
    }
}

export default utils;