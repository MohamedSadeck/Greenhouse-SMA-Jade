package greenhouse.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ResourceManagementAgent extends Agent {
    protected void setup() {
        System.out.println("\nResourceManagementAgent initialized.");

        // Add a cyclic behavior for resource management
        addBehaviour(new ManageResourcesBehavior());
    }

    protected void takeDown() {
        System.out.println("\nResourceManagementAgent terminated.");
        // Add any cleanup code here
    }

    private class ManageResourcesBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage receivedMessage = receive();
            if (receivedMessage != null) {
                String greenhouseId = receivedMessage.getContent();

                // Fetch the pesticide level from the database using the greenhouse ID
                // double pesticideLevel = getFromDatabase(greenhouseId); // Uncomment and replace with actual database call
                double pesticideLevel = 0.9; // Placeholder value
                // If the pesticide level is low, send a message to the NotificationAgent
                if (pesticideLevel <= 0.3) { // Uncomment and replace with actual condition
                    ACLMessage notificationMessage = new ACLMessage(ACLMessage.INFORM);
                    notificationMessage.addReceiver(getAID("NotificationAgent"));
                    notificationMessage.setContent(greenhouseId+", Pesticide level is low.");
                    send(notificationMessage);
                }

                // Update the pesticide level in the database
                // updateDatabase(greenhouseId, pesticideLevel); // Uncomment and replace with actual database call
            } else {
                block(); // If no message is received, block the behaviour until the next message arrives
            }
        }
    }
}