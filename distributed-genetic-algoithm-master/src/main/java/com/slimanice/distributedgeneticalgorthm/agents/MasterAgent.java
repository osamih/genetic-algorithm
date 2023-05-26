package com.slimanice.distributedgeneticalgorthm.agents;

import com.slimanice.distributedgeneticalgorthm.controllers.MasterGuiController;
import com.slimanice.distributedgeneticalgorthm.utils.GAUtils;
import com.slimanice.distributedgeneticalgorthm.utils.Solution;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MasterAgent extends GuiAgent{
    int solutionsFound = 0;
    List<Solution> solutionList = new ArrayList<>();
    MasterGuiController masterGuiController;

    @Override
    protected void setup() {
        // Get Gui Controller
        masterGuiController = (MasterGuiController) getArguments()[0];
        // Initialize the GUI with the master agent
        masterGuiController.setMasterAgent(this);
        // Get the number of islands
        int numberOfIslands = masterGuiController.getNbrIslands();
        // Get target
        String target = masterGuiController.getTarget();
        // Get max iterations
        int maxIterations = masterGuiController.getMaxGen();
        // Get population size
        int populationSize = masterGuiController.getPopulationSize();
        double mutationRate = masterGuiController.getMutationRate();
        // Register the master agent in the DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("master");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        // Cyclic behaviour to handle messages
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    if (msg.getConversationId().equals("request-parameters")) {
                        // Send parameters to the island agent
                        String type = "parameters";
                        String parameters = "target=" + target + ";maxIterations=" + maxIterations + ";populationSize=" + populationSize + ";mutationRate=" + mutationRate;
                        sendMsg(msg.getSender(), type, parameters);
                    }
                    else if (msg.getConversationId().equals("first-fittest")) {
                        String solution = content.split("\\|")[0];
                        String fitness = content.split("\\|")[1];
                        solutionsFound++;
                        solutionList.add(new Solution(msg.getSender(), solution, Integer.parseInt(fitness)));
                    }
                }
                else {
                    block();
                }
            }

            @Override
            public boolean done() {
                if(solutionsFound == numberOfIslands)
                    displayResults();
                return solutionsFound == numberOfIslands;
            }

            private void displayResults() {
                // Sort the solutions by fitness
                solutionList.sort(Comparator.comparingInt(Solution::getFitness));
                System.out.println("All solutions found:");
                for(Solution solution : solutionList){
                    System.out.println("Island: " + solution.getAid().getLocalName() + "; solution: " + solution.getSolution() + "; fitness: " + solution.getFitness());
                }
                // Display results in the GUI
                masterGuiController.displayResults(solutionList);
            }

            private void sendMsg(AID sender, String type, String parameters) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(sender);
                msg.setConversationId(type);
                msg.setContent(parameters);
                send(msg);
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    // Terminate the agent
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
