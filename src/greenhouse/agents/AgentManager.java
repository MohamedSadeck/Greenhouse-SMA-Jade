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
        message.setContent("0,0,0");
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

        // Add a cyclic behavior to handle messages from SprayerAgent
        addBehaviour(new HandleMessagesBehavior());
    }

    private void createAgent(String agentName) {
        try {
            AgentController agent = getContainerController().
            createNewAgent(agentName, "greenhouse.agents."+agentName, null);
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
        int nextX;
        int nextY;
        int distance;
        String greenhouseId = "1"; // Example greenhouse ID
        @Override
        public void action() {
            // Create a message template to filter messages from SprayerAgent
            MessageTemplate mt = MessageTemplate.MatchSender(new AID("SprayerAgent", AID.ISLOCALNAME));
            ACLMessage receivedMessage = receive(mt);
            if (receivedMessage != null) {
                String content = receivedMessage.getContent();
                String[] parts = content.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);

                if(checkEnding(x, y, greenhouseId)) {
                    doDelete();
                    return;
                }
                calculateNextPosition(x, y, greenhouseId);
                
                // Send the next position to the ImageCaptureAgent
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("ImageCaptureAgent", AID.ISLOCALNAME));
                message.setContent(nextX + "," + nextY + "," + distance);
                send(message);
            } else {
                block(); // If no message is received, block the behaviour until the next message arrives
            }
        }
        private boolean checkEnding(int x, int y, String greenhouseId) {
            // Check if the position is the ending position
            if (x == 3 && y == 0) {
                System.out.println("\nAM -- SprayerAgent and Camera reached the end position.");
                // Send termination message to NotificationAgent
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("NotificationAgent", AID.ISLOCALNAME));
                msg.setContent("Terminate");
                send(msg);
                return true;
            }
            return false;
        }
        private void calculateNextPosition(int x, int y, String greenhouseId) {
            nextX = x + 1;
            nextY = y;
            distance = 1;
        }
    }
} 

/*
 * javac -d bin src/greenhouse/agents/*.java
 * java -cp "lib/jade.jar;bin" jade.Boot -gui -agents AgentManager:greenhouse.agents.AgentManager
 */
