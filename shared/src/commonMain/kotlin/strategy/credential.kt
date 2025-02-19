package strategy

import dsl.buildStrategy

fun androidCredential() = buildStrategy {
    version = 1
    description = "Android - 获取用户凭据"

    packet {
        i = "V202"
        retry = 4
        ext("sk") { "sk" }
        ext("ui") { "ui" }
        onSuccess { true }
    }
}

fun iosCredentialRandom() = buildStrategy {
    version = 1
    description = "IOS - 获取用户凭据 - 随机用户"

    packet {
        i = "V201"
        retry = 4
        ext("ui") { "ui" }
        ext("sk") { "sk" }
    }

    packet {
        i = "V203"
        retry = 4
        t {
            ins("ui") { "ui" }
            ins("sk") { "sk" }
        }
        ext("pi") { "pi" }
        onSuccess { true }
    }
}

fun iosCredentialNonRandom() = buildStrategy {
    version = 1
    description = "IOS - 获取用户凭据 - 非随机用户"

    packet {
        i = "V201"
        retry = 4
        ext("ui") { "ui" }
        ext("sk") { "sk" }
    }

    packet {
        i = "V202"
        retry = 4
        t {
            ins("ui") { "ui" }
            ins("sk") { "sk" }
        }
        ext("pi") { "pi" }
        onSuccess { true }
    }
}