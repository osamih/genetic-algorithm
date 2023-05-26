package com.slimanice.distributedgeneticalgorthm.agents;

import com.slimanice.distributedgeneticalgorthm.utils.Population;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class IslandAgent extends Agent {
    private String target;
    private int maxIterations;
    private int populationSize;
    @Override
    protected void setup() {
        // Get the master agent's AID
        AID masterAgentAID = null;
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("master");
        template.addServices(sd);
        try {
            boolean isMasterAgentFound = false;
            while(!isMasterAgentFound) {
                DFAgentDescription[] result = DFService.search(this, template);
                if (result.length > 0) {
                    masterAgentAID = result[0].getName();
                    isMasterAgentFound = true;
                }
                else {
                    System.out.println("Master agent not found. Retrying in 1 seconds...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        // Request parameters from the master agent
//        System.out.println("Requesting parameters from the master agent...");
        sendMsg(masterAgentAID, "request-parameters", "");

        // Receive parameters from the master agent
        // This will block until the master agent sends the parameters
        receiveParameters();
//        System.out.println("Received parameters from the master agent: target=" + target + ";maxIterations=" + maxIterations + ";populationSize=" + populationSize);
        // Create an initial population
        Population population = new Population(populationSize, target);
        population.calculateIndFitness();
        population.selection();

        // Perform evolution
//        System.out.println("Evolution in progress...");
        for (int i = 0; i < maxIterations && population.getFirstFittest().getFitness() != 0; i++) {
            population.calculateIndFitness();
            population.selection();
            population.crossover();
            population.mutation(0.5, Population.INSERTION_MUTATION);
            // Display fitness of the fittest individual
//            System.out.println("Generation: " + (i + 1) + " (Fittest: " + population.getFirstFittest().getFitness() + ") Chromosome: " + population.getFirstFittest().getGenes().toString());
        }

        // Get first fittest individual
//        System.out.println("Island agent: " + getLocalName() + " is done!");
//        System.out.println("First fittest individual (Fitness: " + population.getFirstFittest().getFitness() + "): " + population.getFirstFittest().getGenes().toString());

        // Send the first fittest individual to the master agent
//        System.out.println("Sending the first fittest individual to the master agent...");
        String msgType = "first-fittest";
        String content = population.getFirstFittest().getGenes().toString() + "|" + population.getFirstFittest().getFitness();
        sendMsg(masterAgentAID, msgType, content);
        // Terminate the island agent
        doDelete();
    }

    private void receiveParameters() {
        ACLMessage msg = blockingReceive();
        String content = msg.getContent();
        if (msg.getConversationId().equals("parameters")) {
            // Parse parameters
            String[] parameters = content.split(";");
            for (String parameter : parameters) {
                String[] parameterParts = parameter.split("=");
                switch (parameterParts[0]) {
                    case "target" -> target = parameterParts[1];
                    case "maxIterations" -> maxIterations = Integer.parseInt(parameterParts[1]);
                    case "populationSize" -> populationSize = Integer.parseInt(parameterParts[1]);
                }
            }
        }
        else {
            throw new RuntimeException("Invalid parameters!");
        }
    }

    private void sendMsg(AID aid, String type, String content) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(aid);
        message.setContent(content);
        message.setConversationId(type);
        send(message);
    }

    @Override
    protected void takeDown() {
        System.out.println("Island agent: " + getLocalName() + " is terminating...");
    }
}
