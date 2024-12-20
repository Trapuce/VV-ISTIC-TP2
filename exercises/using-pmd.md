# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer
**Projet** Apache Commons CLI
### **Problème identifié par PMD - True Positive**
**Type de problème** : `CloseResource`  
**Description** : PMD a détecté qu'une ressource, comme un `PrintWriter` ou un `BufferedReader`, n'est pas correctement fermée après son utilisation. Cela peut entraîner des fuites de ressources, notamment dans des applications volumineuses ou à long terme.

**Exemple de code (Avant)** :
```java
PrintWriter writer = new PrintWriter(System.out);
writer.println("Some output");

```
Pour résoudre ce problème, la ressource doit être gérée à l'aide d'un bloc try-with-resources pour garantir sa fermeture correcte.

```java
try (PrintWriter writer = new PrintWriter(System.out)) {
    writer.println("Some output");
}
```


### **Pourquoi résoudre ce problème ?**
Laisser des ressources ouvertes peut entraîner des fuites de mémoire ou des verrous de fichiers, ce qui dégrade les performances et la fiabilité de l'application. Corriger ce problème garantit une meilleure gestion des ressources et le respect des bonnes pratiques en Java.


### **Problème identifié par PMD - False Positive**
**Type de problème** : UnnecessaryConstructor

**Description** : PMD a signalé un constructeur comme étant inutile parce qu'il ne contient aucune logique, et que le compilateur Java générerait un constructeur par défaut en son absence.


Bien que techniquement ce constructeur soit inutile, il peut être intentionnellement laissé dans le code pour améliorer la lisibilité ou servir de point de départ pour des améliorations futures. Supprimer de tels constructeurs n'apporte pas de bénéfices significatifs et pourrait réduire la clarté du code pour les futurs développeurs.