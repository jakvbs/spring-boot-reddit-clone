package pl.jsieczczynski.SpringBootRedditClone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import pl.jsieczczynski.SpringBootRedditClone.model.Role;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Order(1)
    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public static class JwtSecurityConfig {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

            http.csrf().disable();
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .antMatchers("/api/auth/signup/**", "/api/auth/signin/**")
                    .permitAll()
                    .antMatchers("/api/auth/**")
                    .authenticated()
                    .antMatchers(POST, "/api/**")
                    .authenticated()
//                    .anyRequest()
//                    .authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    }

    @Order(2)
    @Configuration
    public static class MvcSecurityConfig {
        @Bean
        public SecurityFilterChain mvcFilterChain
                (HttpSecurity http) throws Exception {
            http.csrf().disable();
            http
                    .antMatcher("/**")
                    .authorizeHttpRequests()
                    .antMatchers("/*")
                    .hasAnyAuthority(Role.ADMIN.name())
//                    .hasAnyRole("USER")
//                    .hasAnyAuthority(Role.USER.name())
                    .antMatchers("/*")
//                    .anyRequest()
                    .authenticated()
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/403")
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
