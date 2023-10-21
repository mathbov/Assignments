import java.lang.*;

public class MasterTheorem{
	public double a;
	public double b;
	public double c;
	public double e;
	public double exponent;

	public String MasterTheorem(double a, double b, double c,double  e, double exponent){
		
		double lnA = Math.log(a);
		double lnB = Math.log(b);
		double alpha = lnA/lnB;
		System.out.println("alpha = " + alpha);

		if ( alpha-e > exponent){
			String alphaString = Double.toString(alpha);
			return "result: n^" + alphaString;
		}

		if ( alpha == exponent){
			String alphaString = Double.toString(alpha);
			return "result: n^" + alphaString + " * log n";

		}
		if ( alpha+e < exponent ){
			String alphaString = Double.toString(alpha);
			return "result: n^" + exponent;
		}

		else{
			return "Not found";
		}
	}

	public static void main(String[] args){
		var instance = new MasterTheorem();

		System.out.println("a = 3, b = 3, f(n) = n^2");
		System.out.println();
		System.out.println(instance.MasterTheorem(3,3,0.1,0.001, 2)); 
		System.out.println();
		System.out.println("a = 2, b = 2, f(n) = n^3");
		System.out.println();
		System.out.println(instance.MasterTheorem(2,2,0.1,0.001, 3));
		System.out.println();	
		System.out.println("a = 4, b = 2, f(n) = n^2");
		System.out.println(instance.MasterTheorem(4,2,0.1,0.001, 2));
		System.out.println();
		System.out.println("a = 2, b = 4, f(n) = n^1");
		System.out.println();
		System.out.println(instance.MasterTheorem(2,4,0.1,0.001, 1));
		System.out.println();
		System.out.println("a = 4, b = 3, f(n) = n^1");
		System.out.println();
		System.out.println(instance.MasterTheorem(4,3,0.1,0.001, 1));
	}
}