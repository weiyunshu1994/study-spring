package com.example.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "测试SwaggerAPI Annotation", tags = "Swagger测试之用户信息管理API")
@RestController
@RequestMapping("/user")
public class SwaggerController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping(value = "/swaggerGet/{name}")
    @ApiOperation(value = "接口方法说明", notes = "接口的详情描述")
    @ApiImplicitParam(name = "name", value = "请传递一个用户名参数",required = true, dataType = "String", paramType = "path")
    public String swaggerGet(@PathVariable String name) {
        return "name="+name;
    }

    @GetMapping(value = "/login")
    @ApiOperation(value = "登录")
    @ApiImplicitParam(name = "redirect_url",value = "发起微信登录的前端URL", required = true,paramType = "query")
    public String login(@RequestParam("redirect_url") String redirect_url){
        return "String";
    }


}
