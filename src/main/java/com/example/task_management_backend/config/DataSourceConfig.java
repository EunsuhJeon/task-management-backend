package com.example.task_management_backend.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
	) {
		String cleanedUrl = sanitizeUrl(rawUrl);
		ParsedDb parsed = parse(cleanedUrl);

		String resolvedUsername = StringUtils.hasText(username) ? username.trim() : parsed.username();
		String resolvedPassword = StringUtils.hasText(password) ? password : parsed.password();

		if (!StringUtils.hasText(resolvedUsername)) {
			throw new IllegalArgumentException(
					"Database username is missing. Set DATABASE_USERNAME or include user in DATABASE_URL."
			);
		}

		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(parsed.jdbcUrl());
		dataSource.setUsername(resolvedUsername);
		dataSource.setPassword(resolvedPassword == null ? "" : resolvedPassword);
		dataSource.setDriverClassName("org.postgresql.Driver");
		return dataSource;
	}

	private static String sanitizeUrl(String rawUrl) {
		if (rawUrl == null) {
			throw new IllegalArgumentException("DATABASE_URL is missing");
		}
		String url = rawUrl.trim()
				.replace("\n", "")
				.replace("\r", "")
				.replace(" ", "");

		// Common paste mistake: "postgres://user: PASS@host" or spaces anywhere
		return url;
	}

	private static ParsedDb parse(String rawUrl) {
		try {
			if (rawUrl.startsWith("jdbc:postgresql://") || rawUrl.startsWith("jdbc:postgres://")) {
				String jdbc = rawUrl.replace("jdbc:postgres://", "jdbc:postgresql://");
				return new ParsedDb(ensureSsl(jdbc), null, null);
			}

			if (isLibPqStyle(rawUrl)) {
				URI uri = new URI(rawUrl);
				String host = uri.getHost();
				if (!StringUtils.hasText(host)) {
					throw new IllegalArgumentException(
							"DATABASE_URL host is missing. Copy Internal Database URL from Render Postgres."
					);
				}

				int port = uri.getPort() > 0 ? uri.getPort() : 5432;
				String path = uri.getPath();
				if (!StringUtils.hasText(path) || "/".equals(path)) {
					throw new IllegalArgumentException(
							"DATABASE_URL database name is missing. Expected .../dbname at the end."
					);
				}

				String user = null;
				String pass = null;
				String userInfo = uri.getUserInfo();
				if (StringUtils.hasText(userInfo)) {
					String[] parts = userInfo.split(":", 2);
					user = decode(parts[0]);
					pass = parts.length > 1 ? decode(parts[1]) : "";
				}

				StringBuilder jdbc = new StringBuilder("jdbc:postgresql://")
						.append(host)
						.append(':')
						.append(port)
						.append(path);

				if (StringUtils.hasText(uri.getQuery())) {
					jdbc.append('?').append(uri.getQuery());
				}

				return new ParsedDb(ensureSsl(jdbc.toString()), user, pass);
			}

			throw new IllegalArgumentException(
					"Unsupported DATABASE_URL format. Use postgres://... or jdbc:postgresql://..."
			);
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(
					"Invalid DATABASE_URL. Remove spaces and paste the Internal Database URL from Render. value="
							+ mask(rawUrl),
					ex
			);
		}
	}

	private static boolean isLibPqStyle(String url) {
		return url.startsWith("postgres://") || url.startsWith("postgresql://");
	}

	private static String ensureSsl(String jdbcUrl) {
		boolean looksRemote = jdbcUrl.contains("render.com")
				|| jdbcUrl.contains("amazonaws.com")
				|| jdbcUrl.contains("dpg-");
		if (!looksRemote || jdbcUrl.contains("sslmode=")) {
			return jdbcUrl;
		}
		return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
	}

	private static String decode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}

	private static String mask(String url) {
		return url.replaceAll("://([^:/@]+):([^@]+)@", "://$1:***@");
	}

	private record ParsedDb(String jdbcUrl, String username, String password) {
	}
}
