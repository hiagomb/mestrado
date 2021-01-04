/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package feature_selection;

import charts.Chart_Generator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author hiago
 */
public class Genetic_Algorithm {
    
    private int[][] population;
    private int[] best_individual;
    private int[] elitism_indexes;
    private final int NUMBER_OF_FEATURES= 740; //mudar

    public Genetic_Algorithm() {
    }

    public Genetic_Algorithm(int[][] population, int[] best_individual, int[] elitism_indexes) {
        this.population = population;
        this.best_individual= best_individual;
        this.elitism_indexes= elitism_indexes;
    }

    public int[][] getPopulation() {
        return population;
    }

    public void setPopulation(int[][] population) {
        this.population = population;
    }

    public int[] getBest_individual() {
        return best_individual;
    }

    public void setBest_individual(int[] best_individual) {
        this.best_individual = best_individual;
    }

    public int[] getElitism_indexes() {
        return elitism_indexes;
    }

    public void setElitism_indexes(int[] elitism_indexes) {
        this.elitism_indexes = elitism_indexes;
    }

    
    public double[][] assemble_selected_matrix(double[][] feature_matrix){
        System.out.print("Características selecionadas: ");
        List<Integer> features= new ArrayList<>();
        for(int i=0; i<getBest_individual().length; i++){
            if(getBest_individual()[i]== 1){
                features.add(i);
                System.out.print("  ["+i+"]");
            }
        }System.out.println("");
        double[][] selected_matrix= new double[feature_matrix.length][features.size()+1]; //plus label
        for(int j=0; j<features.size()+1; j++){
            for(int i=0; i<feature_matrix.length; i++){
                if(j== features.size()){
                    selected_matrix[i][j]= feature_matrix[i][feature_matrix[0].length-1];
                }else{
                    selected_matrix[i][j]= feature_matrix[i][features.get(j)];
                }
            }
        }
        return selected_matrix;
    }
    
    
    private static ArrayList<Double> list= new ArrayList<>(); //for the chart
    
    public double[][] ga_featureSelection(int max_generation, double[][] feature_matrix){ 
        init_population();
        int generation=1;
        setBest_individual(getPopulation()[0]);
        double[][] pizza= fitness(feature_matrix, 1.5);
        
        System.out.println("Selecionando melhor subgrupo de características com AG");
        while(generation< max_generation){
            ArrayList<String> parents= population_selection(pizza);
            reproduction(parents);
            System.out.print("Generation "+generation+"  ||  ");    generation+=1;
            pizza= fitness(feature_matrix, 1.5);   
        }
        System.out.print("MELHOR INDIVIDUO: ");
        System.out.println(Arrays.toString(getBest_individual()));
        
        //chart generation
        Chart_Generator cg= new Chart_Generator();
        cg.plotChart(cg.createChart(list, "Geração", "Distância do ponto P(1,0)", 0.95, 1.30), "src\\charts\\evolucao_AG.png");
        
        return assemble_selected_matrix(feature_matrix);      
    }
    
    public void init_population(){
        System.out.println("Inicializando população");
        BigDecimal max= new BigDecimal("2"); max= max.pow(NUMBER_OF_FEATURES); max= max.subtract(BigDecimal.valueOf(1));
        int[][] new_population= new int[NUMBER_OF_FEATURES][NUMBER_OF_FEATURES]; //HERE
        Paraconsistent_Based_Selection pbs= new Paraconsistent_Based_Selection();
        
        for(int i=0; i<new_population.length; i++){
            BigDecimal aux= new BigDecimal(new String(""+Math.random()));
            BigInteger random_value= max.multiply(aux).toBigInteger();
            pbs.decBinConversion(random_value, new_population[i]);
        }
        setPopulation(new_population);
    }
    
    
    public double[][] fitness(double[][] feature_matrix, double selective_pressure){ //the original matrix
        double[] distances_1_0= new double[NUMBER_OF_FEATURES]; //probability of each individual  //HERE
        double[] copy= new double[NUMBER_OF_FEATURES];//HERE
        
        for(int i=0; i<population.length; i++){
            ArrayList<String> positions= new ArrayList<>();
            int count=0;
            for(int j=0; j<population[0].length; j++){
                if(population[i][j]==1){
                    positions.add(""+j);
                    count+=1;
                }
            }
            Paraconsistent_Based_Selection pbs= new Paraconsistent_Based_Selection();
            distances_1_0[i]= pbs.setUp_and_Test_subMatrix(feature_matrix, count, positions).getDistance_from_right();
            copy[i]= distances_1_0[i]; //without ordering
        }
        elitism_indexes= new int[copy.length];
        
        Arrays.sort(distances_1_0); //sorting in the ascending order
        double[] fit= new double[distances_1_0.length]; //fitness of each individual
        int sum=0; int best_index=-1;
        for(int i=0; i<fit.length; i++){
            for(int j=0; j<fit.length; j++){
                if(distances_1_0[i]== copy[j]){
                    fit[j]= (fit.length-i) * selective_pressure;
                    sum+= fit[j]; //normalization
                    
                    if(i==0){// the best fitness in this generation
                        best_index= j;
                    }
                    elitism_indexes[i]= j;
                    j= fit.length; //for exiting the loop
                }
            }
        }
        //odds pizza
        double[][] odds_pizza= new double[fit.length][2];
        double aux=0; 
        for(int j=0; j<odds_pizza.length; j++){ 
            fit[j]/= sum; //normalization
            aux+= fit[j];
            odds_pizza[j][0]= aux- fit[j];
            odds_pizza[j][1]= aux;
        }
        
        best_of_all_time(best_index, copy, feature_matrix);
        //System.out.println("Indivíduo ["+best_index+"] está à "+copy[best_index]+" do ponto (1, 0)");
        return odds_pizza;
    }
    
    public ArrayList population_selection(double[][] pizza){//the selection is made using SUS
        final int number_of_parents= NUMBER_OF_FEATURES/2;  //HERE
        double[] points= new double[number_of_parents]; 
        double distance= (double) 1/number_of_parents;
        double random_value= Math.random() * distance;
        
        ArrayList<String> parents= new ArrayList<>();
        for(int i=0; i<number_of_parents; i++){
            points[i]= random_value + distance*i;
            for(int j=0; j<(NUMBER_OF_FEATURES); j++){ //HERE
                if(points[i]>=pizza[j][0] && points[i]<pizza[j][1]){
                    parents.add(""+j);
                    j= NUMBER_OF_FEATURES; //for exiting the loop  //HERE
                }
            }
        }
        return parents;
    }
    
    public void reproduction(ArrayList<String> parents){//each double of parents generate 4 children
        int[][] new_population= new int[NUMBER_OF_FEATURES][NUMBER_OF_FEATURES];  //HERE
        int[][] old_population= getPopulation();
        int number_of_children= 4;
        
        double mutation_rate= (double) 1/NUMBER_OF_FEATURES;
        
        int aux=0;
        for(int i=0; i<parents.size(); i++){ //crossover and mutation 
            int father= (int) (Math.random() * parents.size()); father= Integer.parseInt(parents.get(father));
            int mother= (int) (Math.random() * parents.size()); mother= Integer.parseInt(parents.get(mother));
            for(int x=0; x<number_of_children; x++){
                for(int j=0; j<new_population[0].length; j++){
                    if(Math.random()>=0.5){ //take the gene from father or mother
                        new_population[aux*number_of_children+x][j]= old_population[father][j];
                    }else{
                        new_population[aux*number_of_children+x][j]= old_population[mother][j]; 
                    }
                    if(Math.random()<mutation_rate){ //mutation
                        if(new_population[aux*number_of_children+x][j]==0){
                            new_population[aux*number_of_children+x][j]=1;
                        }else{
                            new_population[aux*number_of_children+x][j]=0;
                        }
                    }
                }
                if(aux==0){ //elitism of 4 best individuals
                    new_population[aux*number_of_children+x]=old_population[elitism_indexes[x]];
                }
            }
            aux+=1;
            i+=1; //two parents
        }
        setPopulation(new_population);
    }
    
    public void best_of_all_time(int best_index, double[] copy, double[][] feature_matrix){
        ArrayList<String> pos= new ArrayList<>();
        int c=0;
        for(int j=0; j<population[0].length; j++){
            if(getBest_individual()[j]==1){
                pos.add(""+j);
                c+=1;
            }
        }
        double dist= new Paraconsistent_Based_Selection().setUp_and_Test_subMatrix(feature_matrix, c, pos).getDistance_from_right();
        if(copy[best_index]< dist){
            setBest_individual(getPopulation()[best_index]);
            System.out.println("distancia do melhor individuo para ponto (1, 0): "+copy[best_index]);
            list.add(copy[best_index]);
        }else{
            System.out.println("distancia do melhor individuo para ponto (1, 0): "+dist);
            list.add(dist);
        }
    }
    
//    public static void main(String[] args) {
//        Genetic_Algorithm ga= new Genetic_Algorithm();
//        
//        double[][] matrix= new double[322][287];
//        for(int i=0; i<matrix.length; i++){
//            for(int j=0; j<matrix[0].length; j++){
//                if(j==(matrix[0].length-1)){
//                    if(i<=45){
//                        matrix[i][j]= 0;
//                    }else if(i>=46 && i<=91){
//                        matrix[i][j]= 1;
//                    }else if(i>=92 && i<=137){
//                        matrix[i][j]= 2;
//                    }else if(i>=138 && i<=183){
//                        matrix[i][j]= 3;
//                    }else if(i>=184 && i<=229){
//                        matrix[i][j]= 4;
//                    }else if(i>=230 && i<=275){
//                        matrix[i][j]= 5;
//                    }else if(i>=276 && i<=321){
//                        matrix[i][j]= 6;
//                    }
//                }else{
//                    matrix[i][j]= Math.random();
//                }
//            }
//        }
//
//        ga.ga_featureSelection(100, matrix);   
//    }
    
}
