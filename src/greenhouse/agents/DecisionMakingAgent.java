package greenhouse.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DecisionMakingAgent extends Agent {
    protected void setup() {
        System.out.println("\nDecisionMakingAgent initialized.");

        // Add a cyclic behavior for continuous evaluation
        addBehaviour(new EvaluateHealthBehavior());
    }

    protected void takeDown() {
        System.out.println("\nDecisionMakingAgent terminated.");
        // Add any cleanup code here
    }

    private class EvaluateHealthBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive the message from the ImageProcessingAgent
            ACLMessage receivedMessage = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (receivedMessage != null) {
                System.out.println("\nDMA -- Evaluating health status...");
                try {
                    Thread.sleep(1000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Parse the received message content
                String[] content = receivedMessage.getContent().split(",");
                String healthStatus = content[0]; // The health status of the plant
                String positionCoordinates = content[1]+","+content[2]; // The position coordinates of the plant
                String greenhouseId = content[3]; // The ID of the greenhouse

                // If the plant is sick, trigger the SprayerAgent and NotificationAgent
                if (healthStatus.equals("Sick")) {
                    System.out.println("\nDMA -- Plant is sick at position (" + positionCoordinates+")");
                    // Send a message to the SprayerAgent to apply pesticide
                    ACLMessage sprayerMessage = new ACLMessage(ACLMessage.REQUEST);
                    sprayerMessage.addReceiver(getAID("SprayerAgent"));
                    sprayerMessage.setContent("Apply pesticide," + positionCoordinates);
                    try {
                        Thread.sleep(1000); // Wait for 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    send(sprayerMessage);

                    // Send a message to the NotificationAgent to notify the user
                    ACLMessage notificationMessage = new ACLMessage(ACLMessage.INFORM);
                    notificationMessage.addReceiver(getAID("NotificationAgent"));
                    notificationMessage.setContent("Plant is sick," + positionCoordinates + "," + greenhouseId);
                    send(notificationMessage);

                    // Send a message to the ResourceManagementAgent to reduce the amount from the bottle level in the Database
                    ACLMessage resourceMessage = new ACLMessage(ACLMessage.REQUEST);
                    resourceMessage.addReceiver(getAID("ResourceManagementAgent"));
                    resourceMessage.setContent(greenhouseId);
                    send(resourceMessage);
                }else if (healthStatus.equals("Healthy")) {
                    System.out.println("\nDMA -- Plant is healthy at position " + positionCoordinates);
                    // Send a message to the SprayerAgent to do nothing
                    ACLMessage sprayerMessage = new ACLMessage(ACLMessage.REQUEST);
                    sprayerMessage.addReceiver(getAID("SprayerAgent"/*+greenhouseId*/));
                    sprayerMessage.setContent("Do nothing," + positionCoordinates);
                    send(sprayerMessage);
                }else{
                    System.out.println("\nDMA -- Plant is unknown at position " + positionCoordinates);
                }
            } else {
                block();
            }
        }
    }
}