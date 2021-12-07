package ru.siv.notes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  /*
   * Конфигурирование правил безопасности доступа к страницам.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/registration", "/login").not().fullyAuthenticated() // Доступ только для не зарегистрированных пользователей
//        .antMatchers("/admin/**").hasRole("ADMIN") // Доступ только для пользователей с ролью Администратор
        .antMatchers("/").permitAll() // Доступ разрешен всем пользователей
        .anyRequest().authenticated() // Все остальные страницы требуют аутентификации
        .and().formLogin() //.loginPage("/login") // Настройка для входа в систему
        .defaultSuccessUrl("/") // Перенаправление на главную страницу после успешного входа
        .permitAll()
        .and().logout().permitAll().logoutSuccessUrl("/");
  }

  /*
   * Конфигурирование способа доступа к источнику пользователей и ролей, а также других правил безопасности.
   */
  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder())
        .usersByUsernameQuery("SELECT username, password, (status = 'ACTIVE') FROM users WHERE username=?")
        .authoritiesByUsernameQuery("SELECT u.username, r.name FROM users u, roles r WHERE u.username=? AND u.role_id=r.id ");
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JdbcUserDetailsManager jdbcUserDetailsManager() {
    return new JdbcUserDetailsManager(dataSource);
  }

}
