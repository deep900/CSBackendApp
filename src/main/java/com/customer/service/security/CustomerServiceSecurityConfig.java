/**
 * 
 */
package com.customer.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.session.SessionManagementFilter;

/**
 * @author Pradheep
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class CustomerServiceSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Bean
	public CustomerServiceAuthorizationFilter getAuthFilter() {
		return new CustomerServiceAuthorizationFilter();
	}

	@Bean
	public LogoutCheckFilter getLogoutCheckFilter() {
		return new LogoutCheckFilter();
	}

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		// httpSecurity.authorizeRequests().antMatchers("**/authenticate**").permitAll().and()
		// .addFilterAfter(getAuthFilter(),
		// LogoutFilter.class).authenticationProvider(authenticationProvider)
		// .addFilterBefore(getLogoutCheckFilter(),
		// SessionManagementFilter.class); */
		// httpSecurity.authorizeRequests().anyRequest().permitAll();
		httpSecurity.httpBasic().and().csrf().disable().formLogin().disable().authorizeRequests()
				.antMatchers("**/login").permitAll().and().addFilterAfter(getAuthFilter(), LogoutFilter.class)
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(getLogoutCheckFilter(), SessionManagementFilter.class);
	}

	@Override
	public void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
		authManagerBuilder.authenticationProvider(authenticationProvider);
	}

	@SuppressWarnings("deprecation")
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(getAuthFilter());
		registrationBean.setEnabled(false);
		return registrationBean;
	}

}
