# Order-service

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

{"code":101001,"errorCode":"order_not_found","msg":"No se pudo recuperar el pedido de BTC","traceId":"6648c3d780451313f7b2a1fb189be993"}
```
No additional configuration to support it required, it works out-of-the-box, this header `Accept-Language` would
automatically put correct language into `LocaleContextHolder`.

### Nacos config
Make sure your nacos server is running and you pass it's IP into config variables. If you try to run
this app without nacos config then it won't start.
```
NACOS_SERVER_IP=127.0.0.1;
NACOS_SERVER_ID=31b66f4e-dbf4-4745-a359-2d9701f436e5;
```

### Nacos config hot reload
With nacos config management you can hot-reload your env variables without the need to restart the app.
To demonstrate this you can take a look into `ConfigPrinterService` where each 10 sec env var is printed, yet if we
change config while the app is running, the app will pick-up the change and display correct value. Belos is an example.
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
```
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