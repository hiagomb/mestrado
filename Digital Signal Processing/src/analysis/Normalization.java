/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;


/**
 *
 * @author hiago
 */
public class Normalization {
    //this class is called when the features extracted are not normalized between 0 and 1 yet
    //the method max-min is used for normalizing the values in the intervale [0, 1]
    //I have to pass the index of the start and end columns
    
    public void normaliza_maxMin(double[][] feature_matrix, int start, int end){
        
        double[] major= new double[end-start];
        double[] minor= new double[end-start];
        
        for(int j=start; j<end; j++){// I have to normalize for columns -- each kind of feature
            major[j-start]= Double.MIN_VALUE;
            minor[j-start]=Double.MAX_VALUE;
            for(int i=0; i<feature_matrix.length; i++){
                if(feature_matrix[i][j]> major[j-start]){
                    major[j-start]= feature_matrix[i][j];
                }
                if(feature_matrix[i][j]< minor[j-start]){
                    minor[j-start]= feature_matrix[i][j];
                }
            }
        }
        
        //normalizing
        for(int j=start; j<end; j++){
            for(int i=0; i<feature_matrix.length; i++){
                feature_matrix[i][j]= ((feature_matrix[i][j] - minor[j-start]) / (major[j-start] - minor[j-start]));
            }
        }     
    }
    
    public void zscore(double[][] feature_matrix, int start, int end){
        int rows= feature_matrix.length;
        
        double[] avg= new double[end - start];
        double[] sd= new double[end - start];
        for(int j=start; j<end; j++){ //taking the mean
            avg[j-start]= 0;
            for(int i=0; i<rows; i++){
                avg[j-start]+= feature_matrix[i][j];
            }
            avg[j-start]/= rows;
        }
        
        for(int j=start; j<end; j++){//taking the standard deviation
            sd[j-start]= 0;
            for(int i=0; i<rows; i++){
                sd[j-start]+= Math.pow((feature_matrix[i][j] - avg[j-start]), 2) ;
            }
            sd[j-start]/= rows;
            sd[j-start]= Math.sqrt(sd[j-start]);
        }
        
        for(int j=start; j<end; j++){//applying z-score forced scale
            for(int i=0; i<rows; i++){
                feature_matrix[i][j]= ((feature_matrix[i][j] - avg[j-start]) / sd[j-start]);
                if(feature_matrix[i][j] > 2){
                    feature_matrix[i][j]= 2;
                }
                if(feature_matrix[i][j]< -2){
                    feature_matrix[i][j]= -2;
                }
                feature_matrix[i][j]/= 5;
                feature_matrix[i][j]+= 0.5;
            }
        }
    }
    
    
    public static void main(String[] args) {
        double[][] feature_matrix_teste= new double[6][3];
        
        feature_matrix_teste[0][0]= 24; feature_matrix_teste[0][1]= 23; feature_matrix_teste[0][2]= 4;
        feature_matrix_teste[1][0]= 22; feature_matrix_teste[1][1]= 56; feature_matrix_teste[1][2]= 54;
        feature_matrix_teste[2][0]= 53; feature_matrix_teste[2][1]= 143;feature_matrix_teste[2][2]= 98;
        feature_matrix_teste[3][0]= 57; feature_matrix_teste[3][1]= 32; feature_matrix_teste[3][2]= 9;
        feature_matrix_teste[4][0]= 12; feature_matrix_teste[4][1]= 65; feature_matrix_teste[4][2]= 13;
        feature_matrix_teste[5][0]= 9;  feature_matrix_teste[5][1]= 12; feature_matrix_teste[5][2]= 2;

        
        System.out.println("MATRIZ ORIGINAL");
        for(int i=0; i<feature_matrix_teste.length; i++){
            for(int j=0; j<feature_matrix_teste[0].length; j++){
                System.out.print("["+feature_matrix_teste[i][j]+"]");
            }
            System.out.println("");
        }
//        
//        System.out.println("MATRIZ NORMALIZADA");
        Normalization objteste= new Normalization();
//        objteste.normaliza_maxMin(feature_matrix_teste, 0, 2);
//        for(int i=0; i<feature_matrix_teste.length; i++){
//            for(int j=0; j<feature_matrix_teste[0].length; j++){
//                System.out.print("["+feature_matrix_teste[i][j]+"]");
//            }
//            System.out.println("");
//        }

        objteste.zscore(feature_matrix_teste, 0, feature_matrix_teste[0].length);
        System.out.println("MATRIZ com ZSCORE");
        for(int i=0; i<feature_matrix_teste.length; i++){
            for(int j=0; j<feature_matrix_teste[0].length; j++){
                System.out.print("["+feature_matrix_teste[i][j]+"]");
            }
            System.out.println("");
        }
        
    }
    
}
