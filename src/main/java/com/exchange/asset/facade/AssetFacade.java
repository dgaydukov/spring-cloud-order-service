package com.exchange.asset.facade;

import com.exchange.asset.domain.Asset;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "asset-service")
public interface AssetFacade {

    @GetMapping("/asset/price/{symbol}")
    Asset getAsset(@PathVariable String symbol);
}