package api

import service.latestVersion

@Suppress("unused", "SpellCheckingInspection")
enum class Channel(
    val appId: Int,
    val channelId: Int,
    val packageName: String,
    val channelName: String,
) {
    IOS(-1, -1, "com.popcap.ios.chs.PVZ2", "iOS"),
    TapTap(109, 250, "com.popcap.pvz2cthdbk", "TapTap"),
    Official(109, 208, "com.popcap.pvz2cthdbk", "拓维官网包"),
    Huawei(109, 1030, "com.popcap.pvz2cthdhwct", "华为应用市场"),
    Qihoo(109, 16, "com.popcap.pvz2cthd360", "360应用市场"),
    Tencent(109, 132, "com.tencent.tmgp.pvz2hdtxyyb", "腾讯应用宝"),
    Xiaomi(109, 1013, "com.popcap.pvz2cthdxm", "小米应用商店"),
    Sisanjiujiu(109, 54, "com.popcap.pvz2cthd4399", "4399游戏"),
    Qiqiersan(109, 266, "com.popcap.pvz2cthdbazhang", "7723游戏"),
    Vivo(109, 1027, "com.popcap.pvz2cthdbbg", "vivo应用商店"),
    Oppo(109, 22, "com.popcap.pvz2cthdop", "OPPO应用商店"),
    Baidu(109, 1025, "com.popcap.pvz2cthd.g.baidu", "百度应用商店"),
    Jiuyou(109, 24, "com.popcap.pvz2cthduc", "九游"),
    Meizu(109, 1017, "com.popcap.pvz2cthdamz", "魅族"),
    Haoyoukuaibao(109, 261, "com.popcap.pvz2cthdbk", "好游快爆"),
    Bamenshenqi(109, 10022, "com.popcap.pvz2cthdbmsq", "八门神器"),
    Bilibili(109, 10024, "com.popcap.pvz2cthd.bilibili", "bilibili"),
    ;

    val version: String =
        latestVersion[channelName]?.version ?: latestVersion["拓维官网包"]!!.version
}
