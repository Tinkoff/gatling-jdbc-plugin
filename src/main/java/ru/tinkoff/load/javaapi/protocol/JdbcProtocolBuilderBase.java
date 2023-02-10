package ru.tinkoff.load.javaapi.protocol;

import com.zaxxer.hikari.HikariConfig;

import javax.annotation.Nonnull;

public class JdbcProtocolBuilderBase {
    public JdbcProtocolBuilderBase() {}

    public JdbcProtocolBuilderUsernameStep url(@Nonnull String url) {
        return new JdbcProtocolBuilderUsernameStep(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderBase.url(url));
    }

    public JdbcProtocolBuilder hikariConfig(HikariConfig cfg) {
        return new JdbcProtocolBuilder(ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilderBase.hikariConfig(cfg));
    }
}
