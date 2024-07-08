package it.unipi.healthhub.config;

import it.unipi.healthhub.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> logingFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthFilter());

        registrationBean.addUrlPatterns("/doctor/*");
        registrationBean.addUrlPatterns("/dashboard/*");
        //registrationBean.setOrder(2);

        return registrationBean;
    }

}
