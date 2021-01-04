/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import feature_selection.Paraconsistent_Based_Selection;

/**
 *
 * @author hiago
 */
public class Paraconsistent_Plane {
    
    private double g1;
    private double g2;
    private double distance_from_right;
    private double distance_from_left;
    private double distance_from_up;
    private double distance_from_down;

    public Paraconsistent_Plane() {
    }

    public Paraconsistent_Plane(double g1, double g2, double distance_from_right, double distance_from_left, double distance_from_up, double distance_from_down) {
        this.g1 = g1;
        this.g2 = g2;
        this.distance_from_right = distance_from_right;
        this.distance_from_left = distance_from_left;
        this.distance_from_up = distance_from_up;
        this.distance_from_down = distance_from_down;
    }

    public double getG1() {
        return g1;
    }

    public void setG1(double g1) {
        this.g1 = g1;
    }

    public double getG2() {
        return g2;
    }

    public void setG2(double g2) {
        this.g2 = g2;
    }

    public double getDistance_from_right() {
        return distance_from_right;
    }

    public void setDistance_from_right(double distance_from_right) {
        this.distance_from_right = distance_from_right;
    }

    public double getDistance_from_left() {
        return distance_from_left;
    }

    public void setDistance_from_left(double distance_from_left) {
        this.distance_from_left = distance_from_left;
    }

    public double getDistance_from_up() {
        return distance_from_up;
    }

    public void setDistance_from_up(double distance_from_up) {
        this.distance_from_up = distance_from_up;
    }

    public double getDistance_from_down() {
        return distance_from_down;
    }

    public void setDistance_from_down(double distance_from_down) {
        this.distance_from_down = distance_from_down;
    }
    
    
    public void paraconsistent_analyser(double[][] feature_matrix, int number_of_classes, Paraconsistent_Plane obj){
        //i am supposing that the last column corresponds to the label of the sample (1S)
        int rows= feature_matrix.length;
        int columns= feature_matrix[0].length;
        double alfa=0, beta=0;
        double major=0, minor=0;
        double[][] similarity_matrix= new double[number_of_classes][columns-1]; 
        
        //here is [columns] instead of [columns-1] because the last column is equal to the range vector class label
        double[][] range_matrix= new double[number_of_classes*2][columns]; //order - max and min
                      
        //calculating the value of alfa firstly = intra class similarity
        int aux_class=0, aux_range_rows=0, aux_intervals_columns=0;
        for(int c=0; c<number_of_classes; c++){  
            for(int j=0; j<columns-1; j++){ 
                major= Double.MIN_VALUE; minor= Double.MAX_VALUE;
                for(int i=0; i<rows; i++){
                    if(feature_matrix[i][columns-1]== aux_class){ //i am verifying the label of the class (1S)
                        if(feature_matrix[i][j]> major){
                            major= feature_matrix[i][j];
                        }
                        if(feature_matrix[i][j]<minor){
                            minor= feature_matrix[i][j];
                        }
                    }
                }
                double Y= 1 - (major-minor); similarity_matrix[c][j]= Y;
                //I also use min and max to fill the range vectors (BETA)
                range_matrix[aux_range_rows][j]= major; 
                range_matrix[aux_range_rows+1][j]= minor;
            }
            //labeling the range matrix
            range_matrix[aux_range_rows][columns-1]=aux_class;
            range_matrix[aux_range_rows+1][columns-1]=aux_class;
            //
            aux_class+=1; 
            aux_range_rows+=2; //2 range vectors for each class (BETA)
        }
        
        //here i have the similarity vectors, now I need to calculate an avg of each line and after get the min value
        double[] avg_sim= new double[number_of_classes]; double min_avg= Double.MAX_VALUE;
        for(int i=0; i<number_of_classes; i++){
            double sum=0;
            for(int j=0; j<columns-1; j++){
                sum+=similarity_matrix[i][j];
            }
            avg_sim[i]=sum/(columns-1); //calculating the avg
            if(avg_sim[i]< min_avg){
                min_avg= avg_sim[i];
            }      
        }
        alfa= min_avg; //ALFA IS CALCULATED
        //-----------------now I have to calculate the beta value with range_matrix---------------------------------------
        
        int overlaps= 0; //corresponds to R value in the paper 
//        int F= number_of_classes* (number_of_classes-1)*(rows/number_of_classes)*(columns-1);
        int F= calculate_F(feature_matrix, number_of_classes, false);
        for(int c=0; c<number_of_classes; c++){
            for(int i=0; i<rows; i++){
                if(feature_matrix[i][columns-1]==c){ //comparing the class number
                    int count;
                    for(count=0; count<number_of_classes*2; count++){ //the range vectors
                        if(range_matrix[count][columns-1]!=c && range_matrix[count+1][columns-1]!=c){//different classes
                            for(int j=0; j<columns-1; j++){//each element
                                if(feature_matrix[i][j]<=range_matrix[count][j] && feature_matrix[i][j]>=range_matrix[count+1][j]){
                                    overlaps+=1;
                                }
                            }            
                        }
                        count+=1; //2 range vector for each class   
                    }
                }
            }
        }
        beta= (double)overlaps/F; //BETA IS CALCULATED
        
        obj.setG1(alfa-beta);
        obj.setG2(alfa+beta-1);
        obj.setDistance_from_right(Math.sqrt((Math.pow(obj.getG1()-1, 2))+ (Math.pow(obj.getG2(), 2))));
        obj.setDistance_from_up(Math.sqrt((Math.pow(obj.getG2()-1, 2))+ (Math.pow(obj.getG1(), 2))));
        obj.setDistance_from_left(Math.sqrt((Math.pow(obj.getG1()+1, 2))+ (Math.pow(obj.getG2(), 2))));
        obj.setDistance_from_down(Math.sqrt((Math.pow(obj.getG2()+1, 2))+ (Math.pow(obj.getG1(), 2)))); 
    }
    
    public int calculate_F(double[][] feature_matrix, int number_of_classes, boolean balanced_classes){
        int rows= feature_matrix.length;
        int columns= feature_matrix[0].length;
        int F;
        if(balanced_classes== true){
           F= number_of_classes* (number_of_classes-1)*(rows/number_of_classes)*(columns-1);
        }else{
           F= (number_of_classes-1)* rows *(columns-1);
        }
        return F;
    }
    
    
   
//        public static void main(String[] args) {
//        
////        double[][] matrix= new double[12][3];
////        
////        matrix[0][0]= 0.90; matrix[0][1]= 0.12; matrix[0][2]= 0;
////        matrix[1][0]= 0.88; matrix[1][1]= 0.14; matrix[1][2]= 0;
////        matrix[2][0]= 0.88; matrix[2][1]= 0.13; matrix[2][2]= 0;
////        matrix[3][0]= 0.89; matrix[3][1]= 0.11; matrix[3][2]= 0;
////        
////        matrix[4][0]= 0.55; matrix[4][1]= 0.53; matrix[4][2]= 1;
////        matrix[5][0]= 0.53; matrix[5][1]= 0.55; matrix[5][2]= 1;
////        matrix[6][0]= 0.54; matrix[6][1]= 0.54; matrix[6][2]= 1;
////        matrix[7][0]= 0.56; matrix[7][1]= 0.54; matrix[7][2]= 1;
////        
////        matrix[8][0]= 0.10; matrix[8][1]= 0.88; matrix[8][2]= 2;
////        matrix[9][0]= 0.11; matrix[9][1]= 0.86; matrix[9][2]= 2;
////        matrix[10][0]= 0.12; matrix[10][1]= 0.87; matrix[10][2]= 2;
////        matrix[11][0]= 0.11; matrix[11][1]= 0.88; matrix[11][2]= 2;
//
//        double[][] matrix= new double[8][3];
//        
//        matrix[0][0]= 0.90; matrix[0][1]= 0.11; matrix[0][2]= 0;
//        matrix[1][0]= 0.88; matrix[1][1]= 0.14; matrix[1][2]= 0;
//        matrix[2][0]= 0.10; matrix[2][1]= 0.28; matrix[2][2]= 1;
//        matrix[3][0]= 0.10; matrix[3][1]= 0.28; matrix[3][2]= 1;
//        matrix[4][0]= 0.13; matrix[4][1]= 0.30; matrix[4][2]= 1;
//        matrix[5][0]= 0.12; matrix[5][1]= 0.29; matrix[5][2]= 1;
//        matrix[6][0]= 0.56; matrix[6][1]= 0.55; matrix[6][2]= 2;
//        matrix[7][0]= 0.58; matrix[7][1]= 0.57; matrix[7][2]= 2;
//
//
//                
//        Paraconsistent_Plane obj= new Paraconsistent_Plane();
//        obj.paraconsistent_analyser(matrix, 3, obj);
//        
//        System.out.println("valor de G1: "+obj.getG1());
//        System.out.println("valor de G2: "+obj.getG2());
//        
//        System.out.println("distancia para a direita: "+obj.getDistance_from_right());
//        System.out.println("distancia para cima: "+obj.getDistance_from_up());
//        System.out.println("distancia para a esquerda: "+obj.getDistance_from_left());
//        System.out.println("distancia para baixo: "+obj.getDistance_from_down());       
//   }
    
}
