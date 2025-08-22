📘 InnoShop – README
🔹 Hauptfunktionen

Nutzerregistrierung & Login

Produktsuche und -filterung

Warenkorb & Wunschliste

Bestellungen & Bestellhistorie

Bewertungsfunktion

Admin-Panel (Produkte & Nutzer verwalten)

🔹 Architektur & Technologien

Frontend: Java mit Vaadin

Backend: Java, JPA/Hibernate

Datenbank: PostgreSQL

Architektur: Klassisches Client-Server-Modell (MVC)

🔹 Projektstatus

Der Zahlungs- und Versandprozess wird aktuell simuliert.

Externe Dienste (z. B. Zahlungsanbieter, Versanddienstleister) sind als Erweiterungsmöglichkeit vorgesehen.

🔹 Installation & Start
1. Repository klonen
   git clone https://github.com/mashayekhashkan/InnoShop-

2. Datenbank einrichten

PostgreSQL starten.

Neue Datenbank anlegen:

CREATE DATABASE innoshop;

Benutzer anlegen (falls nicht vorhanden):

3. Konfiguration

Die Verbindungseinstellungen befinden sich in
src/main/resources/application.properties:

4. Projekt starten

Projekt in IntelliJ IDEA öffnen (pom.xml als Maven-Projekt importieren).

Maven-Dependencies herunterladen lassen.

Anwendung starten mit:

mvn jetty:run

oder über eine IntelliJ-Run-Konfiguration.

👉 Die Anwendung ist anschließend erreichbar unter:
http://localhost:9090

🔹 Login

Benutzername: admin

Passwort: admin

Normale Benutzer können sich über das Registrierungsformular im UI selbst anlegen.

🔹 Ausblick

Integration echter Zahlungs- und Versand-APIs

Recommendation-System für Produkte

Mobile App-Version (z. B. Flutter oder React Native)
