# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

### TCC vs LCC : Quand produisent-ils la même valeur ?

**Tight Class Cohesion (TCC)** et **Loose Class Cohesion (LCC)** sont deux métriques qui mesurent la cohésion d'une classe en programmation orientée objet. Ces deux métriques analysent les connexions entre les méthodes d'une classe en fonction des variables d'instance partagées, mais diffèrent dans leur portée de connectivité :

- **TCC** mesure la **connectivité directe** entre les paires de méthodes (les arêtes immédiates dans le graphe des méthodes).
- **LCC** prend en compte la **connectivité directe et indirecte**, où deux méthodes sont connectées si elles partagent un chemin via d'autres méthodes intermédiaires.

#### Quand TCC et LCC produisent-ils la même valeur ?

TCC et LCC produiront la même valeur lorsque **toutes les méthodes connectées dans la classe sont directement connectées**. Cela signifie qu'il n'existe aucune connexion indirecte. Ce cas se produit lorsque le graphe des méthodes est un **graphe complet**, où chaque paire de méthodes possède une connexion directe.

---

### Exemple d'une telle classe

Voici un exemple en Java où TCC = LCC, car toutes les méthodes utilisent directement la même variable d'instance, formant ainsi un graphe totalement connecté :

```java
public class ExempleClasse {
 private int variablePartagee; 

 public void methodeA() {
 variablePartagee += 1;
 }

 public void methodeB() {
 variablePartagee *= 2;
 }

 public void methodeC() {
 variablePartagee -= 3;
 }
}
```


---

### LCC peut-il être inférieur à TCC ?

Non, **LCC ne peut jamais être inférieur à TCC** pour une classe donnée. Par définition :
- **TCC** ne prend en compte que les connexions directes.
- **LCC** inclut les connexions directes **et** indirectes.

Ainsi, LCC est toujours égal ou supérieur à TCC. L'égalité entre les deux métriques se produit uniquement lorsque toutes les connexions sont directes.

---

### Exemple Open-Source

Un exemple open-source d'une classe où **TCC = LCC** peut être trouvé [ici](https://github.com/iluwatar/java-design-patterns/blob/master/factory-method/src/main/java/com/iluwatar/factory/method/Weapon.java) (dans le dépôt **Java Design Patterns**). 
Cette classe `Weapon` a une seule variable d'instance, et toutes ses méthodes interagissent avec elle, formant un graphe totalement connecté.

---

### Conclusion

TCC et LCC sont égaux lorsque toutes les paires de méthodes d'une classe partagent des connexions directes. Cela se produit généralement dans des classes simples ou fortement couplées. Cependant, LCC est toujours au moins égal à TCC, car la prise en compte des connexions indirectes élargit sa portée. 


