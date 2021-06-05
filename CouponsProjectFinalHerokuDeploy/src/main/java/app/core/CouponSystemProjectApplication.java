package app.core;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import app.core.filters.LoginFilter;
import app.core.login.LoginManager;
import app.core.managers.JwtUtil;
import app.core.services.UserDetailLoadServer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CouponSystemProjectApplication {

	public static void main(String[] args) {

		 SpringApplication.run(CouponSystemProjectApplication.class, args);
		 
	}
	

	@Bean
	public FilterRegistrationBean<LoginFilter> loginFilterRegistration(UserDetailLoadServer userDetailLoadServer,
			JwtUtil tokenUtil, LoginManager loginManager) {
		// create a registration bean
		FilterRegistrationBean<LoginFilter> filterRegistrationBean = new FilterRegistrationBean<LoginFilter>();
		// create our login filter
		LoginFilter loginFilter = new LoginFilter(userDetailLoadServer, tokenUtil, loginManager);
		// do the registration
		filterRegistrationBean.setFilter(loginFilter);
		// set the url pattern for the filter
		filterRegistrationBean.addUrlPatterns("/admin/*", "/company/*", "/customer/*");
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<Filter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}

}
