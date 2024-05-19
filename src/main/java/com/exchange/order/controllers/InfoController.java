package com.exchange.order.controllers;

import com.exchange.order.domain.VersionInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {
    @GetMapping("/version")
    public VersionInfo getVersion() {
        VersionInfo info = new VersionInfo();
        info.setName("order-service");
        info.setVersion("v1.0");
        return info;
    }

}
