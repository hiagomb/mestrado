package view;

import acquisition.Wave_Audio_Signal;
import analysis.Paraconsistent_Plane;
import java.awt.List;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import feature_extraction.Energy_Features;
import feature_extraction.Feature_Extractor;
import feature_extraction.Teager_Energy_Operator;
import feature_selection.Genetic_Algorithm;
import feature_selection.Paraconsistent_Based_Selection;

/**
 *
 * @author hiago
 */
public class Application_Test {
        
    private static final int NUMBER_OF_EMOTIONS= 7;
    
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
        Wave_Audio_Signal[] signals; //reading all signals
        signals= readSignals("D:\\Hiago\\Acadêmico\\PÓS GRADUAÇÃO\\Disciplinas\\Estudo - PDS\\Pesquisa - A partir de 09.08.2019 (De Fato)\\Bancos de Dados\\EMO-DB\\wav\\Full DB");
        
//        A1, A2, A3 e FLATNESS já geram valores normalizados no intervalo [0, 1]
//        OPERADOR DE ENERGIA DE TEAGER é normalizado segundo a abordagem max-min
//        FEATURE_EXTRACTOR extrai características, balanceia classes e rotula ultima coluna

        Feature_Extractor f= new Feature_Extractor();
        double[][] feature_matrix= f.extract_features(signals, NUMBER_OF_EMOTIONS);
        
        
//        for(int i=0; i<feature_matrix.length; i++){
//            for(int j=0; j<feature_matrix[0].length; j++){
//                System.out.print("["+feature_matrix[i][j]+"]");
//            }
//            System.out.println("");
//        }  


        Paraconsistent_Based_Selection selector= new Paraconsistent_Based_Selection();
        int[] ranking_best_descriptors= selector.rank_best_features(feature_matrix, NUMBER_OF_EMOTIONS);

        Paraconsistent_Plane pp= new Paraconsistent_Plane();
        pp.paraconsistent_analyser(feature_matrix, NUMBER_OF_EMOTIONS, pp);
        System.out.println("Todas características");        
        System.out.println("distancia para o ponto (1, 0) = "+pp.getDistance_from_right()); 
//        System.out.println("G1: "+pp.getG1());
//        System.out.println("G2: "+pp.getG2());

//        Genetic_Algorithm ga= new Genetic_Algorithm();
//        ga.ga_featureSelection(300, feature_matrix);

        //TESTE
//        ArrayList<String> pos= new ArrayList<>();
//        pos.add(""+191);
//        pos.add(""+212);
//        pos.add(""+243);
//        pos.add(""+244);
//        pos.add(""+246);
//        pos.add(""+247);
//        pos.add(""+248);
//        pos.add(""+249);
//        pos.add(""+250);
//        pos.add(""+251);
//        
//        Paraconsistent_Plane obj= new Paraconsistent_Based_Selection().setUp_and_Test_subMatrix(feature_matrix, pos.size(), pos);
//        System.out.println("Características selecionadas");
//        System.out.println("distancia para (1, 0 ): "+obj.getDistance_from_right());
//        System.out.println("G1: "+obj.getG1());
//        System.out.println("G2: "+obj.getG2());
        //TESTE

    }
    
}