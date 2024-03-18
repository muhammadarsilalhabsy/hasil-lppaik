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
