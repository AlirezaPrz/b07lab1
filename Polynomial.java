public class Polynomial{
    double[] coefficients;
    
    public Polynomial(){
        this.coefficients = new double[]{0.0};
    }

    public Polynomial(double[] coefficients){
        this.coefficients = coefficients;
    }

    public Polynomial add(Polynomial other){
        int mx = Math.max(this.coefficients.length, other.coefficients.length);
        double[] res = new double[mx];

        for(int i = 0; i < mx; i++){
            res[i] = 0.0;
            if(i < other.coefficients.length) res[i] += other.coefficients[i];
            if(i < this.coefficients.length) res[i] += this.coefficients[i];
        }
        
        return new Polynomial(res);
    }

    public double evaluate(double x){
        double res = 0.0;
        double curr = 1.0;
        for(int i = 0; i < this.coefficients.length; i++){
            res += this.coefficients[i] * curr;
            curr *= x;
        }
        return res;
    }

    public boolean hasRoot(double x){
        return Math.abs(evaluate(x)) < 1e-9;
    }
}