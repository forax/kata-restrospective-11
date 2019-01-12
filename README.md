# Kata Restrospective 11
Un kata sur comment utiliser Java 11 de façon fonctionnelle

L'idée est de découvrir les nouveautés de Java 11 (et faire un petit retour sur Java 8) sous forme d'un kata.
De plus, j'espère bien pouvoir faire une code review/retrospective de ce kata à DevoxxFR 2019 et ainsi pouvoir expliquer le comment et le pourquoi des APIs introduite en Java 11.

Les features/[API](https://docs.oracle.com/en/java/javase/11/docs/api/index.html) de Java 5/8/11 mise en oeuvre lors de ce kata
- generics et wildcards
- interface fonctionnelle, méthode par défaut
- var
- Optional, Stream et Collector
- List non modifiable: List.of(), List.copyOf(), Collectors.toUnmodifiableList()

L'idée de ce kata est d'implanter un [Lexer](https://en.wikipedia.org/wiki/Lexer) qui est capable de transformer une chaine de caractère en tokens, c-a-d un mélange d'identifiant, de mot-clés, de valeurs numériques, etc. Pour reconnaitre si une chaine de caractère est un des tokens définies, on utilisera des expressions régulières. Le but de ce kata est plus de se focaliser sur l'API que sur l'implantation en elle même, cela tombe bien en Java, le package java.util.regex nous enlèves le poid d'avoir à ce ré-implantant la gestion des expressions régulières.

Voilà une idée de l'API que l'on veut obtenir
```java
  var lexer = Lexer.create(conf -> conf
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble)
        .with("([a-zA-Z]+)",       Function.identity()));
  System.out.println(lexer.tryParse("foo").orElseThrow());   // affiche "foo" sous forme de String
  System.out.println(lexer.tryParse("12.3").orElseThrow());  // affiche 12.3 sous forme de Double
  System.out.println(lexer.tryParse("200").orElseThrow());   // affiche 200 sous forme d'Integer
```

Le kata est en deux parties, dans un premier temps, on va batir une API fonctionnelle permettant de faire fonctionner le code ci-dessus. Dans un second temps, on va poser la question de comment rendre le compte un peu plus efficace, en conservant la même API.

De plus, pour garantir que vous n'allez pas dans le mur ou que j'ai pas oublié une exigence, le kata vient avec une serie de tests unitaires [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) qui sert de spécification exécutable (Si vous vous posez la question de si j'ai fait du TDD pour créer les tests et l'implantation, la réponse est non, comme pas mal de monde, j'itère sur le code et les tests en parallèle :) ).

Si vous trouvez qu'il manque un test, vous voulez corriger quelque chose, j'attends vos pull requests.

[Démarrer le kata !](kata.md)

Bon kata !
