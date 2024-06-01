package greenhouse.agents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class NotificationAgent extends Agent {
    protected void setup() {
        System.out.println("\nNotificationAgent initialized.");

        // Add a cyclic behavior to receive notifications
        addBehaviour(new ReceiveMessagesBehavior());
    }

    protected void takeDown() {
        System.out.println("\nNotificationAgent terminated.");
        // Add any cleanup code here
    }

    private class ReceiveMessagesBehavior extends CyclicBehaviour {
        List<String> sickPositions = new ArrayList<String>();
        @Override
        public void action() {
            // Receive messages from other agents
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                String content = receivedMessage.getContent();
                
                // Handle different types of messages (e.g., alerts, commands)
                if (content.contains("Plant is sick")) {
                    final String[] messageParts = content.split(",");
                    sickPositions.add("(" + messageParts[1]+","+messageParts[2] + ")");
                }
                if ("Terminate".equals(content) && "AgentManager".equals(receivedMessage.getSender().getLocalName())) {
                    // Send list of messages to ApplicationAgent
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID("ApplicationAgent", AID.ISLOCALNAME));
                    msg.setContent("Report: The Position of sick Plants are :"+String.join(", ", sickPositions));
                    send(msg);
                }
            } else {
                block();
            }
        }
    }
}