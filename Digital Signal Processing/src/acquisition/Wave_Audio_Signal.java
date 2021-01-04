/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acquisition;


import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author hiago
 */
public class Wave_Audio_Signal {
    
    private int sampling_rate;
    private long signal_lenght;
    private double signal[];
    private String file_name;

    public Wave_Audio_Signal() {
    }

    public Wave_Audio_Signal(int sampling_rate, long signal_lenght, double[] signal, String file_name) {
        this.sampling_rate = sampling_rate;
        this.signal_lenght = signal_lenght;
        this.signal = signal;
        this.file_name= file_name;
    }
    
    public int getSampling_rate(){
        return sampling_rate;
    }
    
    public void setSampling_rate(int sampling_rate){
        this.sampling_rate= sampling_rate;
                
    }

    public long getSignal_lenght() {
        return signal_lenght;
    }

    public void setSignal_lenght(long signal_lenght) {
        this.signal_lenght = signal_lenght;
    }

    public double[] getSignal() {
        return signal;
    }

    public void setSignal(double[] signal) {
        this.signal = signal;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    
    
    public void wave_file_reader(String path, Wave_Audio_Signal obj){ 
        try{
            File file= new File(path); obj.setFile_name(path);
            AudioInputStream ais= AudioSystem.getAudioInputStream(file);
            obj.setSampling_rate((int)ais.getFormat().getSampleRate());
            obj.setSignal_lenght(ais.getFrameLength()); 
            double s[]= new double[(int) obj.getSignal_lenght()];
            int bytesPerSample= ais.getFormat().getFrameSize(); //return the number of bytes per sample
            //it will be 4 bytes for stereo sounds and 2 bytes for mono sounds
            
            /*with AudioInputStream class I just can read the samples (amplitude values) through the bytes,
            then I can translate for real amplitude values*/
            int total_number_of_bytes= (int) (bytesPerSample * obj.getSignal_lenght()); 
            byte[] bytes= new byte[total_number_of_bytes];
            int number_of_bytes_read= 0;
            while((number_of_bytes_read= ais.read(bytes))!=-1){

            }
            int aux_byte_count=0;
            for(int i=0; i<obj.getSignal_lenght(); i++){
                    //little endian conversion 2 bytes
                    s[i]= (short) ((bytes[aux_byte_count+1] & 0xff) << 8) + (bytes[aux_byte_count] & 0xff);
                    aux_byte_count+=2;
            }       
            obj.setSignal(s);
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    
//    public static void main(String[] args) {
//        Wave_Audio_Signal obj= new Wave_Audio_Signal();
//        obj.wave_file_reader("D:\\Hiago\\Acadêmico\\PÓS GRADUAÇÃO\\Disciplinas\\Estudo - PDS\\Pesquisa - A partir de 09.08.2019 (De Fato)\\Bancos de Dados\\EMO-DB\\wav\\Full DB\\03a01Wa.wav", obj);
//        System.out.println("Taxa de Amostragem: "+ obj.getSampling_rate());
//        System.out.println("Comprimento do Sinal: "+ obj.getSignal_lenght());
//        System.out.println("Nome do arquivo: "+obj.getFile_name());
//        for(int i=0; i<obj.getSignal_lenght(); i++){
//            System.out.println("["+i+"] = "+obj.getSignal()[i]);
//        }
//    }
    
}
