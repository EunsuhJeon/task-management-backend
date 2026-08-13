package com.example.task_management_backend.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Supports local JDBC URLs and Render-style {@code postgres://user:pass@host:port/db} URLs.
 */
@Configuration
public class DataSourceConfig {

	@Bean
	@ConditionalOnProperty(name = "spring.datasource.url")
	public DataSource dataSource(
			@Value("${spring.datasource.url}") String rawUrl,
			@Value("${spring.datasource.username:}") String username,
			@Value("${spring.datasource.password:}") String password
	) throws URISyntaxException {
		String jdbcUrl = toJdbcUrl(rawUrl);
		String resolvedUsername = username;
		String resolvedPassword = password;

		if (isLibPqStyle(rawUrl)) {
			URI uri = new URI(rawUrl);
			String userInfo = uri.getUserInfo();
			if (StringUtils.hasText(userInfo)) {
				String[] parts = userInfo.split(":", 2);
				resolvedUsername = parts[0];
				resolvedPassword = parts.length > 1 ? parts[1] : "";
			}
		}

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUsername(resolvedUsername);
		dataSource.setPassword(resolvedPassword);
		dataSource.setDriverClassName("org.postgresql.Driver");
		return dataSource;
	}

	private static boolean isLibPqStyle(String url) {
		return url.startsWith("postgres://") || url.startsWith("postgresql://");
	}

	static String toJdbcUrl(String rawUrl) {
		if (rawUrl.startsWith("jdbc:")) {
			return ensureSsl(rawUrl);
		}

		if (isLibPqStyle(rawUrl)) {
			try {
				URI uri = new URI(rawUrl);
				String host = uri.getHost();
				int port = uri.getPort() > 0 ? uri.getPort() : 5432;
				String path = uri.getPath(); // includes leading /
				String query = uri.getQuery();

				StringBuilder jdbc = new StringBuilder("jdbc:postgresql://")
						.append(host)
						.append(':')
						.append(port)
						.append(path == null ? "" : path);

				if (StringUtils.hasText(query)) {
					jdbc.append('?').append(query);
				}

				return ensureSsl(jdbc.toString());
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Invalid DATABASE_URL: " + rawUrl, ex);
			}
		}

		return ensureSsl(rawUrl);
	}

	private static String ensureSsl(String jdbcUrl) {
		// Local Docker/dev usually doesn't need SSL; Render Postgres does.
		boolean looksRemote = jdbcUrl.contains("render.com") || jdbcUrl.contains("amazonaws.com");
		if (!looksRemote) {
			return jdbcUrl;
		}
		if (jdbcUrl.contains("sslmode=")) {
			return jdbcUrl;
		}
		return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
	}
}
