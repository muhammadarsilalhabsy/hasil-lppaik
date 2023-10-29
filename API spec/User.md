##### BASE URL: http://localhost:8080/api/v1/users

## Get All User (ADMIN)

Method : GET

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")

- identity : `String`, user username or name, `using where query and like` (optional)
- major : `String`, major user, `using where query` (optional)
- page : `Integer` start from 0, default 0
- size : `Integer` default 10
- 
Response Body (2xx)

```json
{
  "data": [{
    "username": "1234567890",
    "email": "email@mail.com",
    "name": "simple name",
    "gender": "MALE",
    "major": "M199",
    "completed": false,
    "motto": null,
    "avatar": null,
    "roles": [
      "MAHASISWA",
      "ADMIN"
    ]
  }],
  "message" : "Success get all users"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```


## Update User (ADMIN)

Method : PATCH

URL: /:id

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")

Request Body

```json
{
  "email": "email@mail.com",
  "name": "simple name",
  "gender": "MALE",
  "completed": false,
  "roles": [
    "MAHASISWA",
    "ADMIN"
  ]
}
```

Response Body (2xx)

```json
{
  "data": "OK",
  "message" : "user with id {username} has been updated"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Update User detail

Method : PATCH

URL: /detail

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, All role)

Request Body

```json
{
  "name": "new simple name",
  "email": "newuser@email.com",
  "motto": "i'll be a champion"
}
```

Response Body (2xx)

```json
{
  "data": "OK",
  "message" : "data has been updated"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```


## Update Password

Method : PATCH

URL: /password

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, All role)

Request Body

```json
{
  "newPassword" : "secret",
  "confirmNewPassword" : "secret"
}
```

Response Body (2xx)

```json
{
  "data": "OK",
  "message" : "password has been updated"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```


## Update avatar user

Method : PATCH

URL: /avatar

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, All role)

form-data:

- avatar : `File [.png or .jpg]` (mandatory)

Response Body (2xx)
```json
{
  "data": "OK",
  "message" : "image avatar updated"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Get Certificate

Method : GET

URL: /certificate

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, All role)

Response Body (2xx)

```json
{
  "data": {
    "certificate": "rendom token using uuid",
    "image": "contains btye[]"
  }
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```

(Nanti di separate urlnya) -> api/v1/certificate

## Get Certificate (Without login)

Method : GET

URL: /certificate

Request Param:

- id : `String UUID` (mandatory)

Response Body (2xx)

```json
{
  "data": {
    "certificate": "rendom token using uuid",
    "user": {
      "username": "username with 8-10 characters",
      "name": "simple name",
      "email": "user@email.com",
      "avatar": "/image/user-avatar.png",
      "completed": true
    }
  },
  "message": "Success get certificate"
}
```

Response Body (4xx)

```json
{
  "message": "error message"
}
```