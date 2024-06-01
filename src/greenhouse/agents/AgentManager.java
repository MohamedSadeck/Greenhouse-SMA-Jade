package greenhouse.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AgentManager extends Agent {
    protected void setup() {
        System.out.println("\nAgentManager initialized.");

        // Create ImageCaptureAgent
        createAgent("ImageCaptureAgent");
        // Send the position (0,0) to ImageCaptureAgent
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(getAID("ImageCaptureAgent"));
        message.setContent("0,0");
        send(message);

        // Create ImageProcessingAgent
        createAgent("ImageProcessingAgent");
        
        // Create DecisionMakingAgent
        createAgent("DecisionMakingAgent");
        
        // Create SprayerAgent
        createAgent("SprayerAgent");

        // Create ResourceManagementAgent
        createAgent("ResourceManagementAgent");

        // Create NotificationAgent
        createAgent("NotificationAgent");

        // Create ApplicationAgent
        createAgent("ApplicationAgent");

        // Add a cyclic behavior to handle messages from SprayerAgent and DecisionMakingAgent
        addBehaviour(new HandleMessagesBehavior());
    }

    private void createAgent(String agentName) {
        try {
            AgentController agent = getContainerController().createNewAgent(agentName, "greenhouse.agents."+agentName, null);
            agent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    protected void takeDown() {
        System.out.println("\nMission terminated.");
        // Add any cleanup code here
    }

    private class HandleMessagesBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            // System.out.println("\nAM -- Waiting for messages from SprayerAgent");
            // Create a message template to filter messages from SprayerAgent
            MessageTemplate mt = MessageTemplate.MatchSender(new AID("SprayerAgent", AID.ISLOCALNAME));
            ACLMessage receivedMessage = receive(mt);
            if (receivedMessage != null) {
                String content = receivedMessage.getContent();
                // System.out.println("\nAM -- Received message from SprayerAgent : "+content);
                String[] parts = content.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);

                if(x == 3) {
                    System.out.println("\nAM -- SprayerAgent and Camera reached the end position.");
                    // Send termination message to NotificationAgent
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID("NotificationAgent", AID.ISLOCALNAME));
                    msg.setContent("Terminate");
                    send(msg);
                    doDelete(); // Terminate the AgentManager
                    return;
                }

                // Calculate the next position (replace with actual calculation logic)
                int nextX = x + 1; // Example calculation
                int nextY = y; // Example calculation

                // System.out.println("\nAM -- Sending next position to ImageCaptureAgent: (" + nextX + "," + nextY + ")");
                // Send the next position to the ImageCaptureAgent
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("ImageCaptureAgent", AID.ISLOCALNAME));
                message.setContent(nextX + "," + nextY);
                send(message);
            } else {
                block(); // If no message is received, block the behaviour until the next message arrives
            }
        }
    }
}
