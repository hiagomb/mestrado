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
public class Zero_Crossing_Rate {
    
    private double[] zcr_measures;

    public Zero_Crossing_Rate() {
    }

    public Zero_Crossing_Rate(double[] zcr_measures) {
        this.zcr_measures = zcr_measures;
    }

    public double[] getZcr_measures() {
        return zcr_measures;
    }

    public void setZcr_measures(double[] zcr_measures) {
        this.zcr_measures = zcr_measures;
    }
    
    public void extract_ZCR(double[] signal, Zero_Crossing_Rate obj, int length_windows, int overlap_percent){
        int length_signal= signal.length;
        int T= (int) (100*length_signal - length_windows*overlap_percent) / ((100-overlap_percent) * length_windows);
        double[] zcrs= new double[T];
               
        for(int i=0; i<T; i++){
            zcrs[i]= 0;
            for(int j= i*((int)(((100-overlap_percent)/100.0f)*length_windows)); j< i*((int)(((100-overlap_percent)/100.0f)*length_windows))+length_windows-1; j++){
                zcrs[i]+= Math.abs(((algb_sig(signal[j+1])) - (algb_sig(signal[j]))) );
            }
            zcrs[i]/= 2*length_windows;
        }
        obj.setZcr_measures(Statistical_Measures.extract_statical_measures(zcrs));
    }
    
    public int algb_sig(double s){
        if(s>=0){
            return 1;
        }else{
            return -1;
        }
    }
    
    
//    public static void main(String[] args) {
//        double[] signal_teste= new double[11];
//        signal_teste[0]= 1;
//        signal_teste[1]= -2;
//        signal_teste[2]= 3;
//        signal_teste[3]= 4;
//        signal_teste[4]= -2;
//        signal_teste[5]= -6;
//        signal_teste[6]= 7;
//        signal_teste[7]= -1;
//        signal_teste[8]= 9;
//        signal_teste[9]= 9;
//        signal_teste[10]= 9;
//        
//        Zero_Crossing_Rate obj= new Zero_Crossing_Rate();
//        obj.extract_ZCR(signal_teste, obj, 2, 50);
//        
//        System.out.println("Medidas");
//        for(int i=0; i<obj.getZcr_measures().length; i++){
//            System.out.println(obj.getZcr_measures()[i]);
//        }
//    }
    
}
