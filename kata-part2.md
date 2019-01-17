# Kata Retrospective 11 - Partie 2

L'idée de ce kata est d'implémenter un [Lexer](https://en.wikipedia.org/wiki/Lexer) capable de transformer une chaîne de caractères en un token, c-a-d un identifiant, un mot-clé, une valeur numérique, etc. 
Pour reconnaître si une chaîne de caractère est un des tokens définis, on utilise des expressions régulières. 
Le but de ce kata est plus de se focaliser sur l'API que sur l'implémentation en elle même, cela tombe bien en Java, le package java.util.regex nous enlèves le poids d'avoir à ré-implémenter la gestion des expressions régulières.

Le kata est en deux parties, suivez ce [lien pour la première partie](kata.md).


En fait, l'implémentation que nous avons jusqu'à présent crée autant d'automates qu'il y a d'expressions régulières,
ce n'est pas vraiment nécessaire car il est possible de créer un seul automate en faisant un *ou* entre les expression régulières
et de regarder lorsque le texte `matches` le premier groupe qui a extrait une valeur.

Par exemple, pour le code
```java
  var lexer = Lexer.create()
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble);
```
On va créer l'automate correspondant à l'expression régulière `([0-9]+) | ([0-9]+\\.[0-9]*)`,
et si le premier groupe contient une valeur extraite alors on sait que c'est la première expression qui a été reconnue,
si le second groupe contient une valeur alors on sait que c'est la second expression qui a été reconnue.


## Question 6

On va créer une nouvelle méthode `from()` qui prend deux listes en paramètres,
la liste des expressions régulières et la liste des fonctions à appeler si l'expression régulière correspondante est reconnue
et renvoie un Lexer qui implémente l'algorithme décrit ci-dessus.

Voici un exemple d'utilisation
```java
  var lexer = Lexer.from(
      List.of("([0-9]+)",        "([0-9]+\\.[0-9]*)"),
      List.of(Integer::parseInt, Double::parseDouble));
```

Note: ici, on peu utiliser [List.copyOf()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)).

Vérifier que les tests unitaires marqués Q6 passent, sinon modifier votre code en conséquence.


## Question 7

Même si la méthode `from` qui prend deux listes offre un algorithme un peu plus efficace, le bénéfice de cette algorithme
est perdu si on appel les méthodes `map` ou `or` sur le Lexer résultant.

Modifier votre code pour faire en sorte qu'un seul automate soit créé même si les méthodes `map` et `or` sont utilisées
```java
  var lexer = Lexer.<Integer>from(List.of("([0-9]+)"), List.of(Integer::parseInt))
        .map(x -> x * 2)
        .or(Lexer.from(List.of("([0-9]+\\.[0-9]*)"), List.of(Double::parseDouble)));
```

Vérifier que les tests unitaires marqués Q7 passent, sinon modifier votre code en conséquence.


## Question 8

Modifier votre implémentation pour qu'un seul automate soit créé par défaut si on utilise le code suivant
```java
  var lexer = Lexer.create()
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble);
```

Vérifier que les tests unitaires marqués Q8 passent, sinon modifier votre code en conséquence.


## Question 9

Si vous ne l'avez pas déjà remarqué, nous voulons aussi laisser la possibilité de créer des Lexers
qui ne sont pas liés à des expressions régulières.

Par exemple, un Lexer qui accepte n'importe quel texte
```java
  Lexer<String> lexer1 = text -> Optional.of(text));
```


Vérifier que les tests unitaires marqués Q9 passent, sinon modifier votre code en conséquence.


## That's all folk !

Voilà c'est fini, j'espère que vous avez apprécié la versatilité et la capacité d'expression de Java.
Maintenant, à vous de jouer, à vous d'écrire un kata orienté programmation fonctionnelle avec Java 11, et faite moi une pull request
pour que j'ajoute un lien vers votre kata sur la page de README.

