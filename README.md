# :iphone:SYM - Labo 2 - Protocoles applicatifs

:bust_in_silhouette: **Auteurs :** `Alexis Allemann` ,  `Hakim Balestrieri` et `Christian Gomes`

:page_facing_up: **En bref :** Ce repository contient la r�alisation du laboratoire nomm� "Protocoles applicatifs" de l'[HEIG-VD](https://heig-vd.ch/). Ce travail est r�alis� durant le cours de SYM.

:dart: **Objectifs :** Ce laboratoire propose d'illustrer l'utilisation de protocoles de communication applicatifs, bas�s sur http, dans le cadre d'applications mobiles.

## Manipulations :writing_hand:

Notre classe `SymComManager` est responsable de g�rer les interactions avec le serveur. Celle-ci se connecte de mani�re asynchrone (dans un nouveau thread) au serveur. Lorsqu'une r�ponse est obtenue, celle-ci est transmise au UI-Thread de l'activit� responsable de l'appel gr�ce � une Handler.

### 3.1 Service de transmission asynchrone

La m�thode `SendRequest` de la classe `SymComManager` est appel�e en passant un contenu textuel (*text/plain*) � envoyer. L'envoi s'effectue dans un nouveau thread. Une fois la r�ponse obtenue. Le handler stock� statiquement permet de retourner la r�ponse au UI-Thread o� la r�ponse est trait�e.

### 3.2 Transmission diff�r�e

Un Handler permet de g�rer un envoi des donn�es toutes les 10 secondes. Une liste de string permet de stocker en m�moire les donn�es � envoyer. 

> TODO : Envoyer avec une seule connexion TCP ? Assurer l'ordre d'envoi des donn�es?

Le stockage des donn�es en m�moire n'est pas tr�s adapt� pour un cas d'application concret. En effet, si l'activit� est d�truite ou l'application ferm�e alors l'envoi n'aura jamais lieu. De plus, il serait plus adapt� de stocker le cache � envoyer dans une base de donn�e locale embarqu�e (par exemple SQLite).

Pour envoyer les donn�es m�me si l'application est ferm�e ou l'activit� d�truite, on pourrait utiliser la librairie Jetpack `WorkManager` par exemple.

### 3.3 Transmission d'objets

Un formulaire permet d'envoyer un "Directory" sous forme de s�rialisation texte JSON ou XML. Il est aussi possible d'envoyer les donn�es en tableau de byte en utilisant Protocol Buffer.

Pour chaque type d'envoi, l'objet "Directory" est s�rialis� avec son contenu avant d'�tre transmis. Lors de la r�ception de la r�ponse, l'objet "Directory" est pars� avant d'�tre affich� via sa m�thode `toString()` sur l'interface.

### 3.4 GraphQL - Format JSON

Lors de la cr�ation de l'activit�, les auteurs sont charg�s dans une liste d�roulante (Spinner Android). Lors de la s�lection d'un auteur, l'ensemble des livres qu'il a �crit sont affich�s dans une liste scrollable.

Nous avons veill� � l'under-fetching et l'over-fetching en ne r�cup�rant dans la r�ponse que les champs n�cessaires � afficher.

### 3.5 Transmission compress�e

Les donn�es envoy�es sont compress�es avant d'�tre envoy�es.  Le deflater suivant a �t� utilis� pour compresser les donn�es : `Deflater(DEFLATED, true)`.

Lors de la r�ception, on d�compresse les donn�es re�ues. L'inflater suivant a �t� utilis� pour d�compresser les donn�es : `Inflater(true)`.

## R�ponse aux questions :question:

### 4.1 Traitement des erreurs

Si le serveur n'est pas joignable, l'exception `'java.net.UnknownHostException'` est lanc�e par la connexion lors de l'obtention de l'output stream.

On peut v�rifier si un code de statut HTTP d'erreur est renvoy� avec le code suivante : `connection.responseCode == HttpURLConnection.HTTP_OK`. Ainsi, il serait possible de conditionner les traitement si la r�ponse est OK (Statut 2XX), en erreur client (Statut 4XX) ou en erreur serveur (statut 5XX).

### 4.2 Authentification

On peut utiliser un protocole asynchrone si une authentification est requise. Pour cela, on peut ajouter dans l'ent�te de la requ�te le cookie de session ou les identifiants. Par contre cela implique que les donn�es transmises soient chiffr�s avec TLS. En effet, sinon il serait possible de voler les identifiants en sniffant le trafic r�seau.

Si on utilise une transmission diff�r�e, il est possible que le cookie de session de l'utilisateur ne soit plus valide et que le serveur r�ponde donc avec une statut "401 - non autoris�".

### 4.3 Threads concurrents

Il faut s'assurer que les threads n'ont pas acc�s � des variables communes. Sinon, il faudrait g�rer l'exclusion mutuelle de l'acc�s en modification � une variable commune.

Il faut aussi g�rer ce qu'il se passe lorsque l'action d'un thread n'est plus utile (par exemple lors de l'affichage d'une liste d'image scrollable charg�es dynamiquement). Un thread peut �tre interrompu et il faut g�rer ce qu'il se passe si c'est le cas pour ne pas invalider des donn�es.

### 4.4 Ecriture diff�r�e

**Une connexion par transmission** diff�r�e :

| Avantages                             | Inconv�nients                                            |
| ------------------------------------- | -------------------------------------------------------- |
| Moins � retransmettre en cas d'erreur | Plus de connexion TCP                                    |
| Facilit� d'envoi des donn�es          | Moins bon rapport payload / donn�es envoy�es             |
|                                       | Possiblement plusieurs threads g�rent les communications |
|                                       | Ordre d'ex�cution des requ�tes non garanti               |

> Les r�ponses du serveur doivent �tre planifi�es s�par�ment

**Multiplexer les envois** en une seule connexion de transport :

| Avantages                                   | Inconv�nients                                         |
| ------------------------------------------- | ----------------------------------------------------- |
| Moins d'ouvertures de connexions TCP        | Donn�es sauvegard�es moins rapidement                 |
| Meilleur rapport payload / donn�es envoy�es | Impl�mentation plus compliqu�e de l'envoi des donn�es |
| Un seul thread g�re la communication        |                                                       |
| Garantir l'ordre d'ex�cution des requ�tes   |                                                       |

> Les r�ponses du serveur peuvent �tre trait�e toutes ensembles

### 4.5 Transmission d�objets

**Question a :** 

Il est n�cessaire de valider la coh�rence des donn�es envoy�es c�t� serveur et de g�rer les erreurs. En utilisant un service de validation, le serveur peut partir du postulat que les donn�es re�ues sont valides.

L'avantage d'une infrastructure REST/JSON est que les donn�es envoy�es sont moins verbeuses que du XML. Le rapport donn�es envoy�es / donn�es utiles est meilleur. De plus, avec JSON est plus simple � impl�menter pour s�rialiser / d�s�rialiser des objets.

**Question b :** 

Charger les 2000 auteurs en une seule fois dans la liste d�roulante n'est pas utile. En effet, il serait pr�f�rable de charger dynamiquement les auteurs en g�rant de la pagination et du recyclage des �l�ments dans la liste avec une mise en cache.

Il en va de m�me pour le chargement des livres r�dig�s par l'auteur.

Il est donc n�cessaire de s'interroger sur la taille des pages charg�es et la quantit� de donn�es mises en cache pour ne pas surcharger la m�moire.

### 4.6 Transmission compress�e

Lors de l'envoi de peu de donn�es, on remarque que le payload issu de la compression est plus grand que la taille des donn�es qu'il contient. De plus, la tansmission et la r�ception des donn�es est moins rapide puisqu'il faut compresser le tout. Dans ce cas, il n'est donc pas int�ressant de compresser les donn�es.

Par contre, plus la quantit� de donn�es envoy�es est grande, plus il est int�ressant de compresser les donn�es. Cependant, lorsqu'une grande quantit� de donn�e est envoy�e, le gain de la compression se stabilise comme on peut le voir dans le graphe ci-dessous (issu de nos tests) :

![](/img/graphic.jpg)

Le temps de compression des donn�es augmente aussi selon la taille de ce qu'il faut compresser / d�compresser. Cependant, si l'on transmet de grosses quantit�s d'informations, il est pr�f�rable d'utiliser ce temps pour compresser / d�compresser les donn�es plut�t que d'envoyer des payloads trop important au serveur.

> Si le type de donn�es envoy�es n'est pas du texte (image, vid�o, ...) et que sa taille est grande, l'envoi compress� est tr�s int�ressant (tant en terme de place qu'en terme de temps). Le serveur pourrait aussi envoyer ces donn�es en utilisant un "Chunked transfer".