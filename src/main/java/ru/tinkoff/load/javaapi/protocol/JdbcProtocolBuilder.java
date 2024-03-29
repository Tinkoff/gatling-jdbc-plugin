package ru.tinkoff.load.javaapi.protocol;

import io.gatling.javaapi.core.ProtocolBuilder;

public class JdbcProtocolBuilder implements ProtocolBuilder {
    private final ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilder wrapped;

    public JdbcProtocolBuilder(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilder jdbcProtocolBuilder){
        this.wrapped = jdbcProtocolBuilder;
    }
    @Override
    public io.gatling.core.protocol.Protocol protocol() {
        return wrapped.build();
    }
}
