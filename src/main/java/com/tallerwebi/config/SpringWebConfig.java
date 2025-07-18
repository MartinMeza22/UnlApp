package com.tallerwebi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@EnableWebMvc
@Configuration
@ComponentScan("com.tallerwebi")
@EnableScheduling
public class SpringWebConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Spring + Thymeleaf need this
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/resources/core/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/core/js/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");

    }


    // Add the authentication interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**") // Aplica a todas las rutas
                .excludePathPatterns(   // Excepto a estas, que son públicas y no necesitan autenticación
                        "/",
                        "/login",
                        "/validar-login",
                        "/nuevo-usuario",
                        "/registrarme",
                        "/verificar-token",
                        "/registrarme/paso2",
                        "/css/**",
                        "/js/**",
                        "/webjars/**",
                        "/uploads/**"
                );
    }
    // Para poder usar acentos / letras raras
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);
    }

    // Para poder usar acentos / letras raras
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    // https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
    // Spring + Thymeleaf
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/thymeleaf/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    // Spring + Thymeleaf
    @Bean
    public SpringTemplateEngine templateEngine() {
        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(java8TimeDialect());
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    // Spring + Thymeleaf
    // Configure Thymeleaf View Resolver
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        scheduler.initialize();
        return scheduler;
    }
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        // Podés configurar el tamaño máximo de los archivos acá si querés
        // multipartResolver.setMaxUploadSize(5242880); // Ejemplo: 5MB
        return multipartResolver;
    }
    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

}
