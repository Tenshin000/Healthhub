package it.unipi.healthhub.config;

import it.unipi.healthhub.filter.DoctorDashboardAuthFilter;
import it.unipi.healthhub.filter.DoctorApiFilter;
import it.unipi.healthhub.filter.LoginFilter;
import it.unipi.healthhub.filter.PatientApiFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<DoctorDashboardAuthFilter> doctorDashboardAuthFilter() {
        FilterRegistrationBean<DoctorDashboardAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DoctorDashboardAuthFilter());
        registrationBean.addUrlPatterns("/doctors/dashboard/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PatientApiFilter> patientApiFilter() {
        FilterRegistrationBean<PatientApiFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PatientApiFilter());
        registrationBean.addUrlPatterns("/api/doctors/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<DoctorApiFilter> doctorApiFilter() {
        FilterRegistrationBean<DoctorApiFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DoctorApiFilter());
        registrationBean.addUrlPatterns("/api/doctor/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> alreadyLoggedFilter() {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter());
        registrationBean.addUrlPatterns("/login");
        return registrationBean;
    }

}
