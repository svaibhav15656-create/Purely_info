package com.purelyinfo.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception { return http.csrf(c->c.disable()).cors(Customizer.withDefaults()).headers(h->h.frameOptions(f->f.deny()).contentTypeOptions(Customizer.withDefaults()).referrerPolicy(r->r.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)).contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; object-src 'none'; frame-ancestors 'none'; base-uri 'self'"))).authorizeHttpRequests(a->a.requestMatchers("/api/admin/**","/admin.html").hasRole("ADMIN").requestMatchers("/","/index.html","/portal.css","/api/sample/**","/css/**","/js/**","/notice.js").permitAll().anyRequest().authenticated()).httpBasic(Customizer.withDefaults()).build(); }
}
