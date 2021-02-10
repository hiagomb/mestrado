/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_selection;

import analysis.Paraconsistent_Plane;
import java.awt.List;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author hiago
 */
public class Paraconsistent_Based_Selection {
   
    
    //here I perform the analysis made by the PARACONSISTENT FEATURE ENGINEERING for each feature 
    //so I can create a ranking of the best descriptors
    
    public int[] rank_best_features(double[][] feature_matrix, int number_of_classes){
        int number_of_features= (feature_matrix[0].length)-1; //the last colum corresponds to the label of the class
        Paraconsistent_Plane analyser= new Paraconsistent_Plane();
        double[] distances_from_right= new double[number_of_features];//save the distances
        int[] ranking_features= new int[number_of_features]; //rank the features per its index
                
        for(int x=0; x<number_of_features; x++){ //tenho que montar matrizes de duas colunas - caracteristica + label
            double[][] returned_matrix= assemble_matrix(feature_matrix, x, number_of_features);
            analyser.paraconsistent_analyser(returned_matrix, number_of_classes, analyser);
            distances_from_right[x]= analyser.getDistance_from_right();
        }        
        assemble_ranking(distances_from_right, ranking_features);
        return ranking_features;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public double[][] assemble_matrix(double[][] feature_matrix, int index, int number_of_features){
        double[][] matrix= new double[feature_matrix.length][2]; //one feature + label = 2
        
        for(int i=0; i<feature_matrix.length; i++){
            matrix[i][0]= feature_matrix[i][index];//feature
            matrix[i][1]= feature_matrix[i][number_of_features];//label
        }
        return matrix;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void assemble_ranking(double[] distances, int[] ranking){
        int aux= -1;
        
        for(int x=0; x<distances.length; x++){
            double minor= Double.MAX_VALUE;
            for(int i=0; i<distances.length; i++){
                if(distances[i]< minor){
                    minor= distances[i];
                    aux= i;
                }
            }
            //System.out.println("Caracteristica: "+aux+" está a "+minor+" da direita");
            ranking[x]= aux;
            distances[aux]= Double.MAX_VALUE; //for not getting the same value -- a trick
        }  
    }
    
    
    
    public void decBinConversion(BigInteger bi, int[] binary_vector){  //tem que retornar um int[]
        String bin= bi.toString(2); //converts from decimal to binary representation (2)
        int first_position= binary_vector.length - bin.length();
        
        int char_count= 0;
        for(int i=0; i<binary_vector.length; i++){
            if(i< first_position){
                binary_vector[i]= 0;
            }else{
                binary_vector[i]= Integer.parseInt(Character.toString(bin.charAt(char_count)));
                char_count+=1;
            }
        }
    }
    
    
    public Paraconsistent_Plane setUp_and_Test_subMatrix(double[][] feature_matrix, int number_of_features, ArrayList<String> positions, int number_of_classes){
        double[][] subMatrix= new double[feature_matrix.length][number_of_features+1]; //plus label
        
        for(int j=0; j<(number_of_features+1); j++){ //column == each feature
            for(int i=0; i<feature_matrix.length; i++){//line == each vector
                if(j==number_of_features){
                    subMatrix[i][j]= feature_matrix[i][feature_matrix[0].length-1]; //label
                }else{
                    subMatrix[i][j]= feature_matrix[i][Integer.parseInt(positions.get(j))];
                }  
            }
        }//testing the submatrix
        Paraconsistent_Plane pp= new Paraconsistent_Plane();
        pp.paraconsistent_analyser(subMatrix, number_of_classes, pp);
        return pp;
    }
    
//    public static void main(String[] args) {
//        Paraconsistent_Based_Selection obj= new Paraconsistent_Based_Selection();
//
//        double[][] matrix= new double[12][3];
//        
//        matrix[0][0]= 0.90; matrix[0][1]= 0.12; matrix[0][2]= 0;
//        matrix[1][0]= 0.88; matrix[1][1]= 0.14; matrix[1][2]= 0;
//        matrix[2][0]= 0.88; matrix[2][1]= 0.13; matrix[2][2]= 0;
//        matrix[3][0]= 0.89; matrix[3][1]= 0.11; matrix[3][2]= 0;
//        
//        matrix[4][0]= 0.55; matrix[4][1]= 0.53; matrix[4][2]= 1;
//        matrix[5][0]= 0.53; matrix[5][1]= 0.55; matrix[5][2]= 1;
//        matrix[6][0]= 0.54; matrix[6][1]= 0.54; matrix[6][2]= 1;
//        matrix[7][0]= 0.56; matrix[7][1]= 0.54; matrix[7][2]= 1;
//        
//        matrix[8][0]= 0.10; matrix[8][1]= 0.88; matrix[8][2]= 2;
//        matrix[9][0]= 0.11; matrix[9][1]= 0.86; matrix[9][2]= 2;
//        matrix[10][0]= 0.12; matrix[10][1]= 0.87; matrix[10][2]= 2;
//        matrix[11][0]= 0.11; matrix[11][1]= 0.88; matrix[11][2]= 2;      
//        
//        obj.selectBestFeatures(matrix);
//    }  //SELECT BEST FEATURES IS WORKING
    
}
