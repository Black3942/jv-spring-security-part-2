package mate.academy.spring.config;

import mate.academy.spring.model.RoleName;
import mate.academy.spring.security.CustomUserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String ADMIN_ROLE = RoleName.ADMIN.getName();
    public static final String USER_ROLE = RoleName.USER.getName();
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers(HttpMethod.DELETE, "/movie-sessions/{id}").hasRole(ADMIN_ROLE)
                .antMatchers(HttpMethod.PUT, "/movie-sessions/{id}").hasRole(ADMIN_ROLE)
                .antMatchers("/orders/**", "/shopping-carts/**").hasRole(USER_ROLE)
                .antMatchers("/users/by-email").hasRole(ADMIN_ROLE)
                .antMatchers(HttpMethod.GET, "/cinema-halls", "/movies",
                        "/movie-sessions/**").hasAnyRole(USER_ROLE, ADMIN_ROLE)
                .antMatchers(HttpMethod.POST, "/cinema-halls", "/movies",
                        "/movie-sessions").hasRole(ADMIN_ROLE)
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }
}
