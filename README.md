# Asset-service

### How to use
Below is example how you can call 2 API endpoints here
```shell
# add new asset
curl -H 'content-type: application/json' -d '{"symbol":"BTC","price":100}' http://localhost:8081/asset/price
# get asset price
curl -H 'content-type: application/json' http://localhost:8081/asset/price/BTC
```

### I18n support
We have automatic support for multiple language, just call
```shell
# get error in Spanish language
curl -H 'content-type: application/json' -H 'Accept-Language: es' http://localhost:8081/asset/price/BTC
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
c.e.a.service.impl.ConfigPrinterService  : configEnv=dev
c.e.a.service.impl.ConfigPrinterService  : configEnv=dev

# changing config value to "test"
Receive server push request, request = ConfigChangeNotifyRequest
app.config.print=true, type=text
Started application in 0.915 seconds (process running for 62.546)
Refresh keys changed: [app.config.env]

# new value is displayed
c.e.a.service.impl.ConfigPrinterService  : configEnv=test
c.e.a.service.impl.ConfigPrinterService  : configEnv=test
```

### Returning traceId to customer
If you make a call to get price for an asset that doesn't have a price, then `AppException` is thrown, which is caught by exception handler and user-friendly error returned with traceId
```shell
# call api endpoint for asset that doesn't have a price
curl -H 'content-type: application/json' -H 'Accept-Language: abc' http://localhost:8081/asset/price/ABC
{"code":10101,"errorCode":"price_not_found","msg":"Failed to fetch the price for ABC","traceId":"6647118032e03d55c7fa5d9a29442502"}
```
Now you can check the logs for this traceId, as you can see, now you can identify and link exact user to exact error in your logs.
```
2024-05-17 12:12:48.186 [asset-service] [http-nio-8081-exec-2] [6647118032e03d55c7fa5d9a29442502,c7fa5d9a29442502]  INFO  com.exchange.asset.service.impl.PriceServiceImpl - Fetching price for: symbol=ABC
2024-05-17 12:12:48.204 [asset-service] [http-nio-8081-exec-2] [6647118032e03d55c7fa5d9a29442502,c7fa5d9a29442502]  ERROR c.e.a.config.RestResponseEntityExceptionHandler - catch AppException: url=/asset/price/ABC
com.exchange.asset.exception.AppException: price_not_found
	at com.exchange.asset.service.impl.PriceServiceImpl.getPrice(PriceServiceImpl.java:34)
	at com.exchange.asset.controllers.PriceController.getPrice(PriceController.java:24)
```