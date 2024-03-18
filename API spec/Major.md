##### BASE URL: http://localhost:8080/api/v1/majors

## Get All Major

Method : GET

Response Body (2xx)

```json
{
  "data": [{
    "name": "simple major name",
    "id": "generated-by-UUID"
  }],
  "message" : "Success get all majors"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Create Major

Method : POST

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")

Request Body

```json
{
    "name": "new major"
}
```

Response Body (2xx)

```json
{
    "data": "OK",
    "message": "success create new major"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```


## Update Major

Method : PATCH

URL : /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")

Request Body

```json
{
    "name": "update new major"
}
```

Response Body (2xx)

```json
{
    "data": "OK",
    "message": "success update new major"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Delete Major

Method : DELETE

URL : /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")


Response Body (2xx)

```json
{
    "data": "OK",
    "message": "success delete major"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```