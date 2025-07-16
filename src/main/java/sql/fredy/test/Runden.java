package sql.fredy.test;


public class Runden {


    double einheit = 0.05000;
    
    /**
     * Get the value of einheit.
     * @return value of einheit.
     */
    public double getEinheit() {
	return einheit;
    }
    
    /**
     * Set the value of einheit.
     * @param v  Value to assign to einheit.
     */
    public void setEinheit(double  v) {
	this.einheit = v;
    }
    
    double zahl;
    
    /**
     * Get the value of zahl.
     * @return value of zahl.
     */
    public double getZahl() {
	return zahl;
    }
    
    /**
     * Set the value of zahl.
     * @param v  Value to assign to zahl.
     */
    public void setZahl(double  v) {
	double z1 = v - java.lang.Math.floor(v);
        zahl = java.lang.Math.floor(v);
        double ergebnis = einheit * java.lang.Math.floor( (z1 + (einheit/2)) / einheit);
        zahl = ergebnis + zahl;  
        
    }
    



    public Runden (double zahl) {

	setZahl(zahl);
    }

    public static void main(String args[]) {
	if (args.length > 0) {
	    Double in = Double.valueOf(args[0]);
	    Runden r  = new Runden(in);
	    System.out.println(r.getZahl());
	}
    }
}
