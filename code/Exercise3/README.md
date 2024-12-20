# Code of your exercise

## Contexte

Dans l'exercice, nous avons utilisé la règle PMD `AvoidDeeplyNestedIfStmts` pour détecter des cas où des instructions `if` sont imbriquées de manière excessive dans le code, ce qui rend le code difficile à lire et à maintenir. La règle a été définie dans un fichier de règles personnalisées pour identifier les zones du code où des structures `if` profondément imbriquées apparaissent.

Cette règle permet de détecter ces situations problématiques et ainsi de suggérer des améliorations pour simplifier la logique du code.

## Problème détecté

La règle PMD `AvoidDeeplyNestedIfStmts` a détecté plusieurs cas où des conditions `if` étaient imbriquées les unes dans les autres, rendant le code difficile à suivre. Voici un exemple de code source avec des `if` imbriqués, qui a été signalé par la règle :

### Exemple de code détecté

```java
public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
    if (map != null) {
        final Object answer = map.get(key);
        if (answer != null) {
            if (answer instanceof Boolean) {
                return (Boolean) answer;
            }
            if (answer instanceof String) {
                return Boolean.valueOf((String) answer);
            }
            if (answer instanceof Number) {
                final Number n = (Number) answer;
                return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
        }
    }
    return null;
}

```


## Détection par PMD

La règle `AvoidDeeplyNestedIfStmts` a permis de détecter ces occurrences dans les fichiers suivants du projet Apache Commons Collections :

- `commons-collections/src/main/java/org/apache/commons/collections4/MapUtils.java:230`
- `commons-collections/src/main/java/org/apache/commons/collections4/MapUtils.java:233`
- `commons-collections/src/main/java/org/apache/commons/collections4/MapUtils.java:236`
- `commons-collections/src/main/java/org/apache/commons/collections4/MapUtils.java:930`
- `commons-collections/src/main/java/org/apache/commons/collections4/MapUtils.java:933`
- `commons-collections/src/main/java/org/apache/commons/collections4/map/CompositeMap.java:199`
- `commons-collections/src/main/java/org/apache/commons/collections4/set/CompositeSet.java:197`
- `commons-collections/src/main/java/org/apache/commons/collections4/set/CompositeSet.java:202`

## Règle PMD

La règle utilisée pour détecter les imbrications excessives d'instructions if est définie comme suit dans le fichier XML :

```xml
<?xml version="1.0"?>
<ruleset name="Règles personnalisées"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.net/ruleset_2_0_0.xsd">
    
    <description>
        Règle pour identifier les instructions if imbriquées de manière excessive.
    </description>

    <rule name="ImbricationExcessiveIf"
          language="java"
          message="Imbrication excessive d'instructions if détectée (profondeur >= 3). Cela complique la lisibilité du code. Envisagez une refactorisation."
          class="net.sourceforge.pmd.lang.rule.RuleReference">
        
        <description>
            Cette règle détecte les instructions if imbriquées à trois niveaux ou plus.
            L'imbrication excessive peut rendre le code difficile à comprendre et à maintenir. Considérez l'utilisation de clauses de garde, l'extraction de méthodes ou des alternatives comme le polymorphisme.
        </description>
        
        <priority>3</priority>
        
        <properties>
            <property name="xpath">
                <value>
                    <![CDATA[
                    //IfStatement[
                        ancestor::IfStatement[
                            ancestor::IfStatement
                        ]
                    ]
                    ]]>
                </value>
            </property>
        </properties>
    </rule>
</ruleset>

```