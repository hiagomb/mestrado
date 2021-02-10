/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import acquisition.Wave_Audio_Signal;
import analysis.Paraconsistent_Plane;
import charts.Chart_Generator;
import feature_extraction.Feature_Extractor;
import feature_selection.Genetic_Algorithm;
import feature_selection.Paraconsistent_Based_Selection;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hiago
 */
public class MLP {
    
    private final int NUMBER_OF_CLASSES= 4;
        
    
    public void multi_layer_perceptron(double learning_rate, double[][] train, double[][] train_target, double[][] test, double[][] test_target, int max_it){ 
        int NUMBER_OF_HIDDEN_NEURONS = 32;
        
        Neuron[] first_hidden_layer= new Neuron[NUMBER_OF_HIDDEN_NEURONS];
        Neuron[] output_layer= new Neuron[NUMBER_OF_CLASSES];        
        MLP_Functions.init_neurons(first_hidden_layer, train[0].length-1);
        MLP_Functions.init_neurons(output_layer, first_hidden_layer.length);
        
        ArrayList<Double> error_train= new ArrayList<>();
        ArrayList<Double> error_testing= new ArrayList<>();
                
        int it= 0; double ERROR= Double.MAX_VALUE;  int training_amount= train.length; double[] es= new double[max_it]; int es_control= 0;
        while(it< max_it && ERROR!=0){
            ERROR=0;   int count=0; 
            for(int i=0; i<training_amount; i++){   
                MLP_Functions.feed_forward(train, i, first_hidden_layer, output_layer);                
                int label= (int) train[i][train[0].length-1];
                if(MLP_Functions.winner_neuron(output_layer)== label){
                    count+=1;
                }
                ERROR+= MLP_Functions.calculate_totalError(output_layer, train_target[i]);
                MLP_Functions.backpropagation(output_layer, first_hidden_layer, train_target[i], learning_rate, train, i);
            }
            System.out.print("ERRO Médio²= "+(ERROR/training_amount)); error_train.add((ERROR/training_amount));
            System.out.println(" || acertos no conjunto de TREINAMENTO: "+count+" de "+training_amount);
            
            
            //EARLY STOPPING == es
            int test_amount= test.length; double error_test=0;
            for(int i=0; i<test_amount; i++){
                MLP_Functions.feed_forward(test, i, first_hidden_layer, output_layer);
                error_test+= MLP_Functions.calculate_totalError(output_layer, test_target[i]);
            }
            es[it]= error_test/test_amount; error_testing.add(error_test/test_amount);
            if(it>100){
                if(es[it] > es[it-1]){
                    es_control+= 1;
                }else{
                    es_control= 0;
                }
                if(es_control== 20){ //if for 20 sucessive iterations the validation error increases I stop the training
                    it= max_it;
                }
            }
            it+=1;
        }//TESTE AQUI ->
        
        double[][] confusion_matrix= new double[NUMBER_OF_CLASSES][NUMBER_OF_CLASSES];
        for(int i=0; i<NUMBER_OF_CLASSES; i++){
            for(int j=0; j<NUMBER_OF_CLASSES; j++){
                confusion_matrix[i][j]= 0;
            }
        }
                
        int test_amount= test.length; double accuracy= 0; 
        for(int i=0; i<test_amount; i++){
            MLP_Functions.feed_forward(test, i, first_hidden_layer, output_layer);
            int label= (int) test[i][test[0].length-1];
            if(MLP_Functions.winner_neuron(output_layer)== label){
                    accuracy+=1;
            }
            confusion_matrix[label][MLP_Functions.winner_neuron(output_layer)]+=1;
        }
        System.out.println(""); System.out.println("ACURÁCIA NO CONJUNTO DE TESTE= "+(accuracy/test_amount));
        System.out.println(""); System.out.println("MATRIZ DE CONFUSÃO: ");
        for(int i=0; i<NUMBER_OF_CLASSES; i++){
            System.out.println(Arrays.toString(confusion_matrix[i]));
        }

        Chart_Generator cg= new Chart_Generator();
        cg.plotChart(cg.createEarlyStoppingChart(error_train, error_testing, "Iteração", "Erro médio²", 0.0, 0.40), "src\\charts\\erroMedio.png");
    }
    
   
    public static Wave_Audio_Signal[] readSignals(String path){
        File f= new File(path);
        File[] pathnames= f.listFiles();
        Wave_Audio_Signal[] signals_obj= new Wave_Audio_Signal[pathnames.length];
        
        for(int i=0; i<pathnames.length; i++){
            signals_obj[i]= new Wave_Audio_Signal();
            signals_obj[i].wave_file_reader(pathnames[i].toString(), signals_obj[i]);
        }    
        return signals_obj;
    }
    
    
    public static void main(String[] args) {
        
    //******************************READING SIGNALS FROM EMO-DB***********************************************
        Wave_Audio_Signal[] signals; //reading all signals
        signals= readSignals("D:\\Hiago\\Acadêmico\\PÓS GRADUAÇÃO\\Disciplinas\\Estudo - PDS\\"
                + "Pesquisa - A partir de 09.08.2019 (De Fato)\\Bancos de Dados\\EMO-DB\\wav\\Full DB");
    //********************************************************************************************************
                                            //////
    //******************************EXTRACTING FEATURES*******************************************************
        Feature_Extractor f= new Feature_Extractor();
        double[][] feature_matrix= f.extract_features(signals); 
    //********************************************************************************************************   
                                            //////
    //******************************SELECTING BEST SUBGROUP OF FEATURES***************************************                                         
        Paraconsistent_Plane pp= new Paraconsistent_Plane();
        pp.paraconsistent_analyser(feature_matrix, 4, pp); 
        System.out.print("Todas características: "+(feature_matrix[0].length-1)+"|| ");        
        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()+" || G1: "+pp.getG1()+" || G2: "+pp.getG2());
        ArrayList<Double> points_p= new ArrayList<>(); points_p.add(pp.getG1()); points_p.add(pp.getG2());
        
        Genetic_Algorithm ga= new Genetic_Algorithm();
        double[][] selected_matrix= ga.ga_featureSelection(45, feature_matrix, 4);
        pp.paraconsistent_analyser(selected_matrix, 4, pp);
        System.out.print("Características selecionadas: "+(selected_matrix[0].length-1)+"|| ");        
        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()+" || G1: "+pp.getG1()+" || G2: "+pp.getG2());
        points_p.add(pp.getG1()); points_p.add(pp.getG2());

        Chart_Generator cg= new Chart_Generator();
        cg.plotChart(cg.createParaconsistentChart(points_p), "src\\charts\\plano_paraconsistente.png");
    //********************************************************************************************************     
                                            //////    
    //******************************CLASSIFICATION - HOLDOUT**************************************************  
        Holdout obj= new Holdout();
        //System.out.println("CLASSIFICAÇÃO COM TODAS CARACTERÍSTICAS");
        //obj.holdout(feature_matrix, obj, 0.30, 4);
        //new MLP().multi_layer_perceptron(0.1, obj.getTrain(), obj.getTrain_target(), obj.getTest(), obj.getTest_target(), 2000);
        
        System.out.println("CLASSIFICAÇÃO COM CARACTERÍSTICAS SELECIONADAS");
        obj.holdout(selected_matrix, obj, 0.40, 4); 
        new MLP().multi_layer_perceptron(0.1, obj.getTrain(), obj.getTrain_target(), obj.getTest(), obj.getTest_target(), 100000);
    }
}
