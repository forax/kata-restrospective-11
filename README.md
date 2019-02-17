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

L'idée de ce kata est d'implémenter un [Lexer](https://en.wikipedia.org/wiki/Lexer) capable de transformer une chaîne de caractères en tokens, c-a-d un mélange d'identifiants, de mot-clés, de valeurs numériques, etc.
Pour reconnaître si une chaîne de caractères est un des tokens définis, on utilisera des expressions régulières. 
Le but de ce kata est plus de se focaliser sur l'API que sur l'implémentation en elle même, cela tombe bien en Java, le package java.util.regex nous enlèves le poids d'avoir à ré-implémenter la gestion des expressions régulières.

Voilà une idée de l'API que l'on veut obtenir
```java
  var lexer = Lexer.create()
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble)
        .with("([a-zA-Z]+)",       Function.identity());
  System.out.println(lexer.tryParse("foo").orElseThrow());   // affiche la chaine foo
  System.out.println(lexer.tryParse("12.3").orElseThrow());  // affiche la valeur flottante 12.3
  System.out.println(lexer.tryParse("200").orElseThrow());   // affiche la valeur entière 200
```

Le kata est en deux parties, dans un premier temps, on va bâtir une API fonctionnelle permettant de faire fonctionner le code ci-dessus. 
Dans un second temps, on va poser la question de comment rendre le code un peu plus efficace, en conservant la même API.

De plus, pour garantir que vous n'allez pas dans le mur ou que je n'ai pas oublié une exigence, 
le kata vient avec une série de tests unitaires [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) qui sert de spécification exécutable (Si vous vous posez la question de si j'ai fait du TDD pour créer les tests et l'implémentation, la réponse est non, comme pas mal de monde, j'itère sur le code et les tests en parallèle :) ).

Si vous trouvez qu'il manque un test, vous voulez corriger quelque chose, j'attends vos pull requests.

[Démarrer le kata](kata.md)

[Démarrer la partie 2](kata-part2.md)

Bon kata !

## Construction de projet avec Apache Maven

Le projet contient un `pom.xml` vous permettant de construire le projet et de faire les tests au fur et à mesure.

- `mvn clean install`

Si vous n'avez pas [maven](http://maven.apache.org/) sur votre poste vous pouvez utiliser le wrapper fourni.

- `mvnw clean install`

Les différentes étapes du kata, et l'activation de leur tests associés se fait au travers de l'utilisation de profiles.

- `mvn clean install -PQ1`: construit le projet et joue les tests 'taggués' _Q1_ 
- `mvn clean install -PQ5`: construit le projet et joue les tests 'taggués' _Q1,Q2,Q3,Q4,Q5_ 
  
  
  
  
