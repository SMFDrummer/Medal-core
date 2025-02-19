package api

enum class GameHost(val url: String) {
    ANDROID("http://cloudpvz2android.ditwan.cn"),
    IOS("http://cloudpvz2ios.ditwan.cn")
}

enum class TalkwebHost(val url: String) {
    GET_LATEST_VERSION("https://pvz2.ditwan.cn/backend/api/latest_version/get_latest_version"),
    ABTEST("http://cert.talkyun.com.cn/ad-api/cert/abtest"),
    LOGIN("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/login"),
    RECORD_TERMINAL("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/recordTerminal"),
    UPDATE_USER_PHONE("http://cert.talkyun.com.cn/ad-api/cert/updateuserphone"),
    GEN_VERIFY_SMS("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/genVerifySms"),
    REGISTER("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/register"),
    MODIFY_PASSWORD("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/modifyPassWord"),

}