/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_extraction;

import java.util.Random;

/**
 *
 * @author hiago
 */
public class Statistical_Measures {
    
    private static final int NUMBER_OF_MEASURES= 106;
    
    public static double[] extract_statical_measures(double[] vector){
        double major= Double.MIN_VALUE, minor= Double.MAX_VALUE, 
                standard_deviation, maj_min_difference, avg, variance;
        
        double[] measure_vector= new double[NUMBER_OF_MEASURES];
         
        
        double sum=0;
        for(int i=0; i<vector.length; i++){
            if(vector[i]> major){
                major= vector[i];
            }
            if(vector[i]< minor){
                minor= vector[i];
            }
            sum+=vector[i];
        }
        avg= sum/vector.length;
        maj_min_difference= major - minor;
        
        //calculating the variance and standard deviation
        sum=0;
        for(int i=0; i<vector.length; i++){
            sum+= Math.pow((vector[i] - avg), 2);
        }
        variance= sum/vector.length;
        standard_deviation= Math.sqrt(variance);
        
        //---quantiles
        double[] ordered_vector= vector;  
        quick_sort(ordered_vector, 0, ordered_vector.length-1); 

        double[] quantiles= new double[100]; //from 0% to 99%
        double rate= 0.00; 
        if(ordered_vector.length<100){ //teste -- mudei aqui
            System.out.println("tamanho: "+ordered_vector.length);
            throw new IllegalArgumentException("O vetor deve conter ao menos 100 elementos");
        }

        for(int i=0; i<quantiles.length; i++){
            quantiles[i]= rate * ordered_vector.length;  
            quantiles[i]= ordered_vector[(int)Math.round(quantiles[i])];
            rate+= 0.01;
            measure_vector[i]= quantiles[i];
        }
        //-------------- 

        measure_vector[quantiles.length]= major;
        measure_vector[quantiles.length+1]= minor;
        measure_vector[quantiles.length+2]= standard_deviation;
        measure_vector[quantiles.length+3]= maj_min_difference;
        measure_vector[quantiles.length+4]= avg;
        measure_vector[quantiles.length+5]= variance;      
        
        return measure_vector;
    }
    
    public static void quick_sort(double[] vector, int start, int end){
        if(start< end){
            int i= start, j= end, direction=1;
            double aux;
            while(i<j){
                if(vector[i]> vector[j]){
                    aux= vector[j];
                    vector[j]= vector[i];
                    vector[i]= aux;
                    direction= -direction;
                }
                if(direction==1){
                    j--;
                }else{
                    i++;
                }
            } 
            quick_sort(vector, start, i);
            quick_sort(vector, i+1, end);
        }
    }
    
    
    
    
//    public static void main(String[] args) {
//        double[] vector= new double[20];
//        Random r= new Random();
//        System.out.println("Vetor Original: ");
//        for(int i=0; i<vector.length; i++){
//            vector[i]= r.nextDouble()*100;
//            System.out.println("["+vector[i]+"]");
//        }
//        
//        
//        double[] resultado= Statistical_Measures.extract_statical_measures(vector);
//        System.out.println("");
//        System.out.println("Resultados de medida: ");
//        for(int i=0; i<resultado.length; i++){
//            System.out.println("["+resultado[i]+"]");
//        }
//        
//    }
    
    
}
