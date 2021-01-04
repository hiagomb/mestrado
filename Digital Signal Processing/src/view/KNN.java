/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jfree.data.time.Hour;

/**
 *
 * @author hiago
 */
public class KNN {
    
    
    
    public void classify(int K, double[][] train, double[][] test, int classes){
        System.out.println("Classificação KNN");
        double accuracy= 0;
        
        for(int i=0; i<test.length; i++){
            int[] labels= new int[train.length];
            double[] distances= new double[train.length]; //the label and the distances
            int[] result= new int[classes]; init_result(result);
            for(int j=0; j<train.length; j++){
                labels[j]= (int) train[j][train[0].length-1];
                distances[j]= euclidian_distance(test[i], train[j]);
            }
            System.out.println(Arrays.toString(labels));
            System.out.println(Arrays.toString(distances));
            for(int k=0; k<K; k++){
                int r= label_min(labels, distances);
                result[r]+= 1;
                System.out.println("result["+r+"]= "+result[r]);
            }
            int classification= -1;
            int max_votes= Integer.MIN_VALUE;
            for(int c=0; c<classes; c++){
                if(result[c] > max_votes){
                    max_votes= result[c];
                    classification= c;
                }
            }
            //comparing the result of the classificatio with the label
            System.out.println("classification= "+classification);
            System.out.println("label correto= "+test[i][test[0].length-1]);
            if(classification == test[i][test[0].length-1]){
                System.out.println("ACERTO");
                accuracy+= 1;
            }else{
                System.out.println("ERRO");
            }
        }
        System.out.println("acurácia = "+accuracy/test.length);        
    }
    
    public void init_result(int[] result){
        for(int i=0; i<result.length; i++){
            result[i]= 0;
        }
    }
    
    public double euclidian_distance(double[] test_vector, double[] train_vector){
        double dist= 0;
        int size= test_vector.length-1;  //less the label
        for(int j=0; j<size; j++){
            dist+= Math.pow((test_vector[j] - train_vector[j]) , 2);
        }
        return Math.sqrt(dist);
    }
    
    public int label_min(int[] labels, double[] distances){
        double minor= Double.MAX_VALUE;
        int index= -1; int aux= -1;
        for(int i=0; i<distances.length; i++){
            if(distances[i] < minor){
                minor= distances[i];
                index= labels[i];
                aux= i;
            }
        }
        distances[aux]= Double.MAX_VALUE; //a trick for not getting the same value
        return index;
    }
    
    public static void main(String[] args) {

    
    }
    
}
