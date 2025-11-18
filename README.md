<h1 align="center" style="font-size: 42px; margin-bottom: 10px;">
﹏𓊝﹏ Hnefatafl ﹏𓊝﹏
</h1>

<div align="center">
  <img width="246" height="122" alt="image" src="https://github.com/user-attachments/assets/36a1c06b-f87b-46bf-8cad-ff6570a8b641" />
</div>

Ce projet a été réalisé dans le cadre du cours *Structures de données et algorithmes* (ÉTS).

Le but est d’implémenter une intelligence artificielle capable de jouer au jeu de plateau viking *Hnefatafl*.
L’IA utilise notamment les algorithmes **Minimax** et **Alpha-Beta**, ainsi qu’une fonction d’évaluation heuristique pour choisir le meilleur coup dans un temps maximal de 5 secondes par tour.

## Présentation du jeu

### Objectif
- Les **attaquants (rouges)** doivent capturer le roi.
- Les **défenseurs (noirs)** doivent conduire le roi jusqu’à l’une des cases de sortie situées aux quatre coins du plateau.

### Configuration initiale

- Le plateau est de taille **13×13**.
- Le **roi** (pion avec la couronne dorée) est placé au centre sur le trône.
- Les **défenseurs (noirs)** sont disposés autour du roi, formant une croix centrée.
- Les **attaquants (rouges)** sont positionnés sur les bords du plateau, en quatre groupes : un groupe en haut, un en bas, un à gauche, un à droite.
- Les attaquants jouent en premier.

<div align="center">
   <img src="assets/début partie.png" alt="début partie" width="300" style="border-radius: 10px; margin-bottom: 20px;">
</div>

### Déplacement des pièces

- Toutes les pièces (y compris le roi) se déplacent **horizontalement ou verticalement**, comme une tour aux échecs.
- Elles peuvent parcourir **une ou plusieurs cases**, tant que le chemin est libre.
- Elles ne peuvent **pas sauter** par-dessus d’autres pièces.
- Les pièces **autres que le roi** ne peuvent **pas s’arrêter** :
  - sur le **trône** (case centrale) ;
  - sur les **cases de sortie** (coins).
- Il est toutefois possible de **passer par-dessus le trône** si celui-ci est vide.

<div align="center">
  <img src="assets/partie en cours.png" alt="partie en cours" width="300" style="border-radius: 10px; margin-bottom: 20px;">
</div>

### Captures

Une pièce de l’adversaire peut être capturée si le joueur parvient à l’encadrer. Noter que le roi peut participer aux
captures.
<div align="center">
  <img width="350" height="367" alt="image" src="https://github.com/user-attachments/assets/bb43f64b-9f3c-4c71-879e-55dbd2ca2549" />
</div>

Une pièce peut aussi être capturée si elle est encadrée par une pièce adversaire et une case de sortie ou le trône :
<div align="center">
  <img width="350" height="147" alt="image" src="https://github.com/user-attachments/assets/e0273e43-21e5-4e9a-b440-603f016ad0bb" />
</div>

Il est aussi possible de capturer plus d’une pièce à la fois :
<div align="center">
  <img width="350" height="292" alt="image" src="https://github.com/user-attachments/assets/77913cf9-da39-41c6-bedd-234b708c603b" />
</div>

Par contre, seules les captures actives sont possibles. Donc, si une pièce se place elle-même entre deux pièces
adverses, il n’y a pas de capture.

### Capture du roi

Le roi est capturé lorsqu’il est entièrement encerclé.
<div align="center">
  <img width="350" height="235" alt="image" src="https://github.com/user-attachments/assets/10190ccd-efde-4c78-935c-95ba9ab44ad3" />
</div>

Comme pour les pièces ordinaires, le roi peut aussi être capturé en utilisant le trône, la bordure ou une case de sortie :
<div align="center">
  <img width="412" height="207" alt="image" src="https://github.com/user-attachments/assets/fe7cc3b1-8242-4e14-9270-0c4156bb45c9" />
</div>

### Conditions de victoire
- **Victoire de l’attaquant** : le roi est **capturé**.
- **Victoire du défenseur** : le roi atteint l’une des **quatre cases de sortie** (coins).
- **Match nul** :
  - lorsqu’un joueur, à son tour, n’a **aucun coup** à jouer
  - ou lorsqu’il y a **répétition de la même séquence de coups trois fois**

Pour un résumé visuel des règles et de la stratégie du jeu, voici une courte vidéo récapitulative :
<div align="center">
  <a href="https://www.youtube.com/watch?v=fZ9cMj2Qn5Y">
    <img src="https://img.youtube.com/vi/fZ9cMj2Qn5Y/0.jpg" alt="How to play Viking Chess (Hnefatafl)" style="width: 60%; max-width: 400px;">
  </a>
</div>

## Interaction avec le serveur

L’IA communique avec un serveur de jeu via des messages textuels. Le premier caractère du message indique le type d’information.

### Types de messages

- `1` : envoi de la configuration initiale du plateau lorsque le programme joue les **rouges**.
- `2` : envoi de la configuration initiale du plateau lorsque le programme joue les **noirs**.
- `3` : c’est au tour de l’IA de jouer ; le message contient également le **dernier coup de l’adversaire**.
- `4` : le dernier coup envoyé par l’IA est **invalide** ; le serveur attend un nouveau coup.
- `5` : **fin de la partie** ; le message contient le **dernier coup** joué (celui du gagnant).

Exemple de message indiquant que c’est à l’IA de jouer : `3 G12-L12`
