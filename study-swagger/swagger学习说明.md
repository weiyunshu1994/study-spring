[【Swagger】Springboot中集成Swagger2.0_qgnczmnmn的博客-CSDN博客_swagger2.0](https://blog.csdn.net/qgnczmnmn/article/details/106096425?utm_medium=distribute.pc_relevant_t0.none-task-blog-2~default~BlogCommendFromMachineLearnPai2~default-1.base&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2~default~BlogCommendFromMachineLearnPai2~default-1.base)

[SpringBoot项目中Swagger的配置和使用 - 码农小匠 - 博客园 (cnblogs.com)](https://www.cnblogs.com/giswhw/p/14026283.html)

1. 添加swagger依赖

- swagger不同的版本界面效果不同。目前使用2.9.2版本，使用3.0.0版本则出现问题

<center class="half">
    <img src="https://weiyunshu1994-picgo-img.oss-cn-shenzhen.aliyuncs.com/Typora_image-20210626162219664.png" width="300"/>
    <img src="https://weiyunshu1994-picgo-img.oss-cn-shenzhen.aliyuncs.com/Typora_image-20210626161615234.png" width="300"/>
</center>

2. swagger配置文件

**新建config/SwaggerConfig.java**

```java
package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.config.basePackage}")
    private String basePackage;

    @Value("${swagger.config.title}")
    private String title;

    @Value("${swagger.config.description}")
    private String description;

    @Value("${swagger.config.version}")
    private String version;

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2) // 指定API类型为swagger2
                .apiInfo(apiInfo())             // 用于定义API文档的相关信息
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage)) // 指定controller层包名
                .paths(PathSelectors.any())  // 应用于所有的controller
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title(title)   // API文档的标题
                .description(description) // 关于这个文档作用的描述
                .version(version)  // 设置当前API文档的版本号
                .build();
    }
}
```

**在application.yml中添加相关参数的配置**

```yaml
swagger:
  config:
    basePackage: com.example.demo.controller
    title: 设备管理API接口文档
    description: 提供设备管理相关接口描述
    version: 1.0
```

3. swagger2注解使用

   3.1 swagger2 注解整体说明

   1. @Api：用在请求的类上，说明该类的作用

   ```
   @Api：用在请求的类上，表示对类的说明
       tags="说明该类的作用，可以在UI界面上看到的注解"
       value="该参数没什么意义，在UI界面上也看到，所以不需要配置"
   ```

   示例：

   ```java
   @Api(tags="APP用户注册Controller")
   ```

   2. @ApiOperation：用在请求的方法上，说明方法的作用

   ```java
   @ApiOperation：用在请求的方法上，说明方法的用途、作用
       value="说明方法的用途、作用"
       notes="方法的备注说明"
   // 在notes中进行换行的方法：
   		1、springfox-swagger2 中使用：空格+空格+\n
   		2、swagger-springmvc 中使用：<br/>
   		如： @ApiOperation(value = "this api",notes = "参数name有三个选择：  \n 1...   \n2...  \n3...")
   	  其他参数：
         tags                操作标签，非空时将覆盖value的值
         response            响应类型（即返回对象）
         responseContainer   声明包装的响应容器（返回对象类型）。有效值为 "List", "Set" or "Map"。
         responseReference  指定对响应类型的引用。将覆盖任何指定的response（）类
         httpMethod        指定HTTP方法，"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS" and "PATCH"
         position            如果配置多个Api 想改变显示的顺序位置，在1.5版本后不再支持
         nickname         第三方工具唯一标识，默认为空
         produces            设置MIME类型列表（output），例："application/json, application/xml"，默认为空
         consumes            设置MIME类型列表（input），例："application/json, application/xml"，默认为空
         protocols           设置特定协议，例：http， https， ws， wss。
         authorizations      获取授权列表（安全声明），如果未设置，则返回一个空的授权值。
         hidden              默认为false， 配置为true 将在文档中隐藏
         responseHeaders       响应头列表
         code            响应的HTTP状态代码。默认 200
         extensions       扩展属性列表数组
   ```

   示例：

   ```
   @ApiOperation(value="用户注册",notes="手机号、密码都是必输项，年龄随边填，但必须是数字")
   ```

   3. @ApiImplicitParams：用在请求的方法上，包含一组参数说明

      ```
      @ApiImplicitParams：用在请求的方法上，表示一组参数说明
          @ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
              name：参数名
              value：参数的汉字说明、解释
              required：参数是否必须传
              paramType：参数放在哪个地方
                  · header --> 请求参数的获取：@RequestHeader
                  · query --> 请求参数的获取：@RequestParam
                  · path（用于restful接口）--> 请求参数的获取：@PathVariable
                  · body（不常用）
                  · form（不常用）    
              dataType：参数类型，默认String
              // 注意:如果dataType设置为Integer，在文档中会显示undefined，如果设置为Int，就可以显示Integer  
              // 如果输入的为数组类型，则在dataType中设置数组中元素类型，如：String，然后设置allowMultiple=true即可，此时文档中dataType列下显示的类型为Array[string]
              defaultValue：参数的默认值
              example: 非请求体(body)参数的单个请求示例，注意是非body类型的参数，body类型设置无效
              examples:设置body参数请求的示例
              // examples=@Example((@ExampleProperty(mediaType="",value="")))
              allowableValues：设置参数允许的值，例如：allowableValues="a, b, c"
      ```

      示例：

      ```
      @ApiImplicitParams({
          @ApiImplicitParam(name="mobile",value="手机号",required=true,paramType="form"),
          @ApiImplicitParam(name="password",value="密码",required=true,paramType="form"),
          @ApiImplicitParam(name="age",value="年龄",required=true,paramType="form",dataType="Integer")
      })
      ```

   4. @ApiResponses：用在请求的方法上，表示一组响应

   ```
   @ApiResponses：用在请求的方法上，表示一组响应
       @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
           code：数字，例如400
           message：信息，例如"请求参数没填好"
           response：抛出异常的类
   ```

   示例

   ```
   @ApiOperation(value = "select1请求",notes = "多个参数，多种的查询参数类型")
   @ApiResponses({
       @ApiResponse(code=400,message="请求参数没填好"),
       @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
   })
   ```

   5. @ApiModel：用于响应类上，表示一个返回响应数据的信息

   ```
   @ApiModel：用于响应类上，表示一个返回响应数据的信息
               （这种一般用在post创建的时候，使用@RequestBody这样的场景，
               请求参数无法使用@ApiImplicitParam注解进行描述的时候）
       @ApiModelProperty：用在属性上，描述响应类的属性
   ```

   示例：

   ```
   import io.swagger.annotations.ApiModel;
   import io.swagger.annotations.ApiModelProperty;
   
   import java.io.Serializable;
   
   @ApiModel(description= "返回响应数据")
   public class RestMessage implements Serializable{
   
       @ApiModelProperty(value = "是否成功")
       private boolean success=true;
       @ApiModelProperty(value = "返回对象")
       private Object data;
       @ApiModelProperty(value = "错误编号")
       private Integer errCode;
       @ApiModelProperty(value = "错误信息")
       private String message;
   
       /* getter/setter */
   }
   ```



注意：

- 举例说明：如果使用 @ApiImplicitParam(name = “department”, value = “部门信息”, dataType =“Department”, paramType = “body”, required = true)在API的参数注解中，那么在Department的实体中就不要使用@ApiModel(value = “Department对象”, description = “”)，否则在文档中对应的API参数部分就不能显示Department的实体类。也就是上变说的@ApiModel请求参数无法使用@ApiImplicitParam注解进行描述的时候）

- 我们虽然在model层的实体类上添加了相应的注解，但是并没有显示，出现这个问题的原因是因为虽然在实体类上进行了注解，但是必须在controller层中使用@requestBody注解或者返回的数据类型中包含注解的实体类才可以显示，由于我写的API中没有满足这样的条件，所以没有显示。