package com.authentik.network;

import com.authentik.utils.Constant;

public class ApiUtils {

    private ApiUtils() {}

    public static APIService getAPIService() {

        return RetrofitClientInstance.getRetrofitInstance(Constant.BASE_URL).create(APIService.class);
    }
}
