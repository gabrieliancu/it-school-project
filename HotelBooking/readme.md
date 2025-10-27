# HotelBooking — README

**Proiect:** HotelBooking (API REST pentru rezervări hoteliere)

**Scop:** aplicație demo pentru predare/învățare: Spring Boot, Spring Data JPA, Hibernate, Spring Security, DTO-uri, teste unitare și de integrare, OpenAPI/Swagger.

---

## Descriere scurtă
HotelBooking este un backend REST care permite gestionarea de hoteluri, camere, tipuri de camere, planuri tarifare si rezervări 

Aplicația oferă un flux complet:
- utilizatorul poate vizualiza hotelurile și camerele disponibile,  
- poate crea o rezervare pentru una sau mai multe camere (cu verificări de conflict, perioadă, și plan tarifar valid),
- poate confirma sau anula rezervarea,  
- iar sistemul actualizează automat starea camerelor (AVAILABLE / BOOKED).

Acoperă concepte esențiale Java/Spring:
- principiile **OOP (moștenire, polimorfism, încapsulare, abstractizare),
- straturi arhitecturale clare (Controller → Service → Repository → Entity),
- mapping DTO pentru separarea logicii de prezentare,
- testare automată (unit + integration tests).
---

## Funcționalități principale
Hotel
- CRUD Hotel (creare, listare, actualizare, ștergere)
- Atribute: nume, locație, rating

RoomType
- Definirea tipurilor de camere (ex: Single, Double, Deluxe)
- Fiecare tip este asociat cu un hotel

Room
- CRUD camere
- Atribute: roomNumber, status (AVAILABLE / BOOKED), hotel, roomType
- Camerele se pot filtra după hotel și tip

RatePlan
- Definește prețul/noapte pentru un anumit `RoomType` într-un interval de timp
- Fiecare plan tarifar aparține unui hotel și unui tip de cameră
- Suportă perioade active (startDate, endDate)

Reservation
- Creare rezervare (`ONHOLD`) pentru una sau mai multe camere
- Verifică:
  - dacă camerele sunt din același hotel
  - dacă camerele sunt de același tip (`RoomType`)
  - dacă `RatePlan` este valid pentru perioada aleasă
  - dacă există conflicte cu alte rezervări confirmate
- Confirmare rezervare (`CONFIRMED`) – marchează camerele ca BOOKED
- Anulare rezervare (`CANCELLED`) – eliberează camerele (AVAILABLE)

---

## Pachete / Structură (rezumat)
- `com.example.HotelBooking.model.entities` — entități JPA (Hotel, RoomType, Room, RatePlan, Reservation, User)
- `com.example.HotelBooking.repo` — Spring Data JPA repositories
- `com.example.HotelBooking.service` — business logic (servicii)
- `com.example.HotelBooking.controller` — REST controllers
- `com.example.HotelBooking.dto` — DTO-uri pentru request/response
- `com.example.HotelBooking.config` — DataLoader, Swagger/OpenAPI config

---

## Tehnologii & Dependențe (principale)
- Java 21
- Spring Boot (3.x)
- Spring Web
- Spring Data JPA
- Hibernate ORM
- Spring Security
- springdoc-openapi (Swagger UI)
- H2 (in-memory) — implicit pentru dezvoltare/tests
- Lombok
- JUnit5, Mockito

Exemplu de dependențe din `pom.xml` (rezumat):
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `springdoc-openapi-starter-webmvc-ui`
- `h2` (runtime)
- `lombok`
- `spring-boot-starter-test`

---


## Cum rulezi proiectul (local)
1. Clonează repo sau deschide proiectul în IDE (IntelliJ/Eclipse).
2. Asigură-te că ai Java 21 instalat și Maven configurat.
3. Setări DB: implicit H2 în memoria (development). Dacă vrei PostgreSQL/MySQL, modifică `application.properties`.
4. Build & run:
```bash
mvn clean package
mvn spring-boot:run
```
5. Accesează API: `http://localhost:8080` (port implicit 8080)

---

## Endpoints & cum să le testezi (Postman / curl / Swagger)
### Swagger UI
- După pornire: `http://localhost:8080/swagger-ui.html` sau `http://localhost:8080/swagger-ui/index.html` — interfață pentru a explora API-urile.

### Exemple rapide (Postman / curl)
#### Înregistrare user (public)
`POST /api/users/register`
Body JSON:
```json
{ "name": "Gabi", "email": "gabi@example.com", "password": "pass" }
```

#### Obține toate hotelurile (public)
`GET /api/hotels`

#### Creare hotel (admin only)
`POST /api/hotels` — folosește admin credentials.
Body JSON exemplu:
```json
{ 
  "name": "Intercontinental", 
  "location": "Bucuresti", 
  "rating": 4.5 
}
```

#### Creează camera (admin or as allowed)
`POST /api/rooms` body:
```json
{
  "status": "AVAILABLE",
  "roomNumber": "4",
  "floor": "1",
  "hotelId": 1,
  "roomTypeId": 1
}
```
Vezi apoi: `GET /api/rooms/{Id}`

#### Creează rezervare (authenticated required)
`POST /api/reservation` body:
```json
{
  "ratePlanId": 1,
  "userId": 1,
  "roomIds": [
    4
  ],
  "checkInDate": "2026-06-15",
  "checkOutDate": "2026-06-26"
}
```
Response: `BookingResponseDto` cu status `ONHOLD`.

#### Confirmare rezervare
`POST /api/reservation/confirm/{Id}` (authenticated)

---

## Testare
### Unit tests
- Rulează `mvn test` sau din IDE. Exemple incluse: teste pentru `ReservationService` (succes/conflict), `RatePlanService`.

### Integration tests
- Sunt incluse teste care pornesc context Spring (MockMvc + H2). Unele teste folosesc `@AutoConfigureMockMvc(addFilters = false)` pentru a dezactiva securitatea temporar.

### Comenzi utile
```bash
# rulează toate testele
mvn test
# build si run
mvn clean package
mvn spring-boot:run
```

---


## Extensii posibile
-Sistem de autentificare cu roluri (USER, ADMIN)
- Integrare plăți (checkout) și rezervare plătită.
-Filtrare camere după disponibilitate, locație, rating
-Integrare notificări email (confirmare/anulare)
-Frontend React / Angular conectat la API


