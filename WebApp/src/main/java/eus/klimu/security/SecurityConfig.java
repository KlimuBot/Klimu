package eus.klimu.security;

import eus.klimu.security.filter.AuthenticationFilter;
import eus.klimu.security.filter.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Take care of fetching the users from the database and generates a Spring Security user with
     * SimpleGrantedAuthorities based on a user on the database, and it's roles. The implementation
     * of this class can be located at <b><u>UserServiceImp</u></b>.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Password Encoder for the users, it uses a hashing system to transform the password into a hash
     * to avoid saving the password as plain text. BCrypt uses a random salt by default, and it is stored
     * together with the hash. This allows us to encode the password with both a hash and salt.
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManagerBean());
        authenticationFilter.setFilterProcessesUrl("/login");

        // Disable default configuration.
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ACL list.
        http.authorizeRequests()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/media/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/user/create").permitAll();
        http.authorizeRequests()
                .antMatchers("/**").hasAnyAuthority("USER_ROLE", "ADMIN_ROLE")
                .antMatchers("/email/**").hasAnyAuthority("USER_ROLE", "ADMIN_ROLE")
                .antMatchers("/role/**").hasAuthority("ADMIN_ROLE");
        http.authorizeRequests()
                .anyRequest().authenticated();

        // Add filters.
        http.addFilter(authenticationFilter);
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
