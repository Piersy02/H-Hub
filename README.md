# HackHub - Piattaforma di Gestione Hackathon

<div>
  
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen)
![Database](https://img.shields.io/badge/Database-H2-blue)
  
</div>

**HackHub** è una piattaforma backend RESTful sviluppata in **Java** con **Spring Boot**, progettata per gestire l'intero ciclo di vita di un Hackathon: dalla creazione dell'evento alla formazione dei team, fino alla sottomissione dei progetti, la valutazione e la proclamazione dei vincitori.

## Funzionalità Principali

### Gestione Utenza e Ruoli
*   **Ruoli di Piattaforma:** Admin, Event Creator, Utente Registrato.
*   **Ruoli Contestuali:** Organizzatore, Giudice, Mentore (specifici per ogni singolo Hackathon).
*   **Sicurezza:** Autenticazione tramite Spring Security (Basic Auth) con password cifrate (BCrypt).

### Ciclo di Vita Hackathon
*   Creazione eventi (con definizione regole, premi, staff).
*   Gestione stati (Iscrizione -> In Corso -> Valutazione -> Concluso).

### Gestione Team
*   Creazione Team e invito membri.
*   Iscrizione agli Hackathon.
*   Gestione membri.

### Operatività
*   Caricamento e aggiornamento progetti.
*   Richiesta supporto e prenotazione call.
*   Segnalazione violazioni e squalifica team.
*   Calcolo automatico del vincitore e pagamento premio.

---

## Architettura

Il progetto implementa due Design Pattern comportamentali:

### 1. State Pattern (Gestione Stati Hackathon)
Utilizzato per gestire le regole di business che cambiano in base alla fase dell'evento.
*   **Interfaccia:** `HackathonState`
*   **Stati:** `RegistrationOpen`, `Ongoing`, `Evaluation`, `Finished`.
*   *Esempio:* Se un team prova a iscriversi quando lo stato è `ONGOING`, l'oggetto stato lancia un'eccezione, senza bisogno di `if-else` complessi nel Service.

### 2. Strategy Pattern (Servizi Esterni)
Utilizzato per simulare l'integrazione con servizi di terze parti, rendendo il sistema estendibile.
*   **Calendar:** `CalendarService` (Interfaccia) -> `MockCalendarService` (Implementazione). Usato per generare link di meeting per i mentori.
*   **Pagamenti:** `PaymentService` (Interfaccia) -> `MockPaymentService` (Implementazione). Usato per erogare il premio al vincitore.

## Istruzioni per l'Avvio

### Prerequisiti
*   JDK 21
*   Maven installato.

### Avvio da IntelliJ IDEA
1.  Apri il progetto come **Maven Project**.
2.  Attendi il download delle dipendenze.
3.  Esegui la classe principale: `src/main/java/com/ids/hhub/HHubApplication.java`.

---

## Credenziali di Test

All'avvio, il sistema carica automaticamente dei dati di prova (**Data Seeder**) per facilitare il testing.

Riportati i principali:

| Ruolo | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@hackhub.com` | `password` | 
| **Creator** | `creator1@hackhub.com` | `password` | 
| **Giudice** | `judge1@hackhub.com` | `password` | 
| **Mentore** | `mentor1@hackhub.com` | `password` |
| **Utente** | `user8@hackhub.com` | `password` |

---

## API Documentation

Una volta avviata l'applicazione, la documentazione interattiva delle API è disponibile a questo indirizzo:

👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Per consultare il Database:

👉 **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**

### Come usare Swagger
1.  Clicca sul pulsante verde **Authorize** in alto a destra.
2.  Inserisci username e password (vedi tabella sopra).
3.  Clicca **Authorize** e poi **Close**.
4.  Ora puoi testare gli endpoint.


### Autori
*   **Saverio Maria Piersigilli**
*   **Mattia Penna**
*   **Matteo Bolognini**

*Progetto per il corso di Ingegneria del Software - A.A. 2025/2026*
