package pl.jsieczczynski.SpringBootRedditClone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import pl.jsieczczynski.SpringBootRedditClone.model.Role;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    public static class RestApiSecurityConfig {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http
                    .antMatcher("/api/**")
                    .authorizeHttpRequests()
                    .antMatchers("/api/auth/**")
                    .permitAll()
                    .antMatchers("/api/**")
                    .authenticated()
//                    .anyRequest()
//                    .authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }

    @Order(2)
    @Configuration
    public static class LoginFormSecurityConfig {
        @Bean
        public SecurityFilterChain mvcFilterChain
                (HttpSecurity http) throws Exception {
            http.csrf().disable();
            http
                    .antMatcher("/*")
                    .authorizeRequests()
                    .antMatchers("/", "/home", "/about")
                    .permitAll()
                    .antMatchers("/admin/**")
                    .hasAnyAuthority(Role.ADMIN.name())
                    .antMatchers("/user/**")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
//                    .hasAnyRole("USER")
//                    .hasAnyAuthority(Role.USER.name())
//                    .antMatchers("/*")
//                    .anyRequest()
//                    .authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();

            return http.build();
        }
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        return web -> web.httpFirewall(firewall)
                .ignoring()
                .antMatchers("/styles/**", "/js/**", "/images/**", "/fonts/**", "/resources/**");
    }
}
