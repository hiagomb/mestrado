/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_extraction;

import acquisition.Wave_Audio_Signal;

/**
 *
 * @author hiago
 */
public class Spectral_Entropy {
    
    private double[] se_measures;

    public Spectral_Entropy() {
    }

    public Spectral_Entropy(double[] se_measures) {
        this.se_measures = se_measures;
    }

    public double[] getSe_measures() {
        return se_measures;
    }

    public void setSe_measures(double[] se_measures) {
        this.se_measures = se_measures;
    }
    
    public void extract_spectralEntropy(double[] signal, Spectral_Entropy obj){
        double[][] spectrum_magnitude= new Spectral_Flatness().stft(signal);
        double[][] probabilities= new double[spectrum_magnitude.length][spectrum_magnitude[0].length];

        double[] entropies= new double[spectrum_magnitude.length];
        for(int i=0; i<spectrum_magnitude.length; i++){
            double sum=0;
            for(int j=0; j<spectrum_magnitude[0].length; j++){
                sum+=spectrum_magnitude[i][j];
            }
              
            if(sum!=0){//if all frequency bins (sum) are 0, so the entropy is 0
                for(int j=0; j<spectrum_magnitude[0].length; j++){
                    probabilities[i][j]= spectrum_magnitude[i][j]/sum;
                    if(probabilities[i][j]!=0){
                        entropies[i]+= probabilities[i][j]*Math.log(probabilities[i][j]);
                    }
                }
            }
            entropies[i]*=-1;
        }   
       obj.setSe_measures(Statistical_Measures.extract_statical_measures(entropies));
    }
    
//    public static void main(String[] args) {
//        double[] s= new double[1000];
//        for(int i=0; i<1000; i++){
//            s[i]= 1;
//        }
//        
//        Spectral_Entropy se= new Spectral_Entropy();
//        se.extract_spectralEntropy(s, se);
//
//        Wave_Audio_Signal obj= new Wave_Audio_Signal();
//        obj.wave_file_reader("D:\\Hiago\\Acadêmico\\PÓS GRADUAÇÃO\\Disciplinas\\Estudo - PDS\\Pesquisa - A partir de 09.08.2019 (De Fato)\\Bancos de Dados\\EMO-DB\\wav\\Full DB\\03a01Wa.wav", obj);
//        
//        se.extract_spectralEntropy(obj.getSignal(), se);
//        
//        for(int i=0; i<se.getSe_measures().length; i++){
//            System.out.println("["+se.getSe_measures()[i]+"]");
//        }
//
//    }
    
}
