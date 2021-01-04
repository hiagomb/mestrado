//A1 generate different feature vectors lenght depending on the signal lenght, so, statistical measures
//are extracted from the energy vector
//A2 and A3 generate fixed lenght energy vectors
package feature_extraction;

import java.math.BigDecimal;

/**
 *
 * @author hiago
 */
public class Energy_Features {
    
    private double[] energy_vector; //the generated energy vector according to A1, A2 or A3 approach

    public Energy_Features() {
    }

    public Energy_Features(double[] energy_vector) {
        this.energy_vector = energy_vector;
    }

    public double[] getEnergy_vector() {
        return energy_vector;
    }

    public void setEnergy_vector(double[] energy_vector) {
        this.energy_vector = energy_vector;
    }
    
    public void extraction_A1(double[] signal, int length_windows, int overlap_percent, Energy_Features obj){
        
        int length_signal= signal.length;
        int T= (int) (100*length_signal - length_windows*overlap_percent) / ((100-overlap_percent) * length_windows);
        double[] generated_energy_vector= new double[T];
        double energy_sum= 0; //this is necessary for normalizing each position
               
        for(int i=0; i<T; i++){
            generated_energy_vector[i]=0;
            for(int j= i*((int)(((100-overlap_percent)/100.0f)*length_windows)); j< i*((int)(((100-overlap_percent)/100.0f)*length_windows))+length_windows; j++){
                generated_energy_vector[i]+= Math.pow(signal[j], 2);
            }
            energy_sum+= generated_energy_vector[i];
        }  
        for(int i=0; i<T; i++){ //normalizing the values
            generated_energy_vector[i]/=energy_sum;
        } 
        obj.setEnergy_vector(Statistical_Measures.extract_statical_measures(generated_energy_vector));
    }
    
    public void extraction_A2(double[] signal, Energy_Features obj){
        
        int window_length;
        int lenght_signal= signal.length;
        double energy_sum= 0;
        double[] prime_numbers= {2,109}; //here I can change according to the number of subvectors I want
        int total_size_of_f= 0;
        for(int i=0; i< prime_numbers.length; i++){
            total_size_of_f+= prime_numbers[i];
        }
        double[] generated_energy_vector= new double[total_size_of_f];
        int jump= 0;
        for(int i=0; i<prime_numbers.length; i++){
            energy_sum= 0;
            for(int j=0; j<prime_numbers[i]; j++){
                window_length= (int) (lenght_signal/prime_numbers[i]);
                generated_energy_vector[jump+j]= 0;
                for(int l= (j*window_length); l<(j*window_length)+window_length; l++){
                    generated_energy_vector[jump+j]+= Math.pow(signal[l], 2);                    
                }
                energy_sum+=generated_energy_vector[jump+j];
            }
            for(int x=0; x<prime_numbers[i]; x++){
                generated_energy_vector[jump+x]/=energy_sum;
            }
            jump+= prime_numbers[i];
        }
        obj.setEnergy_vector(generated_energy_vector);
    }
    
    public void extraction_A3(double[] signal, double energy_sublevel, Energy_Features obj){
        
        int signal_length= signal.length; 
        int length_of_generated_vector= (int) (100/energy_sublevel-1);
        double[] e_quantities= new double[length_of_generated_vector];
        double[] generated_energy_vector= new double[length_of_generated_vector];
        
        double total_energy= obj.extract_total_energy(signal);
        double energy;
        for(int i=0; i<length_of_generated_vector; i++){//calculating the quantity values -- starts at 1 or +1 into the loop
            e_quantities[i]= (total_energy* ((energy_sublevel*(i+1))/100.0f)); //i+1 because starts at 0
            energy=0;
            for(int j=0; j< signal_length; j++){
                energy+= Math.pow(signal[j], 2);
                if(energy>= e_quantities[i]){
                    generated_energy_vector[i]=  ((double)j+1)/(double)signal_length; //j+1 because starts at 0
                    j= signal_length; //in order to exit the j loop
                }
            }
        }
        obj.setEnergy_vector(generated_energy_vector);  
    }
    
    public double extract_total_energy(double[] signal){
        double energy=0;
        for(int i=0; i<signal.length; i++){
            energy+= Math.pow(signal[i], 2);
        } 
        return energy;
    }
    
    
    
//    public static void main(String[] args) {
//        Energy_Features obj= new Energy_Features();
//        double[] s= new double[10];
//        s[0]=2;
//        s[1]=4;
//        s[2]=6;
//        s[3]=8;
//        s[4]=10;
//        s[5]=9;
//        s[6]=7;
//        s[7]=5;
//        s[8]=3;
//        s[9]=1;
//        
//        obj.extraction_A1(s, 4, 50, obj);
//        int tam= obj.getEnergy_vector().length;
//        for(int i=0; i<tam; i++){
//            System.out.println(obj.getEnergy_vector()[i]);
//        }
//
//        s[0]=0;
//        s[1]=1;
//        s[2]=2;
//        s[3]=3;
//        s[4]=4;
//        s[5]=5;
//        s[6]=6;
//        s[7]=7;
//        s[8]=8;
//        s[9]=9;
//        
//        obj.extraction_A2(s, obj);
//        int tam= obj.getEnergy_vector().length;
//        for(int i=0; i<tam; i++){
//            System.out.println(obj.getEnergy_vector()[i]+" - ");
//        }
        
//        s[0]=1;
//        s[1]=1;
//        s[2]=2;
//        s[3]=7;
//        s[4]=10;
//        s[5]=9;
//        s[6]=8;
//        s[7]=20;
//        
//        obj.extraction_A3(s, 20, obj);
//    
//        int tam= obj.getEnergy_vector().length;
//        for(int i=0; i<tam; i++){
//            System.out.println(obj.getEnergy_vector()[i]);
//        }
//
//    }
      
}
