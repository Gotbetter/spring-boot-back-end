package pcrc.gotbetter.user.login_method.jwt.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final JwtProvider jwtProvider;

	@Override
	public void configure(HttpSecurity httpSecurity) {
		JwtFilter customFilter = new JwtFilter(jwtProvider);
		httpSecurity.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
