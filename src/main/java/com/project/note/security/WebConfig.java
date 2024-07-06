package com.project.note.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;
import java.util.Properties;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AccountStatusInterceptor accountStatusInterceptor;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css1/**")
                .addResourceLocations("classpath:/static/css1/");
        registry.addResourceHandler("/css2/**")
                .addResourceLocations("classpath:/static/css2/");
        registry.addResourceHandler("/cssWhatIsNoteMySelf/**")
                .addResourceLocations("classpath:/static/cssWhatIsNoteMySelf/");
        registry.addResourceHandler("/fonts1/**")
                .addResourceLocations("classpath:/static/fonts1/");
        registry.addResourceHandler("/fonts2/**")
                .addResourceLocations("classpath:/static/fonts2/");
        registry.addResourceHandler("/home/**")
                .addResourceLocations("classpath:/static/home1/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/images2/**")
                .addResourceLocations("classpath:/static/images2/");
        registry.addResourceHandler("/js1/**")
                .addResourceLocations("classpath:/static/js1/");
        registry.addResourceHandler("/js2/**")
                .addResourceLocations("classpath:/static/js2/");
        registry.addResourceHandler("/vendor2/**")
                .addResourceLocations("classpath:/static/vendor2/");
        registry.addResourceHandler("/input.css")
                .addResourceLocations("classpath:/static/input.css");
        registry.addResourceHandler("/output.css")
                .addResourceLocations("classpath:/static/output.css");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties errorMappings = new Properties();
        errorMappings.setProperty("org.springframework.web.servlet.NoHandlerFoundException", "forward:/home");
        resolver.setExceptionMappings(errorMappings);
        resolver.setDefaultErrorView("forward:/home");
        resolvers.add(resolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accountStatusInterceptor).addPathPatterns("/**");
    }
}