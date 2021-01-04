/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_extraction;

/**
 *
 * @author hiago
 */
public class Teager_Energy_Operator {
    
    private double[] teo_vector_measures;
    
    public Teager_Energy_Operator(){
    }
    
    public Teager_Energy_Operator(double[] teo_vector_measures){
        this.teo_vector_measures= teo_vector_measures;
    }

    public double[] getTeo_vector_measures() {
        return teo_vector_measures;
    }

    public void setTeo_vector_measures(double[] teo_vector_measures) {
        this.teo_vector_measures = teo_vector_measures;
    }
    
    
    
    
    
    public void extract_TEOVectorMeasures(double[] signal, Teager_Energy_Operator obj){
        int lenght= signal.length;
        double[] vector= new double[lenght-2];
        
        for(int i=1; i<lenght-1; i++){
            vector[i-1]= (Math.pow(signal[i], 2)) - (signal[i-1]*signal[i+1]);
        }
        
        //now I need to extract some measures from the TEO vector:
        obj.setTeo_vector_measures(Statistical_Measures.extract_statical_measures(vector));     
    }
  
    
//    public static void main(String[] args) {
//        
//        Teager_Energy_Operator obj_teste= new Teager_Energy_Operator();
//        
//        double[] signal_teste= {20, 10, 2, 8, 12, 1};
//        
//        obj_teste.extract_TEOVectorMeasures(signal_teste ,obj_teste);
//        
//        for(int i=0; i<obj_teste.getTeo_vector_measures().length; i++){
//            System.out.print("["+obj_teste.getTeo_vector_measures()[i]+"]");
//        }
//    }
    
}
