# stock-api
Stock api to monitor the financial profit loss

- Warning:
  - The database is not a persistent database (currently H2 database)
  - 

 - In order to access this api, use below end-point to signup
   - This endpoint will store the user info in H2 DB
   
```
## POST
 
/stock-app/signup

## include the username and password in the body
 {
  "userName":"username",
  "password":"password"
 }
 
## output 
{
    "statusMessage": "Welcome username !!!, Successfully singed up!!"
}
```

- Once `signed up` with the user name
- Hit the below end point, to get the jwt token

```
## POST 
/stock-app/token

## include the username and password in the body
 {
  "userName":"username",
  "password":"password"
 }

## output will be a json as below
{
  "jwtToken": "yyyyyyyy.xxxxxxxxxxxxxxxx.zzzzzzzzzz"
}
```

- In order to add stocks to the database use below end-point
  - use the generated jwt token as `Authorization : Bearer ` header

```
## POST
/stock/v1/add

## with below header
Authorization : Bearer yyyyy.xxxx.zzzzz

## Input json mapper
{
    "symbol": "GAIN",
    "avgStockPrice": 10.5,
    "stockCount": 10
}

## Output json mapper
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
- In order to add list of stock using list

```
## POST request
/stock/v1/add/stocks

## use headers
Authorization : Bearer xxxxx.yyyy.zzzz
Content-Type : "application/json"

## input json 

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

## output
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
- Stored Stock info can be used to compute the metrics like Profit/Loss against symbol stored in DB

```
## POST Endpoint with header Authorization token
/stock/v1/stock-info
```
 
 - Below is the sample output rendered
 
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

### Swagger open api v3 - access
 - Once the user has signed up with the username and password.
 - when accessing URL end-point `http://<domain>:8080/swagger-ui/index.html`
 - once Swagger UI is display, input `/stockapp-openapi`
 - when prompt for username & password, enter the value used to sign up.
 
