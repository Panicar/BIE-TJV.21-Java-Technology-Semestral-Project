# Epistemic Harmony - Scientific Theory & Philosophical Statement Rating Platform

## Project Overview
**Epistemic Harmony** is a three-layer enterprise application that allows users to rate, review, and analyze relationships between scientific theories and philosophical statements. The platform facilitates interdisciplinary research by revealing connections between empirical science and philosophical reasoning.

---

## Technical Architecture

### Technology Stack
- **Language**: Java 21 (Spring Framework)
- **Persistence**: JPA/Hibernate ORM with PostgreSQL
- **Build System**: Gradle
- **API**: RESTful web services (OpenAPI documented)
- **Frontend**: Single Page Application (Vanilla JS / Bootstrap 5)

---

## Domain Model (Entities & Columns)

### 1. User (`users` table)
Represents the platform participants and their access levels.
- **id** (BigInt, PK): Unique identifier.
- **username** (Varchar): Unique display name.
- **email** (Varchar): Unique contact address.
- **password** (Varchar): Hashed credentials.
- **role** (Enum): USER, MODERATOR, or ADMIN.
- **is_active** (Boolean): Account status.

### 2. Epistemic Item (`epistemic_item` table)
The core content of the platform, representing theories or statements.
- **id** (Integer, PK): Unique identifier.
- **name** (Text): Title of the item (e.g., "Germ Theory").
- **content** (Text): Detailed description or full text.
- **category** (Text): The field of study (e.g., "Biology / Medical Science").
- **item_type** (Enum): THEORY or STATEMENT.

### 3. Review (`review` table)
User-generated feedback and quality metrics for items.
- **id** (Integer, PK): Unique identifier.
- **user_id** (FK): Reference to the User who wrote the review.
- **item_id** (FK): Reference to the Epistemic Item being rated.
- **rating** (Integer): Value from 1 to 5.
- **comment** (Text): Detailed feedback.
- **review_date** (Date): Timestamp of creation.

### 4. Connection (`connection` table)
The Many-to-Many (M:N) self-association that links items across categories.
- **id** (Integer, PK): Unique identifier.
- **from_item_id** (FK): The source Epistemic Item.
- **to_item_id** (FK): The target Epistemic Item.
- **connection_type** (Enum): SUPPORTS, CONTRADICTS, RELATES, or COMPLEMENTS.
- **strength** (Integer): Value from 1 to 10 indicating the link's intensity.

---

## Complex Business Logic & Queries

### Server-Side: Interdisciplinary Discovery (Complex JPQL)
**Requirement**: At least one complex query involving multiple tables.
**Implementation**: In `ConnectionRepository`, a query joins `Connection` with two instances of `EpistemicItem` to find bi-directional bridges between specific categories.
- **Logic**: Uses `JOIN FETCH` to eagerly load item metadata (names/categories) in a single request, preventing the N+1 performance problem.
- **Complexity**: Navigates a self-referencing Many-to-Many relationship with bi-directional `OR` logic.

### Client-Side: Interdisciplinary Analysis Engine
**Requirement**: A single action composed of multiple data operations.
**Implementation**: When a user clicks "Analyze" in the UI:
1. The client calls the `ConnectionController` to find links between two categories.
2. The client iterates through the results and performs individual `GET` requests to the `ReviewController` to retrieve average community ratings for every item found.
3. The client synthesizes this data to calculate a "Correlation Score" based on connection strength and item quality.

---

## REST API Standards
- **Standardized Codes**: Uses `200 OK`, `201 Created`, and `404 Not Found` correctly.
- **Zero-Error Policy**: Invalid requests return handled error messages rather than `500 Internal Server Error`.