package ru.tinkoff.load.javaapi.protocol;

import scala.compat.java8.DurationConverters;

import java.time.Duration;

public class JdbcProtocolBuilderConnectionSettingsStep {
    private final ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderConnectionSettingsStep wrapped;

    public JdbcProtocolBuilderConnectionSettingsStep(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderConnectionSettingsStep jdbcProtocolBuilderConnectionSettingsStep){
        this.wrapped = jdbcProtocolBuilderConnectionSettingsStep;
    }

    public JdbcProtocolBuilder protocolBuilder(){
        return new JdbcProtocolBuilder(wrapped.protocolBuilder());
    }

    public JdbcProtocolBuilderConnectionSettingsStep maximumPoolSize(Integer newValue){
        return new JdbcProtocolBuilderConnectionSettingsStep(wrapped.maximumPoolSize(newValue));
    }

    public JdbcProtocolBuilderConnectionSettingsStep minimumIdleConnections(Integer newValue){
        return new JdbcProtocolBuilderConnectionSettingsStep(wrapped.minimumIdleConnections(newValue));
    }

    public JdbcProtocolBuilderConnectionSettingsStep connectionTimeout(Duration newValue){
        return new JdbcProtocolBuilderConnectionSettingsStep(wrapped.connectionTimeout(DurationConverters.toScala(newValue)));
    }
}
