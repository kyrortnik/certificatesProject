package com.epam.esm.servlet;

import com.epam.esm.configs.RootConfig;
import com.epam.esm.configs.WebConfig;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  /*  @Override
    public void onStartup(ServletContext servletContext){

        servletContext.setInitParameter(
                "spring.profiles.active", "prod");
    }
*/
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}