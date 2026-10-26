# Guide Michelin – API

Projet de Web services réalisé par **Gabriel COINCE**.

Le Guide Michelin a décidé de mettre à jour son application web pour gérer les restaurants visités et leur classement. Ce projet en est l'API, développée avec Spring Boot.

## Lancement

```bash
./mvnw spring-boot:run
```

## Swagger

Une fois l'application lancée, le Swagger est disponible ici : http://localhost:8080/swagger-ui.html

## Points techniques

### Droits de modification des évaluations

Un USER ne peut modifier que ses propres évaluations, alors qu'un ADMIN peut toutes les modifier. `@PreAuthorize` ne suffit pas pour faire cette différence. Je récupère donc le nom de l'utilisateur dans le token et je le compare à l'auteur de l'évaluation dans le service (`checkOwnerOrAdmin`).

### Images sur S3

L'API reçoit l'image en multipart (champ `file`) et l'envoie elle-même sur S3. En base, je stocke seulement la clé du fichier. Quand on récupère un restaurant ou une évaluation, je génère une URL temporaire pour afficher l'image.

### Recherche par mots-clés

J'ai utilisé Lucene : chaque évaluation est indexée quand elle est créée, modifiée ou supprimée. La recherche renvoie ensuite les ids des évaluations qui correspondent.