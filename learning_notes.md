### Learnings during the deployment.

##### In order to make use of in-Memory H2  for testing then use application.properties under the test/java/resources
      - Also make a note that the H2 dependencies should be scope testing

##### Developing integration test cases with Feign Client
   - Faced error since didn't include the feign-okhttp client dependency jar.
    
   - After implementing the call to Actual hosted API end-point, token repsonse was not able to create 
      - Got below exception message, the reason for this was 
      - AuthenticationResponse, class didn't include the @NoArgsConstructor
      - Even including that there was a warning message, since all the properties where declared as final
      - Lomobk is not able it initialize the final method. After removing the final from each property the issue resolved. 
      
```
feign.FeignException: Cannot construct instance of `com.stock.finance.model.api.AuthenticationResponse` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (BufferedReader); line: 1, column: 2] reading POST https://my-stock-boot-app.herokuapp.com/stock-app/token
	at feign.FeignException.errorReading(FeignException.java:167)
	at feign.AsyncResponseHandler.handleResponse(AsyncResponseHandler.java:102)
	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:138)
	at feign.SynchronousMethodHandler.invoke(SynchronousMethodHandler.java:89)
	at feign.ReflectiveFeign$FeignInvocationHandler.invoke(ReflectiveFeign.java:100)
	at com.sun.proxy.$Proxy163.getToken(Unknown Source)
....
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.stock.finance.model.api.AuthenticationResponse` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (BufferedReader); line: 1, column: 2]
	at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:67)
	at com.fasterxml.jackson.databind.DeserializationContext.reportBadDefinition(DeserializationContext.java:1764)
	at com.fasterxml.jackson.databind.DatabindContext.reportBadDefinition(DatabindContext.java:400)
	at com.fasterxml.jackson.databind.DeserializationContext.handleMissingInstantiator(DeserializationContext.java:1209)
	at com.fasterxml.jackson.databind.deser.BeanDeserializerBase.deserializeFromObjectUsingNonDefault(BeanDeserializerBase.java:1415)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserializeFromObject(BeanDeserializer.java:362)
	at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:195)
	at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:322)
	at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4593)
	at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3577)
	at feign.jackson.JacksonDecoder.decode(JacksonDecoder.java:63)
	at feign.AsyncResponseHandler.decode(AsyncResponseHandler.java:115)
	at feign.AsyncResponseHandler.handleResponse(AsyncResponseHandler.java:87)
	... 72 more
```
##### Feign RetryableException issue 
   - Below exception occurs, since the herokuapp free tier shuts down if there are no request for 30 minutes.
   - Usually, any request from browser, etc. will start the application deployment, in below case it take some time to startup.
   - The test case fails 
   
```
feign.RetryableException: my-stock-boot-app.herokuapp.com executing POST https://my-stock-boot-app.herokuapp.com/stock-app/apikey
	at feign.FeignException.errorExecuting(FeignException.java:268)
	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:129)
	at feign.SynchronousMethodHandler.invoke(SynchronousMethodHandler.java:89)
	at feign.ReflectiveFeign$FeignInvocationHandler.invoke(ReflectiveFeign.java:100)
	at com.sun.proxy.$Proxy163.getAPIKey(Unknown Source)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:688)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
...
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
Caused by: java.net.UnknownHostException: my-stock-boot-app.herokuapp.com
	at java.base/sun.nio.ch.NioSocketImpl.connect(NioSocketImpl.java:567)
	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:333)
	at java.base/java.net.Socket.connect(Socket.java:648)
	at java.base/sun.security.ssl.SSLSocketImpl.connect(SSLSocketImpl.java:290)
	at java.base/sun.net.NetworkClient.doConnect(NetworkClient.java:177)
	at java.base/sun.net.www.http.HttpClient.openServer(HttpClient.java:474)
	at java.base/sun.net.www.http.HttpClient.openServer(HttpClient.java:569)
	at java.base/sun.net.www.protocol.https.HttpsClient.<init>(HttpsClient.java:265)
	at java.base/sun.net.www.protocol.https.HttpsClient.New(HttpsClient.java:372)
	at java.base/sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection.getNewHttpClient(AbstractDelegateHttpsURLConnection.java:189)
	at java.base/sun.net.www.protocol.http.HttpURLConnection.plainConnect0(HttpURLConnection.java:1194)
	at java.base/sun.net.www.protocol.http.HttpURLConnection.plainConnect(HttpURLConnection.java:1082)
	at java.base/sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection.connect(AbstractDelegateHttpsURLConnection.java:175)
	at java.base/sun.net.www.protocol.http.HttpURLConnection.getOutputStream0(HttpURLConnection.java:1375)
	at java.base/sun.net.www.protocol.http.HttpURLConnection.getOutputStream(HttpURLConnection.java:1350)
	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:119)
	... 71 more
```

##### Enhancement idea:
  - Include Log4j2 MDC, for better handling the request per user.