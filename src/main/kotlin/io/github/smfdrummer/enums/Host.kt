@file:Suppress("HttpUrlsUsage")

package io.github.smfdrummer.enums

enum class GameHost(val url: String) {
    ANDROID("http://cloudpvz2android.ditwan.cn"),
    IOS("http://cloudpvz2ios.ditwan.cn")
}

enum class TalkwebHost(val url: String) {
    GET_LATEST_VERSION("https://pvz2.ditwan.cn/backend/api/latest_version/get_latest_version"),
    ABTEST("http://cert.talkyun.com.cn/ad-api/cert/abtest"),
    LOGIN("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/login"),
    LOGIN_NEW("https://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/loginNew"),
    RECORD_TERMINAL("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/recordTerminal"),
    UPDATE_USER_PHONE("http://cert.talkyun.com.cn/ad-api/cert/updateuserphone"),
    GEN_VERIFY_SMS("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/genVerifySms"),
    GEN_VERIFY_SMS_BY_WEB("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/genVerifySmsByWeb"),
    BIND_PHONE("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/bindPhone"),
    REGISTER("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/register"),
    MODIFY_PASSWORD("http://tgpay.talkyun.com.cn/tw-sdk/sdk-api/user/modifyPassWord"),

}

enum class Game4399Host(val url: String) {
    STATE("https://m.4399api.com/openapi/oauth-callback.html?gamekey=46619&game_key=124642"),
    LOGIN("https://ptlogin.4399.com/oauth2/loginAndAuthorize.do?channel=&sdk=op&sdk_version=3.14.5.577"),
}