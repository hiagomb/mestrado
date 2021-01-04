/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hiago
 */
public class MLP_Functions {
    
    public static void init_neurons(Neuron[] neurons, int number_of_weights){
        int size= neurons.length;
        for(int i=0; i<size; i++){
            neurons[i]= new Neuron();
            neurons[i].setActivation_value(0);
            neurons[i].setBias((Math.random()/1000.0));
            double[] weights= new double[number_of_weights];
            for(int j=0; j<number_of_weights; j++){
                weights[j]= Math.random()/1000.0;
            }
            neurons[i].setWeights(weights);
        }
    }
    //********************************************************
    
    public static void feed_forward(double[][] feature_matrix, int index, Neuron[] first_hidden_layer, Neuron[] second_hidden_layer, Neuron[] third_hidden_layer, Neuron[] output_layer){
        for(int i=0; i<first_hidden_layer.length; i++){ //from input to first hidden layer
            first_hidden_layer[i].setActivation_value(0);
            for(int j=0; j<feature_matrix[0].length-1; j++){
                first_hidden_layer[i].setActivation_value(first_hidden_layer[i].getActivation_value() + 
                        feature_matrix[index][j] * first_hidden_layer[i].getWeights()[j]);
            }
            first_hidden_layer[i].setActivation_value(first_hidden_layer[i].getActivation_value() + first_hidden_layer[i].getBias());
            first_hidden_layer[i].setActivation_value(sigmoid_function(first_hidden_layer[i].getActivation_value()));
        }
        
        for(int i=0; i<second_hidden_layer.length; i++){ //from first hidden layer to second hidden layer
            second_hidden_layer[i].setActivation_value(0);
            for(int j=0; j<first_hidden_layer.length; j++){
                second_hidden_layer[i].setActivation_value(second_hidden_layer[i].getActivation_value() +
                        first_hidden_layer[j].getActivation_value() * second_hidden_layer[i].getWeights()[j]);
            }
            second_hidden_layer[i].setActivation_value(second_hidden_layer[i].getActivation_value() + second_hidden_layer[i].getBias());
            second_hidden_layer[i].setActivation_value(sigmoid_function(second_hidden_layer[i].getActivation_value()));
        }
        
        for(int i=0; i<third_hidden_layer.length; i++){ //from second to third hidden layer
            third_hidden_layer[i].setActivation_value(0);
            for(int j=0; j<second_hidden_layer.length; j++){
                third_hidden_layer[i].setActivation_value(third_hidden_layer[i].getActivation_value() +
                        second_hidden_layer[j].getActivation_value() * third_hidden_layer[i].getWeights()[j]);
            }
            third_hidden_layer[i].setActivation_value(third_hidden_layer[i].getActivation_value() + third_hidden_layer[i].getBias());
            third_hidden_layer[i].setActivation_value(sigmoid_function(third_hidden_layer[i].getActivation_value()));
        }
        
        for(int i=0; i<output_layer.length; i++){ //from third hidden layer to output layer
            output_layer[i].setActivation_value(0);
            for(int j=0; j<third_hidden_layer.length; j++){
                output_layer[i].setActivation_value(output_layer[i].getActivation_value() + 
                        third_hidden_layer[j].getActivation_value() * output_layer[i].getWeights()[j]);
            }
            output_layer[i].setActivation_value(output_layer[i].getActivation_value() + output_layer[i].getBias());
            output_layer[i].setActivation_value(sigmoid_function(output_layer[i].getActivation_value()));
        }
        //softmax(output_layer);
    }
    //********************************************************
    
    public static double sigmoid_function(double value){        
        double result= 1.0 / (1.0+Math.exp(-value));
        return result;
    }
    //********************************************************
        
    public static double leakyReLu_function(double value){        
        if(value>= 0){
            return value;
        }else{
            return value * 0.05;
        }
    }
    //********************************************************
    
    public static double leaky_reLu_derivative(double value){
        if(value>=0){
            return 1;
        }else{
            return 0.05;
        }
    }
    
    public static void softmax(Neuron[] neurons){
        int size= neurons.length;
        double[] softmax_outputs= new double[size];
        double sum=0;
        for(int i=0; i<size; i++){
            softmax_outputs[i]= Math.exp(neurons[i].getActivation_value());
            sum+=softmax_outputs[i];
        }
        for(int i=0; i<size; i++){ //normalizing
            softmax_outputs[i]/= sum;
            neurons[i].setActivation_value(softmax_outputs[i]);
        }
    }
    //********************************************************
    
    public static double cross_entropy_error(double[] target_vector, Neuron[] output_neurons){
        double cerror= 0;
        for(int i=0; i<output_neurons.length; i++){
             cerror+= target_vector[i] * Math.log(output_neurons[i].getActivation_value());
        }
        cerror*= -1;
        
        return cerror;
    }
    //********************************************************
    
    public static double calculate_totalError(Neuron[] output_neurons, double[] target_values){
        double totalError=0;
        for(int i=0; i<output_neurons.length; i++){
            totalError+= 0.5 * (Math.pow((output_neurons[i].getActivation_value() - target_values[i]), 2));
        }
        return totalError;
    }
    //******************************************************************************************************    
    
    public static void backpropagation(Neuron[] output_layer, Neuron[] third_hidden_layer, Neuron[] second_hidden_layer, Neuron[] first_hidden_layer, double[] target_values, double learning_rate, double[][] feature_matrix, int index){ 
        double[] dCost_dA= new double[output_layer.length]; 
        double[] dA_dZ= new double[output_layer.length];
        double[][] auxiliar_output_weights= new double[third_hidden_layer.length][output_layer.length];
        for(int i=0; i<output_layer.length; i++){  //updating output neurons weights
            dCost_dA[i]= output_layer[i].getActivation_value() - target_values[i];
            dA_dZ[i]= output_layer[i].getActivation_value() * (1.0 - output_layer[i].getActivation_value());//d_sigmoid
            //dA_dZ[i]= 1; //d_softmax
            for(int j=0; j<third_hidden_layer.length; j++){
                double dZ_dW= third_hidden_layer[j].getActivation_value();  
                double dCost_dW= dCost_dA[i] * dA_dZ[i] * dZ_dW;//full
                auxiliar_output_weights[j][i]= output_layer[i].getWeights()[j] - learning_rate * dCost_dW;
            }
            output_layer[i].setBias(output_layer[i].getBias() - learning_rate * dCost_dA[i] * dA_dZ[i]);
        }
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        double[] dCost_dA_third= new double[third_hidden_layer.length];
        double[] dA_dZ_third= new double[third_hidden_layer.length];
        double[][] auxiliar_third_hidden_weights= new double[second_hidden_layer.length][third_hidden_layer.length];
        for(int i=0; i<third_hidden_layer.length; i++){ //updating third hidden neuron weights
            dCost_dA_third[i]= 0;
            for(int k=0; k<output_layer.length; k++){
                dCost_dA_third[i]+= dCost_dA[k] * dA_dZ[k] * output_layer[k].getWeights()[i];
            }
            dA_dZ_third[i]= third_hidden_layer[i].getActivation_value() * (1.0 - third_hidden_layer[i].getActivation_value());
            for(int j=0; j<second_hidden_layer.length; j++){
                double dZ_dW_third= second_hidden_layer[j].getActivation_value();
                double dCost_dW_third= dCost_dA_third[i] * dA_dZ_third[i] * dZ_dW_third;
                auxiliar_third_hidden_weights[j][i]= third_hidden_layer[i].getWeights()[j] - learning_rate * dCost_dW_third;
            }
            third_hidden_layer[i].setBias(third_hidden_layer[i].getBias() - learning_rate * dCost_dA_third[i] * dA_dZ_third[i]);
        }
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        double[] dCost_dA_second= new double[second_hidden_layer.length];
        double[] dA_dZ_second= new double[second_hidden_layer.length];
        double[][] auxiliar_second_hidden_weights= new double[first_hidden_layer.length][second_hidden_layer.length]; 
        for(int i=0; i<second_hidden_layer.length; i++){ //updating second hidden neurons weights
            dCost_dA_second[i]= 0;
            for(int k=0; k<third_hidden_layer.length; k++){ //each hidden neuron contributes to multiple third`s
                 dCost_dA_second[i]+= dCost_dA_third[k] * dA_dZ_third[k] * third_hidden_layer[k].getWeights()[i];
            }
            //dA_dZ_second[i]= leaky_reLu_derivative(second_hidden_layer[i].getActivation_value());
            dA_dZ_second[i]= second_hidden_layer[i].getActivation_value() * (1.0 - second_hidden_layer[i].getActivation_value());//d_sigmoid
            for(int j=0; j<first_hidden_layer.length; j++){                
                double dZ_dW_second= first_hidden_layer[j].getActivation_value();
                double dCost_dW_second= dCost_dA_second[i] * dA_dZ_second[i] * dZ_dW_second;
                auxiliar_second_hidden_weights[j][i]= second_hidden_layer[i].getWeights()[j] - learning_rate * dCost_dW_second;
            }
            second_hidden_layer[i].setBias(second_hidden_layer[i].getBias() - learning_rate * dCost_dA_second[i] * dA_dZ_second[i]);
        }
        //------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------------------------
        double[] dCost_dA_first= new double[first_hidden_layer.length];
        double[] dA_dZ_first= new double[first_hidden_layer.length];
        double[][] auxiliar_first_hidden_weights= new double[feature_matrix[0].length-1][first_hidden_layer.length];
        
        for(int i=0; i<first_hidden_layer.length; i++){ //updating first hidden neuron weights
            dCost_dA_first[i]= 0;
            for(int k=0; k<second_hidden_layer.length; k++){ //each neuron in 1º hidden layer contributes to multiples in 2º hidden layer
                dCost_dA_first[i]+= dCost_dA_second[k] * dA_dZ_second[k] * second_hidden_layer[k].getWeights()[i];
            }
            dA_dZ_first[i]= first_hidden_layer[i].getActivation_value() * (1.0 - first_hidden_layer[i].getActivation_value());//d_sigmoid
            //dA_dZ_first[i]= leaky_reLu_derivative(first_hidden_layer[i].getActivation_value());
            for(int j=0; j<(feature_matrix[0].length-1); j++){
                double dZ_dW_first= feature_matrix[index][j];
                double dCost_dW_first= dCost_dA_first[i] * dA_dZ_first[i] * dZ_dW_first;
                auxiliar_first_hidden_weights[j][i]= first_hidden_layer[i].getWeights()[j] - learning_rate * dCost_dW_first;
            }
            first_hidden_layer[i].setBias(first_hidden_layer[i].getBias() - learning_rate * dCost_dA_first[i] * dA_dZ_first[i]);
        }
        
        //updating the weights
        update_weights(output_layer, auxiliar_output_weights);
        update_weights(third_hidden_layer, auxiliar_third_hidden_weights);
        update_weights(second_hidden_layer, auxiliar_second_hidden_weights);
        update_weights(first_hidden_layer, auxiliar_first_hidden_weights);
    }
    //************************************************************************************************
    
    public static void update_weights(Neuron[] neurons, double[][] new_W){
        int rows= new_W.length;
        int columns= new_W[0].length;
        for(int i=0; i<columns; i++){ //inversion
            double[] w= new double[rows];
            for(int j=0; j<rows; j++){
                w[j]= new_W[j][i];
            }
            neurons[i].setWeights(w);
        }
    }
    //********************************************************
    
    public static int winner_neuron(Neuron[] output_neurons){
        int winner= -1;
        double major= Double.MIN_VALUE;
        for(int i=0; i<output_neurons.length; i++){
            if(output_neurons[i].getActivation_value()> major){
                major= output_neurons[i].getActivation_value();
                winner= i;
            }
        }
        if(winner== -1){
            throw new IllegalArgumentException("Erro de neurônios");
        }
        return winner;
    }
    //********************************************************
    
    
    //********************************************************
    
//    public static void main(String[] args) {
//        double[] test_output= new double[3];
//        test_output[0]= 1.8658;
//        test_output[1]= 2.2292;
//        test_output[2]= 2.8204;
//        
//        double[] targets= new double[3];
//        targets[0]= 1.0;
//        targets[1]= 0.0;
//        targets[2]= 0.0;
//        
//        System.out.println(sigmoid_function(0));
//        System.out.println(tanh_function(0));
//        
//    }
    
    
}
