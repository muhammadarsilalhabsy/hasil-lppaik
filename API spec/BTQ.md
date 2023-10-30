##### BASE URL: http://localhost:8080/api/v1/controls

## Get All control books (current User)

Method : GET

Request Header :

- X-API-TOKEN: "token-example" (current user token, Mandatory)

Response Body (2xx)

```json
{
  "data": [
    {
      "lesson": "simple lesson",
      "description": "simple description",
      "data": "2020-10-10",
      "tutor": "tutor name",
      "id": "generated-by-UUID"
    }
  ],
  "paging": {
    "page": 0,
    "pageSize": 2,
    "size": 10,
    "totalItems": 5 
  }, 
  "message": "Success get all majors"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Get All control books (with user id)

Method : GET

URL : /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "TUTOR || ADMIN || DOSEN")

Response Body (2xx)

```json
{
  "data": [
    {
      "lesson": "simple lesson",
      "description": "simple description",
      "data": "2020-10-10",
      "tutor": "tutor name",
      "id": "generated-by-UUID"
    }
  ],
  "paging": {
    "page": 0,
    "pageSize": 2,
    "size": 10,
    "totalItems": 5 
  }, 
  "message": "Success get all majors"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Create control books for user

Method : POST

URL: /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "TUTOR || ADMIN")

Request Body

```json
{
  "lesson": "simple lesson",
  "description": "simple description",
  "data": "2020-10-10"
}
```

Response Body (2xx)

```json
{
    "data": "OK",
    "message": "success create control book for {userId}"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```


## Update control book for user

Method : PATCH

URL : /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "TUTOR || ADMIN")

Request Body

```json
{
  "lesson": "simple lesson",
  "description": "simple description",
  "data": "2020-10-10"
}
```

Response Body (2xx)

```json
{
    "data": "OK",
    "message": "success update control book for {userId}"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Delete control book for user

Method : DELETE

URL : /{id}

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "TUTOR || ADMIN")

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