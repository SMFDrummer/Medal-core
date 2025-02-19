package dsl

import JsonPrimitiveBuilder
import config.StrategyConfig
import kotlinx.serialization.json.*
import packet.ext
import primitive

/**
 * 基本策略类，策略结构体
 *
 * @param version 版本号
 * @param description 策略描述
 * @see packet
 */
class StrategyBuilder internal constructor() {
    /**
     * 版本号
     *
     * ```
     * version = 1
     * ```
     */
    var version: Int = 1

    /**
     * 策略描述
     *
     * ```
     * description = "策略描述"
     * ```
     */
    var description: String = "null"
    internal val packets = mutableListOf<StrategyConfig.PacketConfig>()

    /**
     * packet DSL
     *
     * 用于向基本策略结构中添加一个数据包
     *
     * ```
     * packet {
     *     i = "V316"
     * }
     * ```
     *
     * @see PacketBuilder
     */
    fun packet(block: PacketBuilder.() -> Unit) {
        packets.add(PacketBuilder().apply(block).build())
    }
}

/**
 * t 结构体构建类
 *
 * @see obj
 * @see arr
 * @see ins
 * @see put
 */
class JsonContentBuilder {
    private val map: MutableMap<String, JsonElement> = linkedMapOf()

    /**
     * JsonObject DSL
     *
     * 向数据包 t 结构体中添加一个 Json 对象
     *
     * ```
     * obj("key") {
     *     put("key", value) // else...
     * }
     * ```
     *
     * @see buildJsonObject
     */
    fun obj(key: String, block: JsonObjectBuilder.() -> Unit) {
        map[key] = buildJsonObject(block)
    }

    /**
     * JsonArray DSL
     *
     * 向数据包 t 结构体中添加一个 Json 数组
     *
     * ```
     * arr("key") {
     *     add(element) // else...
     * }
     * ```
     *
     * @see buildJsonArray
     */
    fun arr(key: String, block: JsonArrayBuilder.() -> Unit) {
        map[key] = buildJsonArray(block)
    }

    /**
     * packet insert DSL
     *
     * 向数据包中添加预设键，此键值会从缓存中自动取出
     *
     * @param key 预添加或修改的键名
     * @param insert 缓存中的变量名或以 数据包标识.d.指定键..等 为结构的路径
     *
     * ```
     * ins("key") { "value" } // 在变量缓存中查找变量名为 value 的值
     * ins("key") { "V123.d.key" } // 在响应缓存中查找数据包 V123 响应中 d 的 key 键的值
     * ```
     * @suppress 缓存中不存在的变量名、不存在的数据包
     *
     * 若取值时因数据包未能发送成功或路径格式有误
     *
     * 发包时会填入缺省值 [__MISSING_${expression}__]
     */
    fun ins(key: String, insert: () -> String) {
        map[key] = JsonPrimitive(insert().ext())
    }

    /**
     * put DSL
     *
     * 向数据包中填入键值
     *
     * ```
     * put("key", value) // else...
     * ```
     */
    private fun put(key: String, value: JsonPrimitive) {
        map[key] = value
    }

    /**
     * ```
     * put("key", "stringValue") // or null
     * ```
     */
    fun put(key: String, value: String?) = put(key, JsonPrimitive(value))

    /**
     * ```
     * put("key", 0) // or 0.0, 3e8, null
     * ```
     */
    fun put(key: String, value: Number?) = put(key, JsonPrimitive(value))

    /**
     * ```
     * put("key", true) // or false, null
     * ```
     */
    fun put(key: String, value: Boolean?) = put(key, JsonPrimitive(value))

    internal fun build(): Map<String, JsonElement> = map
}

@DslMarker
annotation class PacketDsl

/**
 * Packet 构建类
 *
 * @see i
 * @see r
 * @see t
 * @see repeat
 * @see retry
 * @see ext
 * @see onSuccess
 * @see onFailure
 */
@PacketDsl
class PacketBuilder {
    /**
     * 数据包标识
     *
     * ```
     * i = "V316"
     * ```
     * @throws UninitializedPropertyAccessException 未初始化标识
     */
    lateinit var i: String

    /**
     * 响应码
     *
     * ```
     * r = 0
     * ```
     */
    var r: Int = 0
    private val t: MutableMap<String, JsonElement> = linkedMapOf()

    /**
     * 数据包循环发送次数，如遇 [r] 不匹配会提前被终止
     *
     * ```
     * cycle = 1 // 0 不发送
     * ```
     */
    var repeat: Int = 1

    /**
     * 发包失败重试次数
     *
     * 需要注意的是，在重试数据包时同样会依照 [repeat] 的值进行新一轮的单包循环
     *
     * 在 [repeat] 数值过大时请谨慎重试
     */
    var retry: Int = 0
    private val extract: MutableMap<String, String> = linkedMapOf()
    private var onSuccess: JsonPrimitive? = null
    private var onFailure: JsonPrimitive? = null

    /**
     * t DSL
     *
     * 数据包体构建顶层函数
     * ```
     * t {
     *     put("key", value)
     * }
     * ```
     * @see JsonContentBuilder
     */
    fun t(block: JsonContentBuilder.() -> Unit) {
        t.putAll(JsonContentBuilder().apply(block).build())
    }

    /**
     * extract DSL
     *
     * 向数据包响应中取出指定路径的值并存入缓存中
     *
     * @param variableName 存入缓存中设定的变量名
     * @param jsonPath 在响应 d 中想要取出并存入缓存的路径
     *
     * ```
     * ext("sk") { "sk" } // 缓存名为 sk 的位于 [i].d.sk 的值
     * ```
     */
    fun ext(variableName: String, jsonPath: () -> String) {
        extract[variableName] = "d." + jsonPath()
    }

    /**
     * onSuccess DSL
     *
     * 这是一个整合量，它在数据包判定为发送成功时起作用，以下列出了所有可行的值，可以不写
     *
     * null 意为数据包发送成功了什么也不做，继续执行下一个数据包，也是这个键的默认值
     *
     * 整数 意为数据包发送成功了就直接执行指定序号的数据包，并且会强制打断当前数据包循环以及重试，当然这个序号是你自己从上往下数的
     *
     * 如果在这里你填了这个数据包的序号，那么恭喜你，这个策略运行到这里会变成一个死循环，直到这个数据包出错
     *
     * 也就是说你可以有选择地回到之前的数据包重新执行，也可以跳过下面的部分数据包接续执行
     *
     * true 意为数据包发送成功了就认定为整个策略发送成功，此时所有未进行的循环及数据包将全部打断结束，并将成功返回给程序
     *
     * false 意为数据包发送成功了就认定为整个策略发送失败，此时所有未进行的循环及数据包将全部打断结束，并将失败返回给程序
     *
     * ```
     * onSuccess { null }
     * ```
     */
    fun onSuccess(block: JsonPrimitiveBuilder.() -> Any?) {
        onSuccess = primitive(block)
    }

    /**
     * onFailure DSL
     *
     * 这是一个整合量，它在数据包判定为发送失败时起作用，以下列出了所有可行的值，可以不写
     *
     * null 意为数据包发送失败了什么也不做，继续执行下一个数据包，也是这个键的默认值
     *
     * 整数 意为数据包发送失败了就直接执行指定序号的数据包，并且会强制打断当前数据包循环以及重试，当然这个序号是你自己从上往下数的
     *
     * 如果在这里你填了这个数据包的序号，那么恭喜你，这个策略运行到这里会变成一个死循环，直到这个数据包出错
     *
     * 你可以有选择地回到之前的数据包重新执行，也可以跳过下面的部分数据包接续执行
     *
     * true 意为数据包发送失败了就认定为整个策略发送成功，此时所有未进行的循环及数据包将全部打断结束，并将成功返回给程序
     *
     * false 意为数据包发送失败了就认定为整个策略发送失败，此时所有未进行的循环及数据包将全部打断结束，并将失败返回给程序
     *
     * ```
     * onFailure { null }
     * ```
     */
    fun onFailure(block: JsonPrimitiveBuilder.() -> Any?) {
        onFailure = primitive(block)
    }

    internal fun build() = StrategyConfig.PacketConfig(
        i = i,
        r = r,
        t = t,
        repeat = repeat,
        retry = retry,
        extract = extract,
        onSuccess = onSuccess,
        onFailure = onFailure
    )
}

/**
 * 顶层策略构造函数
 *
 * @sample strategy.androidCredential
 * @see StrategyBuilder
 */
fun buildStrategy(buildAction: StrategyBuilder.() -> Unit): StrategyConfig {
    val builder = StrategyBuilder().apply(buildAction)
    return StrategyConfig(builder.version, builder.description, builder.packets)
}