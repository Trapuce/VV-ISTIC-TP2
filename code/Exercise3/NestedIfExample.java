// NestedIfExample.java
public class NestedIfExample {
    public void methodWithNestedIf(int a, int b, boolean flag) {
        // Ce code devrait déclencher la règle
        if (a > 0) {
            System.out.println("Premier niveau");
            if (b < 10) {
                System.out.println("Deuxième niveau");
                if (flag) {
                    System.out.println("Troisième niveau - Violation!");
                }
            }
        }

        // Ce code ne devrait pas déclencher la règle
        if (a > 0) {
            System.out.println("Premier niveau");
            if (b < 10) {
                System.out.println("Deuxième niveau seulement");
            }
        }
    }
}