# Order service

### Content
* [How to use](#how-to-use)
* [I18n support](#i18n-support)
* [Nacos env vars](#nacos-env-vars)
* [Nacos config hot reload](#nacos-config-hot-reload)
* [Returning traceId to customer](#returning-traceid-to-customer)
* [Feign retry example](#feign-retry-example)
* [Links](#links)

### How to use
Below is example how you can call 2 API endpoints here
```shell
# create new order
curl -H 'content-type: application/json' -d '{"symbol":"BTC","quantity":5}' http://localhost:8082/order
# get order by symbol
curl -H 'content-type: application/json' http://localhost:8082/order/BTC
```

### I18n support
We have automatic support for multiple language, just call
```shell
# get error in Spanish language
curl -H 'content-type: application/json' -H 'Accept-Language: es' http://localhost:8082/order/BTC
# because order doesn't exist, order-service will return error in Spanish
{"code":101001,"errorCode":"order_not_found","msg":"No se pudo recuperar el pedido de BTC","traceId":"6648c3d780451313f7b2a1fb189be993"}
```
No additional configuration to support it required, it works out-of-the-box, this header `Accept-Language` would
automatically put correct language into `LocaleContextHolder`.
We have even added `FeignRequestInterceptor` to forward language headers, and not we have intra-service i18n support, because your headers forwarded to other services.
```shell
# create order
curl -H 'content-type: application/json' -d '{"symbol":"BTC","quantity":5}' http://localhost:8082/order
# get order by symbol 
curl -H 'content-type: application/json' -H 'Accept-Language: es' http://localhost:8082/order/BTC
# because there is no price, asset-service will return error in Spanish
{"code":100001,"errorCode":"price_not_found","msg":"No se pudo obtener el precio de BTC","traceId":"664c4c5ec5ccd48d0962891a035a3df6"}
```

### Nacos env vars
Make sure your nacos server is running and you pass it's IP into config variables. If you try to run
this app without nacos config then it won't start.
```
NACOS_SERVER_IP=127.0.0.1;
NACOS_SERVER_ID=31b66f4e-dbf4-4745-a359-2d9701f436e5;
```

### Nacos config hot reload
With nacos config management you can hot-reload your env variables without the need to restart the app.
To demonstrate this you can take a look into `ConfigPrinterService` where each 10 sec env var is printed, yet if we
change config while the app is running, the app will pickup the change and display correct value. Belos is an example.
```
# logs from running app (old config data)
2024-05-18 19:06:19.339 [order-service] [Thread-8] [,]  INFO  c.exchange.order.service.impl.ConfigPrinterService - configEnv=test
2024-05-18 19:06:29.343 [order-service] [Thread-8] [,]  INFO  c.exchange.order.service.impl.ConfigPrinterService - configEnv=test

# changing config value to "test"
2024-05-18 19:08:02.288 [order-service] [nacos-grpc-client-executor-10.15.3.40-72] [,]  INFO  com.alibaba.nacos.common.remote.client - [b7bae77d-ce73-4a1d-a86a-eb339926cc2d_config-0] Receive server push request, request = ConfigChangeNotifyRequest, requestId = 9

# new value is displayed
2024-05-18 19:08:09.404 [order-service] [Thread-8] [,]  INFO  c.exchange.order.service.impl.ConfigPrinterService - configEnv=test
```

### Returning traceId to customer
Below I wll show 2 log traces for same traceId, positive & negative use case
Positive use-case
```shell
# create price
curl -H 'content-type: application/json' -d '{"symbol":"BTC","price":100}' http://localhost:8081/asset/price
# create order
curl -H 'content-type: application/json' -d '{"symbol":"BTC","quantity":5}' http://localhost:8082/order
# fetch order
curl -H 'content-type: application/json' http://localhost:8082/order/BTC

{"symbol":"BTC","quantity":5.0,"price":100.0,"amount":500.0}

# order-service logs
2024-05-18 19:11:52.123 [order-service] [http-nio-8082-exec-6] [6648c5388dafecc4175d55639785a706,175d55639785a706]  INFO  com.exchange.order.service.impl.OrderServiceImpl - Fetching order: symbol=BTC
2024-05-18 19:11:52.138 [order-service] [http-nio-8082-exec-6] [6648c5388dafecc4175d55639785a706,175d55639785a706]  INFO  com.exchange.order.service.impl.OrderServiceImpl - Fetched order: order=ConvertOrder(symbol=BTC, quantity=5.0, price=100.0, amount=500.0)
# asset-service logs
2024-05-18 19:11:52.134 [asset-service] [http-nio-8081-exec-5] [6648c5388dafecc4175d55639785a706,9f7a8e1805b24024]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=BTC
2024-05-18 19:11:52.134 [asset-service] [http-nio-8081-exec-5] [6648c5388dafecc4175d55639785a706,9f7a8e1805b24024]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetched price for: symbol=BTC, price=100.0
```
Now let's consider negative case, where we try to fetch order that doesn't have a price
```
# create order (but this time with ABC ticker)
curl -H 'content-type: application/json' -d '{"symbol":"ABC","quantity":5}' http://localhost:8082/order
# fetch order
curl -H 'content-type: application/json' http://localhost:8082/order/ABC
{"code":100001,"errorCode":"price_not_found","msg":"Failed to fetch the price for ABC","traceId":"6648c5c00b41a2ca146f40c1dde12e7e"}

# order-serivce logs
2024-05-18 19:14:08.468 [order-service] [http-nio-8082-exec-2] [6648c5c00b41a2ca146f40c1dde12e7e,146f40c1dde12e7e]  INFO  com.exchange.order.service.impl.OrderServiceImpl - Fetching order: symbol=ABC
2024-05-18 19:14:08.498 [order-service] [http-nio-8082-exec-2] [6648c5c00b41a2ca146f40c1dde12e7e,146f40c1dde12e7e]  WARN  c.exchange.order.config.feign.CustomErrorDecoder - Catch feign error: method=AssetFacade#getAsset(String), requestUrl=http://asset-service/asset/price/ABC, body={"code":100001,"errorCode":"price_not_found","msg":"Failed to fetch the price for ABC","traceId":"6648c5c00b41a2ca146f40c1dde12e7e"}
2024-05-18 19:14:08.503 [order-service] [http-nio-8082-exec-2] [6648c5c00b41a2ca146f40c1dde12e7e,146f40c1dde12e7e]  ERROR c.e.o.config.RestResponseEntityExceptionHandler - catch AppException: url=/order/ABC
com.exchange.order.exception.AppException: price_not_found
	at com.exchange.order.config.feign.FeignErrorDecoder.decode(CustomErrorDecoder.java:34)

# asset-service logs
2024-05-18 19:14:08.490 [asset-service] [http-nio-8081-exec-7] [6648c5c00b41a2ca146f40c1dde12e7e,474b0abdecb8458b]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=ABC
2024-05-18 19:14:08.495 [asset-service] [http-nio-8081-exec-7] [6648c5c00b41a2ca146f40c1dde12e7e,474b0abdecb8458b]  ERROR c.e.a.config.RestResponseEntityExceptionHandler - catch AppException: url=/asset/price/ABC
com.exchange.asset.exception.AppException: price_not_found
	at com.exchange.asset.service.impl.PriceServiceImpl.getPrice(PriceServiceImpl.java:34)
	at com.exchange.asset.controllers.PriceController.getPrice(PriceController.java:24)
```

### Feign retry example
If you look into our `FeignClientConfiguration` and `CustomErrorDecoder` you will notice that we have added `Retryer` for 5xx errors. That means if we get 5xx then we would retry for 5 times. We have this limitation of 5, because if we get 5xx error more than 5 times, that means something wrong with client server, and there is no point to try to call more.
You can observe the logs example. On asset-service side, it's implemented by randomly throwing `RuntimeException`.
```
# order-service
2024-05-21 12:37:20.409 [order-service] [http-nio-8082-exec-9] [664c5d40fdb8ab41390e70ff5e65eceb,390e70ff5e65eceb]  INFO  com.exchange.order.service.impl.OrderServiceImpl - Fetching order: symbol=BTC
2024-05-21 12:37:20.424 [order-service] [http-nio-8082-exec-9] [664c5d40fdb8ab41390e70ff5e65eceb,390e70ff5e65eceb]  WARN  com.exchange.order.config.feign.FeignErrorDecoder - Catch feign error: method=AssetFacade#getAsset(String), requestUrl=http://asset-service/asset/price2/BTC, requestBody=null, body={"code":100000,"errorCode":"server_error","msg":null,"traceId":"664c5d40fdb8ab41390e70ff5e65eceb"}
2024-05-21 12:37:20.440 [order-service] [http-nio-8082-exec-9] [664c5d40fdb8ab41390e70ff5e65eceb,390e70ff5e65eceb]  WARN  com.exchange.order.config.feign.FeignErrorDecoder - Catch feign error: method=AssetFacade#getAsset(String), requestUrl=http://asset-service/asset/price2/BTC, requestBody=null, body={"code":100000,"errorCode":"server_error","msg":null,"traceId":"664c5d40fdb8ab41390e70ff5e65eceb"}
2024-05-21 12:37:20.453 [order-service] [http-nio-8082-exec-9] [664c5d40fdb8ab41390e70ff5e65eceb,390e70ff5e65eceb]  INFO  com.exchange.order.service.impl.OrderServiceImpl - Fetched order: order=ConvertOrder(symbol=BTC, quantity=5.0, price=100.0, amount=500.0)

# asset-service
2024-05-21 12:37:20.416 [asset-service] [http-nio-8081-exec-3] [664c5d40fdb8ab41390e70ff5e65eceb,6f2c61126785b970]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=BTC
2024-05-21 12:37:20.419 [asset-service] [http-nio-8081-exec-3] [664c5d40fdb8ab41390e70ff5e65eceb,6f2c61126785b970]  ERROR c.e.a.config.RestResponseEntityExceptionHandler - Catch Exception: url=/asset/price2/BTC
java.lang.RuntimeException: server_error
	at com.exchange.asset.service.impl.PriceServiceImpl.throwRandomException(PriceServiceImpl.java:58)
	at com.exchange.asset.service.impl.PriceServiceImpl.getPrice2(PriceServiceImpl.java:49)
2024-05-21 12:37:20.416 [asset-service] [http-nio-8081-exec-3] [664c5d40fdb8ab41390e70ff5e65eceb,6f2c61126785b970]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=BTC
2024-05-21 12:37:20.419 [asset-service] [http-nio-8081-exec-3] [664c5d40fdb8ab41390e70ff5e65eceb,6f2c61126785b970]  ERROR c.e.a.config.RestResponseEntityExceptionHandler - Catch Exception: url=/asset/price2/BTC
java.lang.RuntimeException: server_error
	at com.exchange.asset.service.impl.PriceServiceImpl.throwRandomException(PriceServiceImpl.java:58)
	at com.exchange.asset.service.impl.PriceServiceImpl.getPrice2(PriceServiceImpl.java:49)
2024-05-21 12:37:20.451 [asset-service] [http-nio-8081-exec-5] [664c5d40fdb8ab41390e70ff5e65eceb,34caaebdcdbef644]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=BTC
2024-05-21 12:37:20.451 [asset-service] [http-nio-8081-exec-5] [664c5d40fdb8ab41390e70ff5e65eceb,34caaebdcdbef644]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetched price for: symbol=BTC, price=100.0
```

### Links
This project is a part of spring cloud microservices that includes following projects:
* [asset-service](https://github.com/dgaydukov/spring-cloud-asset-service) - provide asset price information
* [order-service](https://github.com/dgaydukov/spring-cloud-order-service) - calls asset service to get the price
* [external-gateway](https://github.com/dgaydukov/spring-cloud-external-gateway) - serve as gateway to forward requests from customers. You can call above 2 services from this gateway.
* [cloud project docs](https://github.com/dgaydukov/spring-cloud-project?tab=readme-ov-file) - special repository with all documentation for intra-service communication