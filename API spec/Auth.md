##### BASE URL: http://localhost:8080/api/v1/auth

## Register

Method : POST

URL: /register

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, role must be "ADMIN")

Request Body:

```json
[
  {
    "username": "1234567890",
    "password": "secret",
    "email": "email@mail.com",
    "name": "simple name",
    "gender": "MALE",
    "major": "M199"
  },
  {
    "username": "...",
    "password": "...",
    "email": "...",
    "name": "...",
    "gender": "...",
    "major": "..."
  }
]
```

Response Body (2xx)

```json
{
  "data": "Ok",
  "message": "Success create {total register account} users"
}
```


Response Body (4xx)

```json
{
  "message": "error message"
}
```

## Login

Method : POST

URL: /login

Request Body:

```json
{
  "username": "1234567890",
  "password": "secret"
}
```

Response Body (2xx)

```json
{
  "data": {
    "token": "rendom token using uuid",
    "user": {
      "username": "1234567890",
      "email": "email@mail.com",
      "name": "simple name",
      "gender": "MALE",
      "major": "M199",
      "completed": false,
      "motto": null,
      "avatar": null.,
      "roles": ["MAHASISWA","ADMIN"]
    }
  }
}
```

Response Body (4xx)
```json
{
  "message": "error message"
}
```

## Logout

Method : DELETE

URL: /logout

Request Header :

- X-API-TOKEN: "token-example" (Mandatory, ALL ROLES)

Response Body (2xx)

```json
{
  "data": "OK",
  "message": "Logged out successfully"
}
```

Response Body (4xx)
```json
{
  "message": "error message"
}
```




