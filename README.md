<h1 align="center" style="font-weight: bold;">Payment Simple 💻</h1>

<p align="center">
 <a href="#tech">Technologies</a> • 
 <a href="#started">Getting Started</a> • 
  <a href="#routes">API Endpoints</a> •
</p>

<p align="center">
    <b>This project if for study purposes where work around picpay challenge</b>
</p>

<h2 id="technologies">💻 Technologies</h2>

- list of all technologies you used
- Kotlin(coroutines)
- Springboot(webflux)
- PostgreSQL
- Flyway(migrations)
- Docker
- Redis

<h2 id="started">🚀 Getting started</h2>

<h3>Prerequisites</h3>
- Docker


<h3>Cloning</h3>

How to clone your project

```bash
git clone https://github.com/vitortcmiranda/payment-simple.git
```

<h3>Starting</h3>

How to start your project

```bash
./gradlew run
```

<h2 id="routes">📍 API Endpoints</h2>

Here you can list the main routes of your API, and what are their expected request bodies.
​
| route               | description                                          
|----------------------|-----------------------------------------------------
| <kbd>POST /api/users</kbd>     | create users [response details](#user-post-detail)
| <kbd>POST /api/transactions</kbd>     | create a transaction between two users [request details](#transactions-post-detail)

<h3 id="user-post-detail">POST /api/users</h3>

**REQUEST**
```json
{
  "first_name": "teste teste",
  "last_name": "teste",
  "document": "123123123123213",
  "email": "teste@bank.com",
  "password": "teste",
  "type": "MERCHANT"
}
```

**RESPONSE**
```json
{
  "id": "dafb55bf-5fb7-481f-91ce-2e62bd1d0521",
  "firstName": "teste teste",
  "lastName": "teste",
  "document": "123123123123213",
  "email": "teste@bank.com",
  "balance": 0,
  "userType": "MERCHANT"
}
```

<h3 id="transactions-post-detail">POST /api/transactions</h3>

**REQUEST**
```json
{
  "value": 32,
  "payer": "07762e06-a52d-4872-bc88-55ff6f5919b7",
  "payee": "fd6ad9d7-5d48-4565-bc8a-214ec2f4d287"
}
```

**RESPONSE**
```json
{
  "status": true,
  "amount": 32,
  "createdAt": "2024-01-18T20:35:12.171112764Z"
}
```
