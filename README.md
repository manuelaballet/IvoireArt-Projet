## IvoireArt - Spring Boot + Thymeleaf

IvoireArt est une vitrine e-commerce minimale pour mettre en avant les artisans ivoiriens (pagnes wax, bijoux, sculptures) avec pages dynamiques, panier et commandes.

### Prerequis
- Java 17+
- Maven 3.9+
- MySQL 8+ accessible en local sur `localhost:3306`

### Installation base de donnees
1) Creer la base et charger les donnees d'exemple :
```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -pMonkeyDREeiZLeBest669 < db_init.sql
```
Cette commande cree la base `ivoireart` avec les tables `products`, `artisans`, `cart_items`, `orders`, `order_items` et insere des produits/artisans de demo.

### Lancement de l'application
```bash
mvn spring-boot:run
# Application sur http://localhost:8080
```
Si le port 8080 est occupe, stoppez le processus qui l'utilise ou lancez avec `-Dspring-boot.run.arguments=--server.port=8081`.

### Fonctionnalites principales
- Pages dynamiques Thymeleaf : `/`, `/produits`, `/artisans`, `/contact`, `/devenir-artisan`, `/panier`.
- Connexion MySQL (config dans `src/main/resources/application.properties`).
- Ajout au panier par produit, calcul des sous-totaux et total, validation de commande (sauvegarde `orders` + `order_items` et purge du panier).
- Formulaire artisan dynamique (sauvegarde en base et affichage dans la liste).
- CDN Bootstrap + MUI et styles custom `src/main/resources/static/css/style.css`, images dans `src/main/resources/static/images`.

### Structure
- `src/main/java/com/ivoireart` : application, controleur `SiteController`, entites JPA, repositories.
- `src/main/resources/templates` : vues Thymeleaf.
- `src/main/resources/static` : CSS, images.
- `schema.sql` / `data.sql` : init automatique au demarrage (ddl-auto=none).

### Tests rapides
- Formulaire artisan : http://localhost:8080/devenir-artisan
- Boutique + panier : http://localhost:8080/produits puis http://localhost:8080/panier
- Contact : http://localhost:8080/contact
