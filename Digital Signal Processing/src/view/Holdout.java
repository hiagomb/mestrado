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

/**
 *
 * @author hiago
 */
public class Holdout {
    
    private double[][] train;
    private double[][] train_target;
    private double[][] test;
    private double[][] test_target;

    public Holdout() {
    }

    public Holdout(double[][] train, double[][] train_target, double[][] test, double[][] test_target) {
        this.train = train;
        this.train_target = train_target;
        this.test = test;
        this.test_target = test_target;
    }

    public double[][] getTrain() {
        return train;
    }

    public void setTrain(double[][] train) {
        this.train = train;
    }

    public double[][] getTrain_target() {
        return train_target;
    }

    public void setTrain_target(double[][] train_target) {
        this.train_target = train_target;
    }

    public double[][] getTest() {
        return test;
    }

    public void setTest(double[][] test) {
        this.test = test;
    }

    public double[][] getTest_target() {
        return test_target;
    }

    public void setTest_target(double[][] test_target) {
        this.test_target = test_target;
    }
    
    public void holdout(double[][] feature_matrix, Holdout obj, double training_percentage, int classes){ //passing as a float:. 70% = 0.70
        int amount= feature_matrix.length;
        List<Integer> l= new ArrayList<>();
        for(int i=0; i<amount; i++){
            l.add(i);
        }
        Collections.shuffle(l);
        
        int train_amount= (int) (training_percentage * amount);
        double[][] train= new double[train_amount][feature_matrix[0].length];
        double[][] train_target= new double[train_amount][classes]; 
        double[][] test= new double[amount - train_amount][feature_matrix[0].length];
        double[][] test_target= new double[amount - train_amount][classes];
        
        for(int i=0; i<amount; i++){
            if(i< train_amount){
                train[i]= feature_matrix[l.get(i)];
                train_target[i]= configure_targets(train[i], classes);
            }else{
                test[i-train_amount]= feature_matrix[l.get(i)];
                test_target[i-train_amount]= configure_targets(test[i-train_amount], classes);
            }
        }
        obj.setTrain(train);
        obj.setTrain_target(train_target);
        obj.setTest(test);
        obj.setTest_target(test_target);
    }
    
     public double[] configure_targets(double[] feature_vector, int classes){
        int label= (int)feature_vector[feature_vector.length-1];
        double[] target= new double[classes];
        for(int i=0; i<classes; i++){
            if(i== label){
                target[i]= 1;
            }else{
                target[i]= 0;
            }
        }
        return target;
    }
     
     
//     public static void main(String[] args) {
//        double[][] m= new double[20][5];
//        for(int i=0; i<20; i++){
//            for(int j=0; j<5; j++){
//                m[i][j]= i;
//            }
//            m[i][m[0].length-1]= 2;
//            m[0][m[0].length-1]= 5;
//            System.out.println(Arrays.toString(m[i]));
//        }
//        Holdout obj= new Holdout();
//        obj.holdout(m, obj, 0.73, 7);
//        
//        System.out.println("TREINO");
//        for(int i=0; i<obj.getTrain().length; i++){
//            System.out.print(Arrays.toString(obj.getTrain()[i]));
//            System.out.println("  || "+Arrays.toString(obj.getTrain_target()[i]));
//        }
//         System.out.println("");
//        System.out.println("TESTE");
//        for(int i=0; i<obj.getTest().length; i++){
//            System.out.print(Arrays.toString(obj.getTest()[i]));
//            System.out.println("  || "+Arrays.toString(obj.getTest_target()[i]));
//        }
//       
//    }
    
}
