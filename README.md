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
    "statusMessage": "Welcome thiru123 !!!, Successfully singed up!!"
}
```

- Once `signed up` with the user name
- Hit the below end point, to get the jwt token

```
## POST 
/stock-app/authenticate

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
    "symbol": "GAIN1",
    "avgStockPrice": 10.5,
    "stockCount": 10
}

## Output json mapper
{
    "status": "Successfully added stock",
    "stockInfo": [
        {
            "id": 2,
            "symbol": "GAIN1",
            "avgStockPrice": 10.5,
            "stockCount": 10,
            "userId": 1,
            "active": true
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
    "id": 3,
    "symbol": "GAIN5",
    "average_price": 10.5,
    "stock_count": 10,
    "user_id": 1
},
{
    "id": 4,
    "symbol": "AMZN",
    "average_price": 10.5,
    "stock_count": 10,
    "user_id": 1
}]

## output
{
    "status": "Successfully added stocks",
    "stockInfo": [
        {
            "id": 1,
            "symbol": "GAIN5",
            "avgStockPrice": 0,
            "stockCount": 0,
            "userId": 0,
            "active": true
        },
        {
            "id": 2,
            "symbol": "AMZN",
            "avgStockPrice": 0,
            "stockCount": 0,
            "userId": 0,
            "active": true
        }
    ]
}
```