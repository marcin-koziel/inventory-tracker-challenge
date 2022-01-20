package ca.shopify.inventorytrackerchallenge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

/**
 *
 * @author Marcin Koziel
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;

    /**
     * Encoding string values, in this case passwords.
     *
     * @return a hashed password
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Encoding string values, in this case passwords.
     */
    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;
    /**
     * Stating the source of database.
     */
    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/", "/**").permitAll()
                .and()
                .formLogin().loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .logout().invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        http.csrf().disable();
        http.headers().frameOptions().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .withUser("BestRecruiter").password(passwordEncoder.encode("Password!")).roles("USER", "ADMIN")
                .and()
                .withUser("Candidate").password(passwordEncoder.encode("ILoveTheBackend")).roles("USER");
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
        JdbcUserDetailsManager jdbcUserDetailsManager
                = new JdbcUserDetailsManager();

        jdbcUserDetailsManager.setDataSource(dataSource);

        return jdbcUserDetailsManager;
    }

}
