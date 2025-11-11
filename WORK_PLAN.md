# 📋 Plan de Travail - Système de Gestion de File d'Attente

## 🎯 Objectif du Projet
Réduire le temps d'attente grâce à un système de tickets numériques pour les administrations.

---

## ✅ État Actuel du Projet (Ce qui est déjà fait)

### 🗄️ Base de Données
- ✅ Schéma complet créé (`database_setup.sql`)
- ✅ 5 tables principales : administrators, agencies, services, citizens, employees, tickets
- ✅ Données de test insérées
- ✅ Relations et index configurés

### 📦 Modèles (Models)
- ✅ `Administrator.java` - Gestion des administrateurs
- ✅ `Agency.java` - Agences administratives
- ✅ `Service.java` - Services offerts (CIN, Passeport, etc.)
- ✅ `Citizen.java` - Citoyens utilisateurs
- ✅ `Employee.java` - Employés aux guichets
- ✅ `Ticket.java` - Tickets de file d'attente (avec statuts)

### 🔧 Infrastructure
- ✅ `DatabaseFactory.java` - Connexion DB avec Singleton pattern
- ✅ `jdbc.properties` - Configuration DB
- ✅ `TestDbServlet.java` - Test de connexion DB
- ✅ `web.xml` - Configuration Jakarta EE
- ✅ Structure MVC de base

### 🎨 Front-End
- ✅ Structure des dossiers (admin/, citizen/, employee/, css/, js/)
- ✅ `index.jsp` basique
- ⚠️ Aucune page fonctionnelle encore

---

## 🚧 Ce Qui Manque (À Développer)

### ❌ Couche DAO (Data Access Object)
- ❌ Interfaces DAO pour chaque modèle
- ❌ Implémentations JDBC
- ❌ Gestion des transactions

### ❌ Couche Service/Controller
- ❌ Servlets pour chaque fonctionnalité
- ❌ Logique métier (algorithme de file d'attente)
- ❌ Gestion des sessions

### ❌ Pages JSP/Interface Utilisateur
- ❌ Pages Citoyen (prise de ticket, suivi)
- ❌ Pages Employé (appel de tickets, tableau de bord)
- ❌ Pages Admin (gestion, statistiques)
- ❌ Système d'authentification

### ❌ Temps Réel (WebSockets)
- ❌ Mise à jour automatique des positions
- ❌ Notifications
- ❌ Tableau d'affichage

---

## 📅 Plan de Développement par Phases

## 🔴 **PHASE 1 : COUCHE DAO & AUTHENTIFICATION** (Priorité Haute)
> *Durée estimée : 3-4 jours*

### Tâche 1.1 : Créer les Interfaces DAO
**Fichiers à créer :**
- [ ] `dao/CitizenDAO.java`
- [ ] `dao/EmployeeDAO.java`
- [ ] `dao/AdministratorDAO.java`
- [ ] `dao/TicketDAO.java`
- [ ] `dao/ServiceDAO.java`
- [ ] `dao/AgencyDAO.java`

**Méthodes principales :**
```java
// Exemple pour CitizenDAO
- create(Citizen citizen)
- findById(int id)
- findByEmail(String email)
- update(Citizen citizen)
- delete(int id)
- findAll()
```

### Tâche 1.2 : Implémenter les DAOs
**Fichiers à créer :**
- [ ] `dao/impl/CitizenDAOImpl.java`
- [ ] `dao/impl/EmployeeDAOImpl.java`
- [ ] `dao/impl/AdministratorDAOImpl.java`
- [ ] `dao/impl/TicketDAOImpl.java` (le plus complexe)
- [ ] `dao/impl/ServiceDAOImpl.java`
- [ ] `dao/impl/AgencyDAOImpl.java`

**Points critiques :**
- Gestion des exceptions SQL
- Fermeture correcte des ressources (try-with-resources)
- Requêtes préparées (PreparedStatement)

### Tâche 1.3 : Système d'Authentification
**Fichiers à créer :**
- [ ] `servlets/LoginServlet.java` (POST)
- [ ] `servlets/LogoutServlet.java`
- [ ] `servlets/RegisterServlet.java` (pour citoyens)
- [ ] `filters/AuthenticationFilter.java` (vérifier session)
- [ ] `utils/PasswordUtils.java` (hash BCrypt/SHA-256)

**Pages JSP à créer :**
- [ ] `webapp/login.jsp` (commun)
- [ ] `webapp/citizen/register.jsp`

---

## 🟠 **PHASE 2 : FONCTIONNALITÉS CITOYEN** (Priorité Haute)
> *Durée estimée : 4-5 jours*

### Tâche 2.1 : Prise de Ticket en Ligne
**Servlets :**
- [ ] `servlets/citizen/CreateTicketServlet.java`
  - GET : Afficher formulaire (choisir service + agence)
  - POST : Créer ticket avec numéro unique
  - Assigner position dans la file

**Pages JSP :**
- [ ] `webapp/citizen/dashboard.jsp` (page d'accueil citoyen)
- [ ] `webapp/citizen/create-ticket.jsp` (formulaire)
- [ ] `webapp/citizen/ticket-confirmation.jsp` (ticket créé)

**DAO à améliorer :**
- [ ] `TicketDAO.generateTicketNumber(agencyId, serviceId)` → "A001", "B023"
- [ ] `TicketDAO.getNextPosition(agencyId, serviceId)`

### Tâche 2.2 : Suivi en Temps Réel
**Servlets :**
- [ ] `servlets/citizen/TrackTicketServlet.java` (GET)
  - Afficher position actuelle
  - Temps d'attente estimé
  - Statut du ticket

**Pages JSP :**
- [ ] `webapp/citizen/track-ticket.jsp`
  - Affichage dynamique (AJAX polling ou WebSocket)
  - Indicateur visuel de progression

**API REST (optionnel) :**
- [ ] `api/TicketStatusAPI.java` (GET /api/ticket/{id}/status) → JSON

### Tâche 2.3 : Historique des Tickets
**Servlets :**
- [ ] `servlets/citizen/TicketHistoryServlet.java`

**Pages JSP :**
- [ ] `webapp/citizen/history.jsp`

---

## 🟡 **PHASE 3 : FONCTIONNALITÉS EMPLOYÉ** (Priorité Moyenne)
> *Durée estimée : 3-4 jours*

### Tâche 3.1 : Tableau de Bord Employé
**Servlets :**
- [ ] `servlets/employee/DashboardServlet.java`
  - Voir tickets en attente pour son guichet/agence
  - Appeler le prochain ticket

**Pages JSP :**
- [ ] `webapp/employee/dashboard.jsp`
  - Liste des tickets en attente
  - Bouton "Appeler suivant"
  - Ticket en cours de traitement

### Tâche 3.2 : Gestion des Tickets
**Servlets :**
- [ ] `servlets/employee/CallTicketServlet.java` (POST)
  - Changer statut → CALLED
  - Assigner counter_id
  - Mettre à jour called_at

- [ ] `servlets/employee/StartServiceServlet.java` (POST)
  - Statut → IN_PROGRESS

- [ ] `servlets/employee/CompleteTicketServlet.java` (POST)
  - Statut → COMPLETED
  - Mettre à jour completed_at

- [ ] `servlets/employee/CancelTicketServlet.java` (POST)

**DAO à ajouter :**
- [ ] `TicketDAO.getWaitingTicketsByAgency(agencyId, serviceId)`
- [ ] `TicketDAO.updateStatus(ticketId, status)`

### Tâche 3.3 : Statistiques Basiques
**Servlets :**
- [ ] `servlets/employee/StatsServlet.java`
  - Nombre de tickets traités aujourd'hui
  - Temps moyen de traitement

**Pages JSP :**
- [ ] `webapp/employee/stats.jsp`

---

## 🟢 **PHASE 4 : FONCTIONNALITÉS ADMIN** (Priorité Moyenne)
> *Durée estimée : 4-5 jours*

### Tâche 4.1 : Gestion des Services
**Servlets :**
- [ ] `servlets/admin/ServiceManagementServlet.java`
  - GET : Liste des services
  - POST : Créer/Modifier/Supprimer service

**Pages JSP :**
- [ ] `webapp/admin/services.jsp`
- [ ] `webapp/admin/service-form.jsp`

### Tâche 4.2 : Gestion des Agences
**Servlets :**
- [ ] `servlets/admin/AgencyManagementServlet.java`

**Pages JSP :**
- [ ] `webapp/admin/agencies.jsp`
- [ ] `webapp/admin/agency-form.jsp`

### Tâche 4.3 : Gestion des Employés
**Servlets :**
- [ ] `servlets/admin/EmployeeManagementServlet.java`

**Pages JSP :**
- [ ] `webapp/admin/employees.jsp`
- [ ] `webapp/admin/employee-form.jsp`

### Tâche 4.4 : Tableau de Bord & Statistiques
**Servlets :**
- [ ] `servlets/admin/DashboardServlet.java`
- [ ] `servlets/admin/ReportsServlet.java`

**Pages JSP :**
- [ ] `webapp/admin/dashboard.jsp`
  - Tickets du jour
  - Temps d'attente moyen par service
  - Taux de complétion
  - Graphiques (Chart.js)

**DAO à ajouter :**
- [ ] `TicketDAO.getDailyStatistics(date)`
- [ ] `TicketDAO.getAverageWaitTime(serviceId, agencyId)`

---

## 🔵 **PHASE 5 : TEMPS RÉEL (WebSockets)** (Priorité Basse - Nice to Have)
> *Durée estimée : 3-4 jours*

### Tâche 5.1 : Configuration WebSocket
**Dépendances :**
- [ ] Ajouter `jakarta.websocket-api` (si pas inclus)

**Fichiers à créer :**
- [ ] `websocket/QueueWebSocket.java`
  - Endpoint: `/ws/queue`
  - Émettre mises à jour de position
  - Broadcast aux citoyens

### Tâche 5.2 : Notifications Temps Réel
**JavaScript Front-End :**
- [ ] `webapp/js/websocket-client.js`
  - Connexion WebSocket
  - Mise à jour dynamique sans refresh

**Servlets WebSocket :**
- [ ] `websocket/NotificationWebSocket.java`
  - Notifier citoyen quand son tour approche
  - Notifier employés de nouveaux tickets

### Tâche 5.3 : Tableau d'Affichage Public
**Pages JSP :**
- [ ] `webapp/display/queue-board.jsp`
  - Plein écran
  - Liste des 10 prochains tickets
  - Numéro du ticket actuel appelé

---

## 🎨 **PHASE 6 : UI/UX & DESIGN** (En parallèle)
> *Durée estimée : 2-3 jours*

### Tâche 6.1 : CSS & Design System
**Fichiers à créer :**
- [ ] `webapp/css/style.css` (améliorer)
- [ ] `webapp/css/citizen.css`
- [ ] `webapp/css/employee.css`
- [ ] `webapp/css/admin.css`

**Options :**
- Bootstrap 5
- Tailwind CSS
- Material Design

### Tâche 6.2 : JavaScript Interactif
**Fichiers à créer :**
- [ ] `webapp/js/main.js`
- [ ] `webapp/js/ajax-utils.js`
- [ ] `webapp/js/notifications.js`

### Tâche 6.3 : Templates JSP Réutilisables
**Fichiers à créer :**
- [ ] `webapp/includes/header.jsp`
- [ ] `webapp/includes/footer.jsp`
- [ ] `webapp/includes/navbar.jsp` (différent par rôle)

---

## 🧪 **PHASE 7 : TESTS & VALIDATION** (Priorité Haute)
> *Durée estimée : 2-3 jours*

### Tâche 7.1 : Tests Unitaires
**Fichiers à créer :**
- [ ] `test/dao/impl/TicketDAOImplTest.java`
- [ ] `test/dao/impl/CitizenDAOImplTest.java`

**Outils :**
- JUnit 5
- Mockito (pour mock DB)

### Tâche 7.2 : Tests d'Intégration
- [ ] Test du flow complet : Création ticket → Appel → Complétion
- [ ] Test des sessions utilisateurs
- [ ] Test des WebSockets

### Tâche 7.3 : Tests Manuels
**Scénarios :**
- [ ] Citoyen prend un ticket
- [ ] Employé appelle et traite le ticket
- [ ] Admin consulte les statistiques
- [ ] Notifications temps réel fonctionnent

---

## 📦 **PHASE 8 : DÉPLOIEMENT** (Priorité Basse)
> *Durée estimée : 1-2 jours*

### Tâche 8.1 : Configuration Serveur
- [ ] Tomcat 10+ (Jakarta EE 9+)
- [ ] MySQL en production
- [ ] Configuration `jdbc.properties` pour prod

### Tâche 8.2 : Documentation
**Fichiers à créer :**
- [ ] `README.md` (instructions installation)
- [ ] `DEPLOYMENT.md`
- [ ] Documentation API (si REST API)

### Tâche 8.3 : Scripts de Déploiement
- [ ] Script de build (Maven/Gradle)
- [ ] Script de déploiement DB
- [ ] Variables d'environnement

---

## 🛠️ Technologies & Dépendances Nécessaires

### Backend
- ✅ Jakarta EE (Servlets, JSP)
- ✅ MySQL Connector/J
- ⚠️ BCrypt ou SHA-256 (pour mots de passe)
- ⚠️ WebSocket API (temps réel)
- ⚠️ JSON-P ou Jackson (API REST)

### Frontend
- ⚠️ Bootstrap 5 ou Tailwind CSS
- ⚠️ Chart.js (graphiques)
- ⚠️ jQuery ou Fetch API (AJAX)
- ⚠️ WebSocket Client JS

### Build & Tests
- ⚠️ Maven ou Gradle
- ⚠️ JUnit 5
- ⚠️ Mockito

---

## 📊 Priorités Recommandées

### 🔥 SPRINT 1 (Semaine 1) - MVP
1. DAO Layer complet
2. Authentification
3. Prise de ticket citoyen (basique)
4. Appel de ticket employé (basique)

### 🔥 SPRINT 2 (Semaine 2) - Fonctionnalités Core
5. Suivi en temps réel (polling AJAX)
6. Tableau de bord employé complet
7. Gestion des statuts de tickets

### 🔥 SPRINT 3 (Semaine 3) - Admin & Stats
8. Gestion des services/agences/employés
9. Statistiques et rapports
10. Amélioration UI/UX

### 🎁 SPRINT 4 (Semaine 4) - Bonus
11. WebSockets (temps réel)
12. Notifications push
13. Tableau d'affichage public

---

## 🚀 Prochaines Actions Immédiates

### À Faire Maintenant (Ordre de Priorité)
1. **Créer les interfaces DAO** (`dao/` package)
2. **Implémenter CitizenDAO et TicketDAO** (les plus critiques)
3. **Créer LoginServlet + système d'auth**
4. **Page de prise de ticket** (citizen/create-ticket.jsp)
5. **Servlet CreateTicketServlet**

### Commandes Git
```bash
# Créer une branche de développement
git checkout -b develop

# Pour chaque feature
git checkout -b feature/dao-layer
git checkout -b feature/authentication
git checkout -b feature/citizen-ticket
```

---

## 📝 Notes Importantes

### Conventions de Code
- Package naming: `dao`, `dao.impl`, `servlets`, `models`, `utils`, `filters`
- Servlets: Nommage `*Servlet.java`
- JSP: kebab-case `create-ticket.jsp`
- Classes: PascalCase
- Méthodes: camelCase

### Sécurité
- ⚠️ **CRITIQUE** : Hasher les mots de passe (ne JAMAIS stocker en clair)
- Utiliser PreparedStatement (éviter SQL injection)
- Valider toutes les entrées utilisateur
- HTTPS en production
- Session timeout approprié

### Performance
- Connection pooling (Apache DBCP ou HikariCP)
- Cache pour les services/agences (rarement modifiés)
- Index DB sur les colonnes fréquemment requêtées

---

## 🎯 Résumé Exécutif

**Projet déjà fait (30%) :**
- Base de données complète
- Modèles Java
- Configuration de base

**À développer (70%) :**
- Couche DAO (critique)
- Servlets/Controllers (critique)
- Pages JSP (critique)
- Authentification (critique)
- WebSockets (optionnel)
- UI/UX (amélioration continue)

**Durée totale estimée : 3-4 semaines (1 développeur à temps plein)**

---

## 📞 Questions à Clarifier

1. **Déploiement :** Local seulement ou serveur de production ?
2. **Notifications :** Email/SMS en plus des notifications web ?
3. **Multi-agences :** Un citoyen peut-il prendre plusieurs tickets en même temps ?
4. **Annulation :** Citoyen peut annuler son ticket en ligne ?
5. **Priorités :** Files prioritaires (personnes âgées, handicapées) ?

---

**Dernière mise à jour :** 11 novembre 2025  
**Statut :** Prêt à commencer le développement 🚀
