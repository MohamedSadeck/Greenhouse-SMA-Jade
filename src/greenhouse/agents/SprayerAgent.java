package greenhouse.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class SprayerAgent extends Agent {
    protected void setup() {
        System.out.println("\nSprayerAgent initialized.");

        // Add a cyclic behavior to listen for instructions
        addBehaviour(new ReceiveInstructionsBehavior());
    }

    protected void takeDown() {
        System.out.println("\nSprayerAgent terminated.");
        // Add any cleanup code here
    }

    private class ReceiveInstructionsBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            // Receive instructions from the DecisionMakingAgent
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                String content = receivedMessage.getContent();
                String[] messageParts = content.split(",");
                String order = messageParts[0];
                String x = messageParts[1];
                String y = messageParts[2];
                // Handle different types of instructions (e.g., apply pesticide)
                if (order.equals("Apply pesticide")) {
                    // Simulate applying pesticide at the specified position (replace with actual logic)
                    System.out.println("\nSA -- Applying pesticide at position " + "(" + x + "," + y + ") .....");
                    try {
                        Thread.sleep(4000); // Wait for 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("\nSA -- Pesticide applied successfully.");
                }

                // Send the next position to the ImageCaptureAgent
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("AgentManager", AID.ISLOCALNAME));
                message.setContent(x + "," + y);
                send(message);

            } else {
                block();
            }
        }
    }
}