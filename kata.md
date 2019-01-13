# Kata Retrospective 11 - Partie 1

L'idée de ce kata est d'implanter un [Lexer](https://en.wikipedia.org/wiki/Lexer) capable de transformer une chaine de caractère en un token, c-a-d un identifiant, un mot-clé, une valeur numérique, etc. Pour reconnaitre si une chaine de caractère est un des tokens définis, on utilise des expressions régulières. Le but de ce kata est plus de se focaliser sur l'API que sur l'implantation en elle même, cela tombe bien, en Java le package java.util.regex nous enlèves le poid d'avoir à ré-implantant la gestion des expressions régulières.

Le kata est en deux parties, suivez ce [lien pour la seconde partie](kata-part2.md).


## Rappel sur java.util.regex

Une expression régulière est représentée en Java par la classe Pattern.
- la méthode static [Pattern.compile(regex)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#compile(java.lang.String)) prend une expression régulière sous forme de String, et construit l'automate correspondant,
- la méhode [pattern.matcher(text)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#matcher(java.lang.CharSequence)) créer un Matcher, un curseur sur l'automate qui se déplacera en fonction des caractères contenu dans `text`,
- la méthode [matcher.matches()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Matcher.html#matches()) déplace le curseur et renvoie vrai si le texte est reconnu par l'automate,
- les méthodes [matcher.groupCount()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Matcher.html#groupCount()) et [matcher.group(index)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Matcher.html#group(int)) permet d'extraire les motifs reconnu par les groupes (un [groupe](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html#cg) est une partie de l'expression régulière définie par des parenthèses, par convention le groupe 0 correspond à tout le texte).

Par exemple, une exécution du code suivant
```java
  var pattern = Pattern.compile("([a-z]o)o");
  System.out.println(matcher.matches());
  var matcher = pattern.matcher("zoo");
  System.out.println(matcher.group(1));
``` 
affiche `true` car zoo est bien reconnu par le pattern [a-z]oo puis `zo` car le group 1 a capturé les lettres z et o (celles entre parenthèses).

Note: il n'existe pas de moyen direct de demander à Pattern combien il y a de groupes dans l'expression régulière qui a servi à créer le Pattern, il faut créer un Matcher puis faire un groupCount.


## Question 1

Un Lexer est un objet qui est configuré avec des expressions régulières et qui indique si un texte peut être transformer ou non en token.
De façon abstraite, c'est une fonction qui prend un texte en entrée et qui 
- soit renvoie un token (on va dire un T comme cela, cela marchera avec n'importe quoi)
- soit renvoie rien si le texte n'est pas reconnu.
Note: "un truc ou rien en Java", c'est un [Optional](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html).

On va dans un premier temps, créer un Lexer (avec la méthode `create`) qui quelque soit le texte, ne le reconnait pas,
ça à pas l'air super utile mais cela permet d'avoir une instance sur laquelle on peut, après, faire des choses intéressantes.

Ecrire un code dans Lexer.java de tel façon que l'exmple ci-dessous fonctionne. 
```java
var lexer = Lexer.create();
System.out.println(lexer.tryParse("a_keyword").isEmpty());  // affiche true  
```

Vérifier que les [tests unitaires](https://github.com/forax/kata-restrospective-11/blob/master/src/test/java/fr/umlv/lexer/LexerTest.java) marqués Q1 (pour question 1) passent, sinon modifier votre code en conséquence.


## Question 2

On va maintenant créer une seconde implantantation de Lexer (avec une méthode `from`) qui utilise une expression régulière ayant un groupe et
qui renvoie le contenu du texte capturé par le groupe si le texte verifie l'expression régulière ou rien sinon.

Par exemple,
```java
var lexer = Lexer.from(Pattern.compile("([a-z]o)o"));
System.out.println(lexer.tryParse("zoo").orElseThrow());  // affiche zo
System.out.println(lexer.tryParse("bar").isEmpty);  // affiche vrai
```

et comme demander à l'utilisateur de faire des Pattern.compile à chaque fois, c'est pas terrible, on va ajouter une surchage à `from`
pour que le code ci-dessus marche en faisant un `Lexer.from("([a-z]o)o")` directement.

Vérifier que les tests unitaires marqués Q2 passent, sinon modifier votre code en conséquence.
Note: vous pouvez en même temps admirer comment on écrit en [JUnit 5](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests) des tests qui marche sur plusiuers implantations (ici from(Pattern) et from(String)).


## Question 3

Renvoyer une chaine de caractère lorsque l'on reconnait une expression régulière c'est bien mais renvoyer directement sa valeur
(un entier si le motif est un entier, une date si le motif est une date, etc) c'est mieux.

L'idée ici est que l'on a déjà un Lexer qui reconnait et extrait un morceau de texte, pour transformer le texte en une valeur,
il suffit de demander à l'utilisateur de donner une fonction qui prend une chaine de caractère et renvoie la valeur.
Habituellement, la méthode qui prend une fonction en paramètre pour transformer la valeur qui est à l'intérieur s'appel <tt>map</tt>.

```java
var lexer = Lexer.from("([0-9]+)").map(Integer::parseInt);
System.out.println(lexer.tryParse(404).orElseThrow());  // affiche 404 (sous forme d'Integer)
```

Vérifier que les tests unitaires marqués Q3 passent, sinon modifier votre code en conséquence.


## Question 4

On cherche maintenant à reconnaitre non pas un seul token, mais plusieurs, il nous faut donc une façon de combiner deux Lexer.
On se propose d'ajouter une méthode <tt>or</tt> qui prend en paramètre un Lexer et renvoie un nouveau Lexer.
Le lexer renvoyé demande au premier lexer d'essayer de reconnaitre le texte et
si celui-ci n'est pas reconnu demande au second Lexer pris en paramètre de faire la même chose. 

```java
var lexer1 = Lexer.from("([0-9]+)").map(Integer::parseInt);
var lexer2 = Lexer.from("([0-9]+\\.[0-9]*)").map(Double::parseDouble);
var lexer3 = lexer1.or(lexer2);
```

Vérifier que les tests unitaires marqués Q4 passent, sinon modifier votre code en conséquence.


## Question 5

Enfin, pour finir cette première partie, on cherche maintenant à faire en sorte que le code ci-dessous fonctionne
```java
  var lexer = Lexer.create()
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble)
        .with("([a-zA-Z]+)",       Function.identity());
  System.out.println(lexer.tryParse("foo").orElseThrow());   // affiche la chaine foo
  System.out.println(lexer.tryParse("12.3").orElseThrow());  // affiche la valeur flottante 12.3
  System.out.println(lexer.tryParse("200").orElseThrow());   // affiche la valeur entière 200
```

Pour cela, ajouter la méthode <tt>with</tt> qui associe à une expression régulière une fonction à évaluer.

Vérifier que les tests unitaires marqués Q5 passent, sinon modifier votre code en conséquence.


## Et la suite

Félicitation vous avez réussi la première partie du kata, heu juste en passant, on est d'accord que toute les méthodes
que vous avez écrite jusqu'à présent ne font pas plus de 2 ou 3 lignes ?
Sinon votre code est trop compliqué, il peut être simplifié !

Une fois que vous êtes content de votre code, vous pouvez passer à la [Partie 2](kata-part2.md).

