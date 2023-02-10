package ru.tinkoff.load.javaapi.protocol;

public class JdbcProtocolBuilderPasswordStep {
    private final ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderPasswordStep wrapped;

    public JdbcProtocolBuilderPasswordStep(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderPasswordStep jdbcProtocolBuilderPasswordStep){
        this.wrapped = jdbcProtocolBuilderPasswordStep;
    }

    public JdbcProtocolBuilderConnectionSettingsStep password(String newValue) {
        return new JdbcProtocolBuilderConnectionSettingsStep(wrapped.password(newValue));
    }
}
