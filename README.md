# Springboot REST Intro

In dieser Übung wird ein einfacher REST-Server in Springboot implementiert.

## Ziele

1. Die Grundlagen von Springboot dependency injection zu verstehen, besonders die Annotationen
   `@SpringBootApplication`, `@RestController`, `@RequestMapping` and `@GetMapping`.
2. Wissen wie ich REST-Services debuggen kann mit Tools wie `IntelliJ HTTP Client`, `curl` oder Postman.
3. Elemente von HTTP verstehen: Methode, Pfad, Status-Code, Headers und Body.

## Aufgaben

1. Erstelle einen neuen `MovieController`, als `@RestController`, lies dazu:
   https://www.baeldung.com/spring-controller-vs-restcontroller#spring-mvc-rest-controller
2. Erstelle Schritt für Schritt neue Methoden, entsprechend den Tests/Tipps in `RestintroApplicationTests`. Die Test-Klasse sollte nicht verändert werden und am Schluss sind alle Tests grün.
3. Schau das `sample.http` file an. IntelliJ besitzt einen built-in HTTP Client ähnlich zu Postman. Schreibe tests zu allen REST Endpoints in diesem .http Format.

## Weiterführendes

- https://www.youtube.com/watch?v=9SGDpanrc8U gibt eine eher lange, aber schonend Einführung in Springboot, geht aber mit JPA/Datenbanken weiter als diese Übung.
