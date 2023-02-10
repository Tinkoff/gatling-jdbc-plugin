package ru.tinkoff.load.jdbc.test

import ru.tinkoff.load.javaapi.protocol.JdbcProtocolBuilder

import  ru.tinkoff.load.javaapi.JdbcDsl.DB

object KtJdbcProtocol {
    var dataBase: JdbcProtocolBuilder = DB()
            .url("jdbc:h2:mem:test")
            .username("sa")
            .password("")
            .maximumPoolSize(23)
            .protocolBuilder()
}
