package cn.zpc.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Restful API 访问路径:
 * http://IP:port/{context-path}/swagger-ui.html
 * eg:http://localhost:8080/jd-config-web/swagger-ui.html
 * Author: sukai
 * Date: 2017-08-18
 */
@EnableWebMvc // 配置注解，自动在本类上下文加载一些环境变量信息
@EnableSwagger2 // 使swagger2生效
@ComponentScan(basePackages = {"cn.zpc.mvc.user"}) // 需要扫描的包路径
@Configuration
@Profile({"dev", "stg", "pro"})
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean
    public Docket createRestApi() {
        Parameter parameter = new ParameterBuilder().name(Global.AUTHORIZATION)
                .modelRef(new ModelRef("string"))
                .description("信息令牌")
                .parameterType("header")
                .required(false).build();
        List<Parameter> pars = new ArrayList<>();
        pars.add(parameter);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Demo API")
                .description("Created by Sukai")
                .version("1.0")
                .build();
    }
}