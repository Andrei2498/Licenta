/* Copyright 2020, Gurobi Optimization, LLC */

/* Assign workers to shifts; each worker may or may not be available on a
   particular day. If the problem cannot be solved, use IIS iteratively to
   find all conflicting constraints. */
package Gurobi;

import gurobi.*;

import java.io.BufferedReader;
import java.io.FileReader;

public class GurobiMain {

    private int depots;
    private int trips;
    private int[] nrVehicles;
    private double[][] cost;
    private GRBVar[][][] x;
    private GRBEnv env;
    private GRBModel model;
    public double objectiveValue;
    private int i,j,k;

    public void run(String filename){
        init(filename);
        addVariables();
        setObjective();
        setConstraints();
        optimizeModel();
    }

    private void init(String filename){

        try{
            env = new GRBEnv();
            model = new GRBModel(env);
        } catch (GRBException e){
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }

        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String[] splitLine;

            // Parsing the first line and initializing the size variables.
            line = br.readLine();
            splitLine = line.trim().split("\\s+");
            depots = Integer.valueOf(splitLine[0]);
            trips = Integer.valueOf(splitLine[1]);
            nrVehicles = new int[depots];
            cost = new double[depots + trips][depots + trips];
            for(int i = 0; i < depots; i++){
                nrVehicles[i] = Integer.valueOf(splitLine[2 + i]);
            }

            // Initialization of the cost matrix
            int current_line = 0; //Current line in the cost matrix
            while ((line = br.readLine()) != null){
                splitLine = line.trim().split("\\s+");
                for(int j = 0; j < splitLine.length; j++){
                    cost[current_line][j] = Integer.valueOf(splitLine[j]);
                }
                current_line += 1;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addVariables(){
        try {
            x = new GRBVar[depots][trips + depots][trips + depots];
            for (int k = 0; k < depots; k++) {
                for (int i = 0; i < trips + depots; i++) {
                    for (int j = 0; j < trips + depots; j++) {
                        if((i == k || i >= depots) && (j == k || j >= depots)){
                            if (cost[i][j] != -1) {
                                x[k][i][j] = model.addVar(0, 1, 0, GRB.BINARY, k + " " + i + " " + j);
                            }
                        }
                    }
                }
            }
        } catch (GRBException e){
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }
    }

    private void setObjective(){
        try {
            GRBLinExpr expr = new GRBLinExpr();
            for (k = 0; k < depots; k++) {
                for (i = 0; i < trips + depots; i++) {
                    for (j = 0; j < trips + depots; j++) {
                        if (x[k][i][j] != null) {
                            expr.addTerm(cost[i][j], x[k][i][j]);
                        }
                    }
                }
            }
            model.setObjective(expr, GRB.MINIMIZE);
        } catch (GRBException e){
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }
    }

    private void setConstraints(){
        try {
            //(2) All trips must be done
            for(i = depots; i < trips + depots; i++){
                GRBLinExpr expr1 = new GRBLinExpr();
                GRBLinExpr expr2 = new GRBLinExpr();
                for(j = 0; j < trips + depots; j++){
                    for(k = 0; k < depots; k++){
                        if(x[k][i][j] != null){
                            expr1.addTerm(1, x[k][i][j]);
                        }
                        if(x[k][j][i] != null){
                            expr2.addTerm(1,x[k][j][i]);
                        }
                    }
                }
                model.addConstr(expr1, GRB.EQUAL, 1, "Trip covered:" + i);
                model.addConstr(expr2, GRB.EQUAL, 1,    "Trip 2 covered:" + i);
            }

            //(3) The number of vehicles pulled out from a depot can't be higher the amount of vehicles stored in that depot
            for(i = 0; i < depots; i++){
                GRBLinExpr expr = new GRBLinExpr();
                for (j = depots; j < depots + trips; j++){
                    if(x[i][i][j] != null){
                        expr.addTerm(1, x[i][i][j]);
                    }
                }
                model.addConstr(expr, GRB.LESS_EQUAL, nrVehicles[i], "Depot number: " + i);
            }

            //(4) Flow conservation constraint
            for(i = 0; i < depots; i++){
                GRBLinExpr expr1 = new GRBLinExpr();
                GRBLinExpr expr2 = new GRBLinExpr();
                for(j = depots; j < depots + trips; j++){
                    if(x[i][i][j] != null){
                        expr1.addTerm(1, x[i][i][j]);
                    }
                    if(x[i][j][i] != null){
                        expr2.addTerm(1, x[i][j][i]);
                    }
                }
                model.addConstr(expr1, GRB.EQUAL, expr2, "Depot: " + i);
            }

        } catch (GRBException e){
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }
    }

    private void optimizeModel(){
        try {

            model.write("Test.lp");

            model.optimize();
            int status = model.get(GRB.IntAttr.Status);
            if (status == GRB.Status.INF_OR_UNBD ||
                    status == GRB.Status.INFEASIBLE  ||
                    status == GRB.Status.UNBOUNDED     ) {
                System.out.println("The model cannot be solved "
                        + "because it is infeasible or unbounded");
                System.out.println("Status: " + status);
            }
            if (status != GRB.Status.OPTIMAL ) {
                System.out.println("Optimization was stopped with status " + status);
                System.out.println("Status: " + status);
            }

            objectiveValue = model.get(GRB.DoubleAttr.ObjVal);
            model.dispose();
            env.dispose();

        } catch (GRBException e){
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
        }
    }
}