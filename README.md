# stock-api
Stock api to monitor the financial profit loss

- Warning:
  - The database is not a persistent database (currently H2 database)


 - In order to access this api, use below end-point to signup
   - This endpoint will store the user info in H2 DB

##### End-point to `singup`
```
## POST
 
/stock-app/signup
```
 - INPUT: Include username and password in the request body

```json
 {
  "userName":"username",
  "password":"password"
 }
``` 

- OUTPUT: Response

```json
{
    "statusMessage": "Welcome <<username>> !!!, Successfully singed up!!"
}
```

##### Create jwt token to access other end-points

  - Once `signed up` with the user name
  - Hit the below end point, to generate the jwt token

```
## POST 
/stock-app/token
```
 - INPUT: username and password info in the body of POST request

```json 
 {
  "userName":"username",
  "password":"password"
 }
```

 - OUTPUT: jwt token in Json repsonse
 
```json 
{
  "jwtToken": "yyyyyyyy.xxxxxxxxxxxxxxxx.zzzzzzzzzz"
}
```

##### Adding single stock info

- In order to add stocks to the database use below end-point
  - use the token in the POST request HTTP header like below
   ```
   Authorization : Bearer xxxxx.yyyy.zzzz
   Content-Type : "application/json"
   
  ```
    

```
## POST
/stock/v1/add
````
 - INPUT: json input to be sent in the body of the POST request
   - use the same structure

```json
{
    "symbol": "GAIN",
    "avgStockPrice": 10.5,
    "stockCount": 10
}
```

 - Output: json repsone after added the stock info to DB
 
```json 
{
    "status": "Successfully added stock",
    "stockInfo": [
        {
            "symbol": "GAIN1",
            "avgStockPrice": 10.5,
            "stockCount": 10.0
        }
    ]
}
```

##### In order to add list of stock using json Array
   - pass the in JWT token as a Request header as below 
   ```
   Authorization : Bearer xxxxx.yyyy.zzzz
   Content-Type : "application/json"
   ```
   
```
## POST request
/stock/v1/add/stocks
```

 - INPUT: Pass the below json structure in the body of POST Http request.
  - Use the generated JWT token to access the end-point, like below
   ```
   Authorization : Bearer xxxxx.yyyy.zzzz
   Content-Type : "application/json"
  ```

```json
[{
		"symbol": "MSFT",
		"stockCount": "10.0",
		"avgStockPrice": "200.50"
	},
	{
		"symbol": "INTC",
		"stockCount": "5.0",
		"avgStockPrice": "50.00"
}]
```

  - OUTPUT : Response from the API
  
```json
{
    "status": "Successfully added stocks",
    "stockInfo": [{
		"symbol": "MSFT",
		"stockCount": "10.0",
		"avgStockPrice": "200.50"
	 },
	 {
		"symbol": "INTC",
		"stockCount": "5.0",
		"avgStockPrice": "50.00"
     }]
}
```
##### To get the stock details with computed metrics use below end-point
  - The list of stock info stored under the specific user will be computed and displayed in the response
  - Pass the JWT token part of the POST request header.
   ```
   Authorization : Bearer xxxxx.yyyy.zzzz
   Content-Type : "application/json"  
   ```
  
  
```
## POST Endpoint with header Authorization token
/stock/v1/stock-info
```
   
 - OUTPUT : Below is the sample output rendered
 
```json
{
    "stockInfo": [
        {
            "symbol": "MSFT",
            "currentPrice": 336.06,
            "companyName": "Microsoft Corporation",
            "lastAccessed": "2021-11-07T17:38:31.7269808",
            "currentInvestedAmount": 111.1111,
            "actualInvestedAmount": 222.2222,
            "difference": 111.1111,
            "profitOrLoss": "** Profit **"
        },
        {
            "symbol": "INTC",
            "currentPrice": 50.92,
            "companyName": "Intel Corporation",
            "lastAccessed": "2021-11-07T17:38:31.8349824",
            "currentInvestedAmount": 111.111,
            "actualInvestedAmount": 222.2222,
            "difference": -11.1111,
            "profitOrLoss": "** Loss **"
        }
    ],
    "investedAmount": 1000.500,
    "currentMarketTotalAmount": 5000.000,
    "difference": 4000.500,
    "profitLossStatus": "**PROFIT**",
    "lastAccessed": "2021-11-07T17:38:31.8589826",
    "simpleStatus": "Successfully computed."
}
```

#####  Swagger open api v3 - access

 - Once the user has signed up with the username and password.
 - Access end-point with URL - `http://<domain>/swagger-ui/index.html`
 - Once Swagger UI is rendered, input `/stockapp-openapi`
 - To explore the API end-points, use username & password used to sign-up for API to access.
 
