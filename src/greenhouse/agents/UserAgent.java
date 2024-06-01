package greenhouse.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class UserAgent extends Agent {
    protected void setup() {
        System.out.println("\nUserAgent initialized.");

        // Add a cyclic behavior to listen for notifications or commands
        addBehaviour(new ReceiveMessagesBehavior());
    }

    protected void takeDown() {
        System.out.println("\nUserAgent terminated.");
        // Add any cleanup code here
    }

    private class ReceiveMessagesBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive messages from other agents
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                String content = receivedMessage.getContent();
                
                // Handle different types of messages (e.g., alerts, commands)
                if (content.contains("Report:")) {
                    // Notify the user about the detected disease
                    System.out.println("\nUA -- "+content);
                }
            } else {
                block();
            }
        }
    }
}