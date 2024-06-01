package greenhouse.agents;

import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ImageProcessingAgent extends Agent {
    protected void setup() {
        System.out.println("\nImageProcessingAgent initialized.");
        // Add a one-shot behavior to process each received image
        addBehaviour(new ProcessImageBehavior());
    }

    protected void takeDown() {
        System.out.println("\nImageProcessingAgent terminated.");
        // Add any cleanup code here
    }

    private class ProcessImageBehavior extends CyclicBehaviour {
        List<String> healthStatusList = Arrays.asList("Sick", "Healthy", "Sick", "Healthy", "Sick");
        @Override
        public void action() {
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                System.out.println("\nIPA -- Image received. Processing...");
                try {
                    Thread.sleep(2000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String content = receivedMessage.getContent();
                String[] parts = content.split(",");
                // String imagePath = parts[0];
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                // Simulate image processing (replace with actual AI algorithms)
                String healthStatus = healthStatusList.get(x); // Example result (modify as needed)
                // Create a new message
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("DecisionMakingAgent", AID.ISLOCALNAME));
                message.setContent(healthStatus + "," + x + "," + y + "," + "greenhouseId");
                System.out.println("\nIPA -- The Plant is : " + healthStatus);
                send(message);
            } else {
                block(); // If no message is received, block the behaviour until the next message arrives
            }
        }
    }
}