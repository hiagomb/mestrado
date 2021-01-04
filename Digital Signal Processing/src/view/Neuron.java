/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author hiago
 */
public class Neuron {
    
    private double bias;
    private double activation_value;
    private double[] weights;

    public Neuron() {
    }

    public Neuron(double bias, double activation_value, double[] weights) {
        this.bias = bias;
        this.activation_value = activation_value;
        this.weights = weights;
    }

    

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getActivation_value() {
        return activation_value;
    }

    public void setActivation_value(double activation_value) {
        this.activation_value = activation_value;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }
    
    
    
    
    
}
