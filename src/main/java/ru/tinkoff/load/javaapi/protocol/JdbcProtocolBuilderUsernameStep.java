package ru.tinkoff.load.javaapi.protocol;

public class JdbcProtocolBuilderUsernameStep {
    private final ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderUsernameStep wrapped;

    public JdbcProtocolBuilderUsernameStep(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderUsernameStep jdbcProtocolBuilderUsernameStep){
        this.wrapped = jdbcProtocolBuilderUsernameStep;
    }

    public JdbcProtocolBuilderPasswordStep username(String newValue) {
        return new JdbcProtocolBuilderPasswordStep(wrapped.username(newValue));
    }
}
