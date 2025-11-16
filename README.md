# Hnefatafl

## Présentation du jeu

Ce projet a été réalisé dans le cadre du cours **Structures de données et algorithmes** (ÉTS).

Le but est d’implémenter une intelligence artificielle capable de jouer au jeu de plateau viking *Hnefatafl* sur un plateau de 13×13 :

- Les **attaquants (rouges)** doivent capturer le roi.
- Les **défenseurs (noirs)** doivent faire s’échapper le roi en l’amenant sur une des cases de sorti.e (coins du plateau).
- Les **attaquants jouent en premier**.

L’IA utilise notamment les algorithmes **Minimax** et **Alpha-Beta**, ainsi qu’une fonction d’évaluation heuristique pour choisir le meilleur coup dans un temps maximal de 5 secondes par tour.


## Règles du jeu

### Configuration initiale

- Le plateau est de taille **13×13**.
- Le **roi** est placé au centre sur la case appelée *trône*.
- Les **défenseurs (noirs)** sont disposés autour du roi.
- Les **assaillants (rouges)** entourent la zone centrale, plus éloignés.

### Déplacement des pièces

- Toutes les pièces (y compris le roi) se déplacent **horizontalement ou verticalement**, comme une tour aux échecs.
- Elles peuvent parcourir **une ou plusieurs cases**, tant que le chemin est libre.
- Elles ne peuvent **pas sauter** par-dessus d’autres pièces.
- Les pièces **autres que le roi** ne peuvent **pas s’arrêter** :
  - sur le **trône** (case centrale) ;
  - sur les **cases de sortie** (coins).
- Il est toutefois possible de **passer par-dessus le trône** si celui-ci est vide.

### Captures

Une pièce adverse est capturée lorsqu’elle se retrouve **encerclée activement** :

- entre **deux pièces adverses** sur une même ligne ou colonne ;
- ou entre **une pièce adverse** et :
  - le **trône** ;
  - une **case de sortie** ;
  - la **bordure du plateau**.

Il est possible de capturer **plusieurs pièces en un seul coup** si elles se retrouvent toutes encerclées après le déplacement.

Une capture doit être **active** : si une pièce se place volontairement entre deux pièces adverses, elle **n’est pas capturée**.

### Capture du roi

Le roi est capturé lorsqu’il est entièrement encerclé :

- soit par **quatre pièces adverses** ;
- soit par **deux pièces adverses** et un ou plusieurs éléments particuliers (trône, case de sortie, bordure du plateau), selon la configuration.

### Conditions de victoire

- **Victoire du défenseur** : le roi atteint l’une des **quatre cases de sortie** (coins).
- **Victoire de l’attaquant** : le roi est **capturé**.
- **Match nul** :
  - lorsqu’un joueur, à son tour, n’a **aucun coup légal** à jouer ;
  - ou lorsqu’il y a **répétition de la même séquence de coups trois fois** (non détecté automatiquement par le serveur).

## Interaction avec le serveur

L’IA communique avec un serveur de jeu via des messages textuels. Le premier caractère du message indique le type d’information.

### Types de messages

- `1` : envoi de la configuration initiale du plateau lorsque le programme joue les **rouges**.
- `2` : envoi de la configuration initiale du plateau lorsque le programme joue les **noirs**.
- `3` : c’est au tour de l’IA de jouer ; le message contient également le **dernier coup de l’adversaire**.
- `4` : le dernier coup envoyé par l’IA est **invalide** ; le serveur attend un nouveau coup.
- `5` : **fin de la partie** ; le message contient le **dernier coup** joué (celui du gagnant).

Exemple de message indiquant que c’est à l’IA de jouer : `3 G12-L12`
