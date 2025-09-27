import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;

public class Polynomial{
    double[] coefficients;
    int[] exponents;

    static final double EPS = 1e-12;
    
    public Polynomial(){
        this.coefficients = new double[]{0.0};
        this.exponents = new int[]{0};
    }

    // Constructor for internal use only
    private Polynomial(double[] coefficients, int[] exponents, boolean dummy){
        this.coefficients = coefficients;
        this.exponents = exponents;
    }

    public Polynomial(double[] coefficients, int[] exponents){
        this.coefficients = coefficients;
        this.exponents = exponents;
        // Combine like terms and sort
        Polynomial combined = this.combineLikeTerms();
        this.coefficients = combined.coefficients;
        this.exponents = combined.exponents;
    }

    public Polynomial(File file) throws Exception{
        Scanner scanner = new Scanner(file);
        ArrayList<Double> coeffs = new ArrayList<>();
        ArrayList<Integer> exps = new ArrayList<>();
        String content = scanner.nextLine();
        scanner.close();
        int start = 0;
        ArrayList<String> terms = new ArrayList<>();
        for(int i =0; i < content.length(); i++){
            if(content.charAt(i) == '+' || content.charAt(i) == '-'){
                if(i != 0){
                    terms.add(content.substring(start, i));
                    start = i;
                }
            }
        }
        terms.add(content.substring(start));
        for(String term : terms){
            double c; int e;
            String[] parts = term.split("x", -1);
            if(parts.length == 1){
                // Constant term
                c = Double.parseDouble(parts[0]);
                e = 0;
            } else {
                // parts[0] is the coefficient (could be "", "+", "-" meaning ±1)
                if(parts[0].equals("") || parts[0].equals("+")) c = 1.0;
                else if(parts[0].equals("-")) c = -1.0;
                else c = Double.parseDouble(parts[0]);

                // parts[1] is the exponent (could be "" meaning 1)
                if(parts[1].equals("")) e = 1;
                else e = Integer.parseInt(parts[1]);
            }
            coeffs.add(c);
            exps.add(e);
        }
        this.coefficients = new double[coeffs.size()];
        this.exponents = new int[exps.size()];
        for(int i = 0; i < coeffs.size(); i++){
            this.coefficients[i] = coeffs.get(i);
            this.exponents[i] = exps.get(i);
        }
        // Combine like terms and sort
        Polynomial combined = this.combineLikeTerms();
        this.coefficients = combined.coefficients;
        this.exponents = combined.exponents;
    }

    public Polynomial add(Polynomial other){
        int mx = this.coefficients.length + other.coefficients.length;
        double[] res_coeffs = new double[mx];
        int[] res_exps = new int[mx];
        Polynomial sorted = this.sort();
        Polynomial other_sorted = other.sort();
        int i = 0, j = 0, k = 0;
        while(i < sorted.coefficients.length || j < other_sorted.coefficients.length){
            if(i < sorted.coefficients.length && (j >= other_sorted.coefficients.length || sorted.exponents[i] < other_sorted.exponents[j])){
                res_coeffs[k] = sorted.coefficients[i];
                res_exps[k] = sorted.exponents[i];
                i++;
            } else if(j < other_sorted.coefficients.length && (i >= sorted.coefficients.length || sorted.exponents[i] > other_sorted.exponents[j])){
                res_coeffs[k] = other_sorted.coefficients[j];
                res_exps[k] = other_sorted.exponents[j];
                j++;
            } else{
                res_coeffs[k] = sorted.coefficients[i] + other_sorted.coefficients[j];
                res_exps[k] = sorted.exponents[i];
                i++;
                j++;
            }
            k++;
        }
        // Combine like terms and remove zero coefficients
        return new Polynomial(Arrays.copyOf(res_coeffs, k), Arrays.copyOf(res_exps, k), true).combineLikeTerms();
    }

    public double evaluate(double x){
        double res = 0.0;
        for(int i = 0; i < this.coefficients.length; i++) res += this.coefficients[i] * Math.pow(x, this.exponents[i]);
        return res;
    }

    public boolean hasRoot(double x){
        return Math.abs(evaluate(x)) <= EPS;
    }

    public Polynomial sort(){
        // Sort by exponents in ascending order with bubble sort
        Polynomial sorted = new Polynomial(this.coefficients.clone(), this.exponents.clone(), true);
        for(int i = 0; i < sorted.exponents.length - 1; i++){
            for(int j = i + 1; j < sorted.exponents.length; j++){
                if(sorted.exponents[i] > sorted.exponents[j]){
                    // Swap exponents
                    int tempExp = sorted.exponents[i];
                    sorted.exponents[i] = sorted.exponents[j];
                    sorted.exponents[j] = tempExp;
                    // Swap coefficients
                    double tempCoeff = sorted.coefficients[i];
                    sorted.coefficients[i] = sorted.coefficients[j];
                    sorted.coefficients[j] = tempCoeff;
                }
            }
        }
        return sorted;
    }

    public Polynomial combineLikeTerms(){   
        // Combine terms with the same exponent, and remove zero coefficients, and sorts exponents
        Polynomial sorted = this.sort();
        double[] res_coeffs = new double[sorted.coefficients.length];
        int[] res_exps = new int[sorted.exponents.length];
        int k = 0;
        for(int i = 0; i < sorted.coefficients.length; i++){
            res_coeffs[k] = sorted.coefficients[i];
            res_exps[k] = sorted.exponents[i];
            while(i < sorted.coefficients.length - 1 && sorted.exponents[i] == sorted.exponents[i + 1]){
                res_coeffs[k] += sorted.coefficients[i + 1];
                i++;
            }
            if (Math.abs(res_coeffs[k]) > EPS) k++; // No zero coefficients
        }
        // If all coefficients are zero
        if(k==0) return new Polynomial(new double[]{0.0}, new int[]{0}, true);
        // Return only the non-zero part
        return new Polynomial(Arrays.copyOf(res_coeffs, k), Arrays.copyOf(res_exps, k), true);
    }

    public Polynomial multiply(Polynomial other){
        int mx = this.coefficients.length * other.coefficients.length;
        double[] res_coeffs = new double[mx];
        int[] res_exps = new int[mx];
        int k = 0;
        for(int i = 0; i < this.coefficients.length; i++){
            for(int j = 0; j < other.coefficients.length; j++){
                res_coeffs[k] = this.coefficients[i] * other.coefficients[j];
                res_exps[k] = this.exponents[i] + other.exponents[j];
                k++;
            }
        }
        return new Polynomial(Arrays.copyOf(res_coeffs, k), Arrays.copyOf(res_exps, k), true).combineLikeTerms();
    }

    public void saveToFile(String filename) throws Exception{
        FileWriter writer = new FileWriter(filename);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.coefficients.length; i++){
            double a = this.coefficients[i];
            int e = this.exponents[i];
            if (Math.abs(a) <= EPS) continue;

            // sign of first term
            if (sb.length() == 0) {
                if (a < 0) sb.append("-");
            }
            // sign of rest
            else {
                if (a < 0) sb.append("-");
                else sb.append("+");
            }
            double absA = Math.abs(a);
            // if absA is an integer, print as integer
            if (Math.rint(absA) == absA) {
                if (absA == 1.0 && e != 0) {
                    // skip printing coefficient 1 or -1 if exponent is not 0
                } else sb.append((long)Math.rint(absA));
            }
            // else print as double
            else sb.append(absA);

            // exponent formatting
            if (e != 0) {
                sb.append("x");
                if (e != 1) sb.append(e);
            }
        }
        if (sb.length() == 0) sb.append("0");
        writer.write(sb.toString());
        writer.close();
    }
}