package cn.guet.airbnb.controller;

import cn.guet.airbnb.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Result<Map<String, String>> health() {
        return Result.success(Map.of("status", "UP"));
    }
}
