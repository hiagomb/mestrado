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
    
    private final int NUMBER_OF_CLASSES= 7;
        
    
    public void multi_layer_perceptron(double learning_rate, double[][] train, double[][] train_target, double[][] test, double[][] test_target, int max_it){ 
        int NUMBER_OF_HIDDEN_NEURONS = (train[0].length-1 + NUMBER_OF_CLASSES)/2;
        //NUMBER_OF_HIDDEN_NEURONS= 100;
        System.out.println("hidden_neurons= "+NUMBER_OF_HIDDEN_NEURONS);
        
        Neuron[] first_hidden_layer= new Neuron[NUMBER_OF_HIDDEN_NEURONS];
        Neuron[] second_hidden_layer= new Neuron[NUMBER_OF_HIDDEN_NEURONS];
        Neuron[] third_hidden_layer= new Neuron[NUMBER_OF_HIDDEN_NEURONS];
        Neuron[] fourth_hidden_layer= new Neuron[NUMBER_OF_HIDDEN_NEURONS];
        Neuron[] output_layer= new Neuron[NUMBER_OF_CLASSES];
        
        MLP_Functions.init_neurons(first_hidden_layer, train[0].length-1);
        MLP_Functions.init_neurons(second_hidden_layer, NUMBER_OF_HIDDEN_NEURONS);
        MLP_Functions.init_neurons(third_hidden_layer, NUMBER_OF_HIDDEN_NEURONS);
        MLP_Functions.init_neurons(fourth_hidden_layer, NUMBER_OF_HIDDEN_NEURONS);
        MLP_Functions.init_neurons(output_layer, NUMBER_OF_HIDDEN_NEURONS);
        
        ArrayList<Double> error= new ArrayList<>();
        
        int it= 0; double ERROR= Double.MAX_VALUE;  int training_amount= train.length;
        while(it< max_it && ERROR!=0){
            ERROR=0;   int count=0; 
            for(int i=0; i<training_amount; i++){   
                MLP_Functions.feed_forward(train, i, first_hidden_layer, second_hidden_layer, third_hidden_layer, fourth_hidden_layer, output_layer);                
                int label= (int) train[i][train[0].length-1];
                if(MLP_Functions.winner_neuron(output_layer)== label){
                    count+=1;
                }
                ERROR+= MLP_Functions.calculate_totalError(output_layer, train_target[i]);
                //ERROR+= MLP_Functions.cross_entropy_error(train_target[i], output_layer);
                MLP_Functions.backpropagation(output_layer, fourth_hidden_layer, third_hidden_layer, second_hidden_layer, first_hidden_layer, train_target[i], learning_rate, train, i);
            }
            System.out.print("ERRO Médio²= "+(ERROR/training_amount)); error.add((ERROR/training_amount));
            System.out.print(" || acertos no conjunto de TREINAMENTO: "+count+" de "+training_amount);
            
            it+=1;

            int test_amount= test.length; double accuracy= 0; double error_test=0;
            for(int i=0; i<test_amount; i++){
                MLP_Functions.feed_forward(test, i, first_hidden_layer, second_hidden_layer, third_hidden_layer, fourth_hidden_layer, output_layer);
                int label= (int) test[i][test[0].length-1];
                if(MLP_Functions.winner_neuron(output_layer)== label){
                    accuracy+=1;
                }
                error_test+= MLP_Functions.calculate_totalError(output_layer, test_target[i]);
            }
            System.out.println("  ||  acurácia no conjunto de TESTE= "+(accuracy/test_amount)+"  || ERR= "+error_test/test_amount);
            
        }
        //TESTE AQUI ->
//        int test_amount= test.length; double accuracy= 0;
//        for(int i=0; i<test_amount; i++){
//            MLP_Functions.feed_forward(test, i, hidden_neurons, output_neurons, W0, W1, bias);
//            int label= (int) test[i][test[0].length-1];
//            if(MLP_Functions.winner_neuron(output_neurons)== label){
//                    accuracy+=1;
//            }
//        }
//        System.out.println("acurácia= "+(accuracy/test_amount));

        Chart_Generator cg= new Chart_Generator();
        cg.plotChart(cg.createChart(error, "Iteração", "Erro médio²", 0.0, 0.6), "src\\charts\\erroMedio.png");
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
        double[][] feature_matrix= f.extract_features(signals, 7); //7 is the number of classes/emotions
    //********************************************************************************************************   
                                            //////
    //******************************SELECTING BEST SUBGROUP OF FEATURES***************************************                                         
        Paraconsistent_Plane pp= new Paraconsistent_Plane();
        pp.paraconsistent_analyser(feature_matrix, 7, pp);
        System.out.print("Todas características: "+(feature_matrix[0].length-1)+"|| ");        
        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()+" || G1: "+pp.getG1()+" || G2: "+pp.getG2());
        ArrayList<Double> points_p= new ArrayList<>(); points_p.add(pp.getG1()); points_p.add(pp.getG2());
        
//        Genetic_Algorithm ga= new Genetic_Algorithm();
//        double[][] selected_matrix= ga.ga_featureSelection(100, feature_matrix);
//        pp.paraconsistent_analyser(selected_matrix, 7, pp);
//        System.out.print("Características selecionadas: "+(selected_matrix[0].length-1)+"|| ");        
//        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()+" || G1: "+pp.getG1()+" || G2: "+pp.getG2());
//        points_p.add(pp.getG1()); points_p.add(pp.getG2());
    //-----------------------------------------------------------------------------------------------------
                                /*THIS IS JUST FOR TESTING -- I WILL DELETE THIS*/
        Paraconsistent_Based_Selection selector= new Paraconsistent_Based_Selection();
        int[] ranking_best_descriptors= selector.rank_best_features(feature_matrix, 7);
//        System.out.println(Arrays.toString(ranking_best_descriptors));
        int feature_dimension= 50; //I can choose the number here
        double[][] matrix_test= new double[feature_matrix.length][feature_dimension+1]; //plus label
        for(int j=0; j<(feature_dimension+1); j++){
            for(int i=0; i<feature_matrix.length; i++){
                if(j!=feature_dimension){
                    matrix_test[i][j]= feature_matrix[i][ranking_best_descriptors[j]];
                }else{
                    matrix_test[i][j]= feature_matrix[i][feature_matrix[0].length-1];
                }
            }
        }
        
        //testando
        pp.paraconsistent_analyser(matrix_test, 7, pp);
        System.out.print("MELHORES CARACTERÍSTICAS: || ");        
        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()+" || G1: "+pp.getG1()+" || G2: "+pp.getG2());
    //-----------------------------------------------------------------------------------------------------
//        Chart_Generator cg= new Chart_Generator();
//        cg.plotChart(cg.createParaconsistentChart(points_p), "src\\charts\\plano_paraconsistente.png");
//    ********************************************************************************************************     
                                            //////    
    //******************************CLASSIFICATION - HOLDOUT*******************************  
        Holdout obj= new Holdout();
//        System.out.println("CLASSIFICAÇÃO COM TODAS CARACTERÍSTICAS");
//        obj.holdout(feature_matrix, obj, 0.90, 7);
//        new MLP().multi_layer_perceptron(0.1, obj.getTrain(), obj.getTrain_target(), obj.getTest(), obj.getTest_target(), 2000);
        
        System.out.println("CLASSIFICAÇÃO COM CARACTERÍSTICAS SELECIONADAS");
        obj.holdout(matrix_test, obj, 0.50, 7);
        new MLP().multi_layer_perceptron(0.1, obj.getTrain(), obj.getTrain_target(), obj.getTest(), obj.getTest_target(), 20000);

    }
}
