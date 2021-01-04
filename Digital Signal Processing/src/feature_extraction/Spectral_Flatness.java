/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_extraction;


import dependencies.Complex;

/**
 *
 * @author hiago
 */
public class Spectral_Flatness{
    
    private static final double SAMPLING_RATE= 16000;
    private double[] sf_measures;

    public Spectral_Flatness() {
    }

    public Spectral_Flatness(double[] sf_measures) {
        this.sf_measures = sf_measures;
    }

    public double[] getSf_measures() {
        return sf_measures;
    }

    public void setSf_measures(double[] sf_measures) {
        this.sf_measures = sf_measures;
    }
    
    
    //STEPS
    // 1 - divide the signal into segments
    // 2 - take the DFT of each segment (calculate the magnitude of the spectrum == absolute values)
    // 3 - calculate the spectral flatness from DFT of each segment (one value for each segment -- an amount Z of values for each signal - Z= number of segments)
    // 4 - extract statistical measures from an amount Z of spectral flatness for each signal 
    
    
    
    
    //---------------------------------------------------------------------------------------------------------------------
    public double[][] stft(double[] signal){
        //dividing the signal into frames with a specific overlap****************************
            double f_lenght= 512; //number of samples  //must be power of two
            double ov_lenght= 384;//number of samples
            int number_of_frames= (int) ((signal.length - ((ov_lenght/f_lenght)*f_lenght)) / ((1-(ov_lenght/f_lenght))*f_lenght));
            double[][] frames= new double[number_of_frames][(int)f_lenght];
        //*************************************************************************************
            double[][] spectrum_magnitude= new double[number_of_frames][(int)f_lenght];
            Complex[] coeffs= new Complex[(int)f_lenght];
            for(int i=0; i<number_of_frames; i++){
                for(int j=0; j<(int) f_lenght; j++){
                    frames[i][j]= signal[(((i*(int)f_lenght) - (i*(int)ov_lenght))+j)];
                    coeffs[j]= new Complex();
                    coeffs[j].setReal(frames[i][j]);     coeffs[j].setImaginary(0);
                }
                fft(coeffs);
                spectrum_magnitude[i]= getMagnitude(coeffs);
            }
            
            return spectrum_magnitude;    
    }
    //---------------------------------------------------------------------------------------------------------------------
    
    
    
    //---------------------------------------------------------------------------------------------------------------------
        public double[] getMagnitude(Complex[] coefficients){
            int N= coefficients.length;
            double[] frame_magnitude= new double[N];
            
            for(int i=0; i<N; i++){
                coefficients[i].setReal(coefficients[i].getReal()/N);
                coefficients[i].setImaginary(coefficients[i].getImaginary()/N);
                frame_magnitude[i]= coefficients[i].abs();
            }
            return frame_magnitude;
        }
    //---------------------------------------------------------------------------------------------------------------------
    
    
    
    //---------------------------------------------------------------------------------------------------------------------
    //here I already product normalized values in the range [0, 1]
    public void calculate_spectral_flatness_measures(double[] signal, Spectral_Flatness obj){
        double[][] spectrum_magnitude= stft(signal); 
        
        //SPECTRAL FLATNESS is defined as the ratio of the geometric mean to the arithmetic mean of the magnitude
        double[] sf= new double[spectrum_magnitude.length];        
        
        for(int i=0; i<spectrum_magnitude.length; i++){
            double sum=0, geomean=1;
            for(int j=0; j<spectrum_magnitude[0].length; j++){
                geomean*= Math.pow(spectrum_magnitude[i][j], (1.0/spectrum_magnitude[0].length));
                sum+=spectrum_magnitude[i][j];
            }
            double average= sum/spectrum_magnitude[0].length;
            if(geomean!=0){
                sf[i]= geomean/average;
            }else{
                sf[i]= 0;
            }
        } 
        //extracting measures from the SF frames generated
        double[] vector_measures= Statistical_Measures.extract_statical_measures(sf);
        obj.setSf_measures(vector_measures);
    }
    //---------------------------------------------------------------------------------------------------------------------
    
    
    //---------------------------------------------------------------------------------------------------------------------
//    public static void main(String[] args) {
//        double [] signal_teste= new double[20541];
////        
//        double teste=5, aux=-2;
//        for(int i=0; i<20541; i++){
//            signal_teste[i]= teste;
//            teste+=aux; aux+=43;
//        }
////        
//        Spectral_Flatness objteste= new Spectral_Flatness();
//        objteste.calculate_spectral_flatness_measures(signal_teste, objteste);
//        System.out.println("Medidas");
//        for(int i=0; i<objteste.getSf_measures().length; i++){
//            System.out.print("["+objteste.getSf_measures()[i]+"]  ");
//        }    
//    }
    //---------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------
    
    
    //------------------------------------FFT(O(n Log n))-------------------------------------------------------------------------
     public void fft(Complex[] signal){
        int N= signal.length;
                 
        if(N==1){
            return;
        }
        if(N%2!=0){
            throw new IllegalArgumentException("N não é potencia de 2");
        }
         
        Complex[] even= new Complex[(N/2)];
        Complex[] odd= new Complex[(N/2)];
        for(int i=0; i<(N/2); i++){
            even[i]= signal[(2*i)];
            odd[i]= signal[(2*i+1)];
        }
        fft(even);
        fft(odd);
        //end of recursion
        
        for(int k=0; k<(N/2); k++){
            
            Complex t = Complex.polar(1.0, -2 * Math.PI * k / N).multiply(odd[k]);
            signal[k] = even[k].add(t);
            signal[k + N/2] = even[k].sub(t);   
        }
     }
    //---------------------------------------------------------------------------------------------------------------------

     
}
