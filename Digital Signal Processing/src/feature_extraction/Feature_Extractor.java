/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_extraction;

import acquisition.Wave_Audio_Signal;
import analysis.Normalization;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hiago
 */
public class Feature_Extractor {
    
    //********************************EXTRACT FEATURES*****************************************************************
    public double[][] extract_features(Wave_Audio_Signal[] signals){
        
        System.out.println("Extraíndo características...");
        
        Energy_Features ef2= new Energy_Features();
        Energy_Features ef3= new Energy_Features();
        Teager_Energy_Operator teo= new Teager_Energy_Operator();
        Spectral_Flatness sf= new Spectral_Flatness();
        Energy_Features ef1= new Energy_Features();
        Zero_Crossing_Rate zcr= new Zero_Crossing_Rate();
        Spectral_Entropy se= new Spectral_Entropy();
        
        int vector_dimension= getFeatureWidth(signals[0].getSignal(), ef2, ef3, teo, sf, ef1, zcr, se); 
        double feature_matrix[][]= new double[signals.length][vector_dimension+1]; //plus label
        
        int rows= feature_matrix.length; int columns= feature_matrix[0].length;
        for(int i=0; i<rows; i++){
            feature_matrix[i]= concatenateVector(vector_dimension, ef2, ef3, teo, sf, ef1, zcr, se, signals[i].getSignal());
        }
        Normalization normalizer= new Normalization(); //I have to normalize teo_measures and spectral entropy measures
        normalizer.normaliza_maxMin(feature_matrix, ef2.getEnergy_vector().length+ef3.getEnergy_vector().length,
                ef2.getEnergy_vector().length+ ef3.getEnergy_vector().length +teo.getTeo_vector_measures().length);
        
        normalizer.normaliza_maxMin(feature_matrix, ef2.getEnergy_vector().length+ef3.getEnergy_vector().length+
                teo.getTeo_vector_measures().length+sf.getSf_measures().length+ef1.getEnergy_vector().length+
                zcr.getZcr_measures().length, ef2.getEnergy_vector().length+ef3.getEnergy_vector().length+
                teo.getTeo_vector_measures().length+sf.getSf_measures().length+ef1.getEnergy_vector().length+
                zcr.getZcr_measures().length+se.getSe_measures().length);
        
        double[][] final_feature_matrix= labelAndBalance_feature_matrix(feature_matrix, signals, vector_dimension);
        return final_feature_matrix;
    }
    //*********************************************************************************************************************************************************

    
    //**********************************************************************************************************************
    public int getFeatureWidth(double[] s, Energy_Features ef2, Energy_Features ef3, Teager_Energy_Operator teo, 
            Spectral_Flatness sf, Energy_Features ef1, Zero_Crossing_Rate zcr, Spectral_Entropy se){
       
        int W=0;        
        ef2.extraction_A2(s, ef2); 
        ef3.extraction_A3(s, 1, ef3); 
        teo.extract_TEOVectorMeasures(s, teo);
        sf.calculate_spectral_flatness_measures(s, sf);
        ef1.extraction_A1(s, 512, 75, ef1);
        zcr.extract_ZCR(s, zcr, 512, 75);
        se.extract_spectralEntropy(s, se);
        
        W+= ef2.getEnergy_vector().length + ef3.getEnergy_vector().length + teo.getTeo_vector_measures().length + 
            sf.getSf_measures().length + ef1.getEnergy_vector().length + zcr.getZcr_measures().length + se.getSe_measures().length;
        return W;
    }
    //**********************************************************************************************************************
    
    
    //**********************************************************************************************************************
    public double[] concatenateVector(int vector_dimension, Energy_Features ef2, Energy_Features ef3, Teager_Energy_Operator teo, 
            Spectral_Flatness sf, Energy_Features ef1, Zero_Crossing_Rate zcr, Spectral_Entropy se, double[] s){
        
        double[] feature_vector= new double[vector_dimension+1]; //plus label
        
        ef2.extraction_A2(s, ef2); 
        ef3.extraction_A3(s, 1, ef3); 
        teo.extract_TEOVectorMeasures(s, teo);
        sf.calculate_spectral_flatness_measures(s, sf);
        ef1.extraction_A1(s, 512, 75, ef1);
        zcr.extract_ZCR(s, zcr, 512, 75);
        se.extract_spectralEntropy(s, se);
        
        double[] label= new double[1]; label[0]= 0;
        
        System.arraycopy(ef2.getEnergy_vector(), 0, feature_vector, 0, ef2.getEnergy_vector().length);
        System.arraycopy(ef3.getEnergy_vector(), 0, feature_vector, ef2.getEnergy_vector().length, ef3.getEnergy_vector().length);
        System.arraycopy(teo.getTeo_vector_measures(), 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length, teo.getTeo_vector_measures().length);
        System.arraycopy(sf.getSf_measures(), 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length+teo.getTeo_vector_measures().length, sf.getSf_measures().length);
        System.arraycopy(ef1.getEnergy_vector(), 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length+teo.getTeo_vector_measures().length+sf.getSf_measures().length, ef1.getEnergy_vector().length);
        System.arraycopy(zcr.getZcr_measures(), 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length+teo.getTeo_vector_measures().length+sf.getSf_measures().length+ef1.getEnergy_vector().length, zcr.getZcr_measures().length);
        System.arraycopy(se.getSe_measures(), 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length+teo.getTeo_vector_measures().length+sf.getSf_measures().length+ef1.getEnergy_vector().length+zcr.getZcr_measures().length, se.getSe_measures().length);
        System.arraycopy(label, 0, feature_vector, ef2.getEnergy_vector().length + ef3.getEnergy_vector().length+teo.getTeo_vector_measures().length+sf.getSf_measures().length+ef1.getEnergy_vector().length+zcr.getZcr_measures().length+se.getSe_measures().length, label.length);
        
        return feature_vector;
        
    }
    //*********************************************************************************************************************
    
    //********************************LABEL AND BALANCE FEATURE MATRIX*****************************************************************
    public double[][] labelAndBalance_feature_matrix(double[][] feature_matrix, Wave_Audio_Signal[] signals, int vector_dimension){
        
        //labeling the feature vectors for each class (0, 1, 2, 3, 4, 5, 6)
        List<Integer> l= new ArrayList<>();
        
        
        for(int i=0; i<feature_matrix.length; i++){
            String signal_name= signals[i].getFile_name();
            //getting just the last part of the pathname -- cutting out absolute path
            signal_name= signal_name.split("\\\\")[signal_name.split("\\\\").length-1];

            if(signal_name.contains("W")){//anger
                feature_matrix[i][vector_dimension]= 0; 
                //l.add(i);
            }else if(signal_name.contains("L")){//boredom
                feature_matrix[i][vector_dimension]= 6;
                l.add(i);
            }else if(signal_name.contains("E")){//disgust
                feature_matrix[i][vector_dimension]= 6;
                l.add(i);
            }else if(signal_name.contains("A")){//fear
                feature_matrix[i][vector_dimension]= 1; 
                //l.add(i);
            }else if(signal_name.contains("F")){//happiness
                feature_matrix[i][vector_dimension]= 6;
                l.add(i);
            }else if(signal_name.contains("N")){//neutrality
                feature_matrix[i][vector_dimension]= 2; 
                //l.add(i);
            }else if(signal_name.contains("T")){//sadness
                feature_matrix[i][vector_dimension]= 3; 
                //l.add(i);
            }
        }
        
        double[][] four_feature_matrix= new double[feature_matrix.length - l.size()][feature_matrix[0].length];
        int count=0;
        for(int i=0; i<feature_matrix.length; i++){
            if(feature_matrix[i][vector_dimension]!=6){
                four_feature_matrix[count]= feature_matrix[i];
                count+=1;
            }
        }

        return four_feature_matrix;
    }
    //*********************************************************************************************************************************************************    
}
