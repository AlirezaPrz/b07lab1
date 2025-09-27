import java.io.File;

public class Driver {
    public static void main(String[] args) throws Exception {
        Polynomial p = new Polynomial();
        System.out.println(p.evaluate(3));
        double[] c1 = {6.1, 0, 5};
        int[] e1 = {1, 0, 2};
        Polynomial p1 = new Polynomial(c1, e1);
        double[] c2 = {1, 0, 0, -9};
        int[] e2 = {1, 3, 4, 0}; 
        Polynomial p2 = new Polynomial(c2, e2);
        Polynomial s = p1.add(p2);
        System.out.println("s(0.1) = " + s.evaluate(0.1));
        if (s.hasRoot(1))
            System.out.println("1 is a root of s");
        else
            System.out.println("1 is not a root of s");

        p1.saveToFile("p1.txt");
        p2.saveToFile("p2.txt");
        Polynomial m = p1.multiply(p2);
        m.saveToFile("multip.txt");
        File file = new File("input.txt");
        Polynomial fromFile = new Polynomial(file);
        System.out.println("fromFile(2) = " + fromFile.evaluate(2));
        fromFile.saveToFile("fromFile.txt");
    }
}