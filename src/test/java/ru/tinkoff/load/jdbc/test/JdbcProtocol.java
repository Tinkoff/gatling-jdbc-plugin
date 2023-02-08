package ru.tinkoff.load.jdbc.test;

import ru.tinkoff.load.javaapi.protocol.JdbcProtocolBuilder;

import static ru.tinkoff.load.javaapi.JdbcDsl.DB;

public class JdbcProtocol {
    public static JdbcProtocolBuilder dataBase = DB()
            .url("jdbc:h2:mem:test")
            .username("sa")
            .password("")
            .maximumPoolSize(23)
            .protocolBuilder();
}
