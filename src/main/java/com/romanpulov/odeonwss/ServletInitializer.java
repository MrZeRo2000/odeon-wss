package com.romanpulov.odeonwss;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class ServletInitializer extends SpringBootServletInitializer {

	private Map<String, Object> initParamProps = Map.of();

	@Override
	public void onStartup(ServletContext sc) throws ServletException {
		Map<String, Object> p = new HashMap<>();

		p.put("app.dbUrl", sc.getInitParameter("db-url"));
		p.put("app.mp3Path", sc.getInitParameter("mp3-path"));
		p.put("app.laPath", sc.getInitParameter("la-path"));
		p.put("app.classicsPath", sc.getInitParameter("classics-path"));
		p.put("app.dvMusicPath", sc.getInitParameter("dv-music-path"));
		p.put("app.dvMoviesPath", sc.getInitParameter("dv-movies-path"));
		p.put("app.dvAnimationPath", sc.getInitParameter("dv-animation-path"));
		p.put("app.ffProbePath", sc.getInitParameter("ffprobe-path"));
		p.put("app.mediaInfoPath", sc.getInitParameter("mediainfo-path"));
		p.put("app.mdbPath", sc.getInitParameter("mdb-path"));

		this.initParamProps = Map.copyOf(p);

		super.onStartup(sc);
	}

	@Override
	protected @NotNull SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application
				.sources(OdeonWssApplication.class)
				.initializers(ctx ->
						ctx
								.getEnvironment()
								.getPropertySources()
								.addFirst(new MapPropertySource("servletInitParams", initParamProps)));
	}

}
