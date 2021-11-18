# :iphone:SYM - Labo 2 - Protocoles applicatifs

:bust_in_silhouette: **Auteurs :** `Alexis Allemann` ,  `Hakim Balestrieri` et `Christian Gomes`

:page_facing_up: **En bref :** Ce repository contient la réalisation du laboratoire nommé "Protocoles applicatifs" de l'[HEIG-VD](https://heig-vd.ch/). Ce travail est réalisé durant le cours de SYM.

:dart: **Objectifs :** Ce laboratoire propose d'illustrer l'utilisation de protocoles de communication applicatifs, basés sur http, dans le cadre d'applications mobiles.

## Manipulations :writing_hand:

Notre classe `SymComManager` est responsable de gérer les interactions avec le serveur. Celle-ci se connecte de manière asynchrone (dans un nouveau thread) au serveur. Lorsqu'une réponse est obtenue, celle-ci est transmise au UI-Thread de l'activité responsable de l'appel grâce à une Handler.

### 3.1 Service de transmission asynchrone

La méthode `SendRequest` de la classe `SymComManager` est appelée en passant un contenu textuel (*text/plain*) à envoyer. L'envoi s'effectue dans un nouveau thread. Une fois la réponse obtenue. Le handler stocké statiquement permet de retourner la réponse au UI-Thread où la réponse est traitée.

### 3.2 Transmission différée

Un Handler permet de gérer un envoi des données toutes les 10 secondes. Une liste de string permet de stocker en mémoire les données à envoyer. 

> TODO : Envoyer avec une seule connexion TCP ? Assurer l'ordre d'envoi des données?

Le stockage des données en mémoire n'est pas très adapté pour un cas d'application concret. En effet, si l'activité est détruite ou l'application fermée alors l'envoi n'aura jamais lieu. De plus, il serait plus adapté de stocker le cache à envoyer dans une base de donnée locale embarquée (par exemple SQLite).

Pour envoyer les données même si l'application est fermée ou l'activité détruite, on pourrait utiliser la librairie Jetpack `WorkManager` par exemple.

### 3.3 Transmission d'objets

Un formulaire permet d'envoyer un "Directory" sous forme de sérialisation texte JSON ou XML. Il est aussi possible d'envoyer les données en tableau de byte en utilisant Protocol Buffer.

Pour chaque type d'envoi, l'objet "Directory" est sérialisé avec son contenu avant d'être transmis. Lors de la réception de la réponse, l'objet "Directory" est parsé avant d'être affiché via sa méthode `toString()` sur l'interface.

### 3.4 GraphQL - Format JSON

Lors de la création de l'activité, les auteurs sont chargés dans une liste déroulante (Spinner Android). Lors de la sélection d'un auteur, l'ensemble des livres qu'il a écrit sont affichés dans une liste scrollable.

Nous avons veillé à l'under-fetching et l'over-fetching en ne récupérant dans la réponse que les champs nécessaires à afficher.

### 3.5 Transmission compressée

Les données envoyées sont compressées avant d'être envoyées.  Le deflater suivant a été utilisé pour compresser les données : `Deflater(DEFLATED, true)`.

Lors de la réception, on décompresse les données reçues. L'inflater suivant a été utilisé pour décompresser les données : `Inflater(true)`.

## Réponse aux questions :question:

### 4.1 Traitement des erreurs

Si le serveur n'est pas joignable, l'exception `'java.net.UnknownHostException'` est lancée par la connexion lors de l'obtention de l'output stream.

On peut vérifier si un code de statut HTTP d'erreur est renvoyé avec le code suivante : `connection.responseCode == HttpURLConnection.HTTP_OK`. Ainsi, il serait possible de conditionner les traitement si la réponse est OK (Statut 2XX), en erreur client (Statut 4XX) ou en erreur serveur (statut 5XX).

### 4.2 Authentification

On peut utiliser un protocole asynchrone si une authentification est requise. Pour cela, on peut ajouter dans l'entête de la requête le cookie de session ou les identifiants. Par contre cela implique que les données transmises soient chiffrés avec TLS. En effet, sinon il serait possible de voler les identifiants en sniffant le trafic réseau.

Si on utilise une transmission différée, il est possible que le cookie de session de l'utilisateur ne soit plus valide et que le serveur réponde donc avec une statut "401 - non autorisé".

### 4.3 Threads concurrents

Il faut s'assurer que les threads n'ont pas accès à des variables communes. Sinon, il faudrait gérer l'exclusion mutuelle de l'accès en modification à une variable commune.

Il faut aussi gérer ce qu'il se passe lorsque l'action d'un thread n'est plus utile (par exemple lors de l'affichage d'une liste d'image scrollable chargées dynamiquement). Un thread peut être interrompu et il faut gérer ce qu'il se passe si c'est le cas pour ne pas invalider des données.

### 4.4 Ecriture différée

**Une connexion par transmission** différée :

| Avantages                             | Inconvénients                                            |
| ------------------------------------- | -------------------------------------------------------- |
| Moins à retransmettre en cas d'erreur | Plus de connexion TCP                                    |
| Facilité d'envoi des données          | Moins bon rapport payload / données envoyées             |
|                                       | Possiblement plusieurs threads gèrent les communications |
|                                       | Ordre d'exécution des requêtes non garanti               |

> Les réponses du serveur doivent être planifiées séparément

**Multiplexer les envois** en une seule connexion de transport :

| Avantages                                   | Inconvénients                                         |
| ------------------------------------------- | ----------------------------------------------------- |
| Moins d'ouvertures de connexions TCP        | Données sauvegardées moins rapidement                 |
| Meilleur rapport payload / données envoyées | Implémentation plus compliquée de l'envoi des données |
| Un seul thread gère la communication        |                                                       |
| Garantir l'ordre d'exécution des requêtes   |                                                       |

> Les réponses du serveur peuvent être traitée toutes ensembles

### 4.5 Transmission d’objets

**Question a :** 

Il est nécessaire de valider la cohérence des données envoyées côté serveur et de gérer les erreurs. En utilisant un service de validation, le serveur peut partir du postulat que les données reçues sont valides.

L'avantage d'une infrastructure REST/JSON est que les données envoyées sont moins verbeuses que du XML. Le rapport données envoyées / données utiles est meilleur. De plus, avec JSON est plus simple à implémenter pour sérialiser / désérialiser des objets.

**Question b :** 

Charger les 2000 auteurs en une seule fois dans la liste déroulante n'est pas utile. En effet, il serait préférable de charger dynamiquement les auteurs en gérant de la pagination et du recyclage des éléments dans la liste avec une mise en cache.

Il en va de même pour le chargement des livres rédigés par l'auteur.

Il est donc nécessaire de s'interroger sur la taille des pages chargées et la quantité de données mises en cache pour ne pas surcharger la mémoire.

### 4.6 Transmission compressée

Lors de l'envoi de peu de données, on remarque que le payload issu de la compression est plus grand que la taille des données qu'il contient. De plus, la tansmission et la réception des données est moins rapide puisqu'il faut compresser le tout. Dans ce cas, il n'est donc pas intéressant de compresser les données.

Par contre, plus la quantité de données envoyées est grande, plus il est intéressant de compresser les données. Cependant, lorsqu'une grande quantité de donnée est envoyée, le gain de la compression se stabilise comme on peut le voir dans le graphe ci-dessous (issu de nos tests) :

![](/img/graphic.jpg)

Le temps de compression des données augmente aussi selon la taille de ce qu'il faut compresser / décompresser. Cependant, si l'on transmet de grosses quantités d'informations, il est préférable d'utiliser ce temps pour compresser / décompresser les données plutôt que d'envoyer des payloads trop important au serveur.

> Si le type de données envoyées n'est pas du texte (image, vidéo, ...) et que sa taille est grande, l'envoi compressé est très intéressant (tant en terme de place qu'en terme de temps). Le serveur pourrait aussi envoyer ces données en utilisant un "Chunked transfer".