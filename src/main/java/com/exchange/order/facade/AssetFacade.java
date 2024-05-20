package com.exchange.order.facade;

import com.exchange.order.config.feign.FeignClientConfiguration;
import com.exchange.order.domain.Asset;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "asset-service", configuration = FeignClientConfiguration.class)
public interface AssetFacade {

    // we use this API endpoint, cause it randomly throws 500, which is helpful for retry testing with feign
    @GetMapping("/asset/price2/{symbol}")
    Asset getAsset(@PathVariable String symbol);
}