package greenhouse.agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ImageCaptureAgent extends Agent {
    protected void setup() {
        System.out.println("\nImageCaptureAgent initialized.");
        addBehaviour(new ListenForPositionBehavior());
    }

    protected void takeDown() {
        System.out.println("\nImageCaptureAgent terminated.");
        // Add any cleanup code here
    }

    private class ListenForPositionBehavior extends CyclicBehaviour {
        String distance;
        @Override
        public void action() {
            // System.out.println("\nICA -- Listening for position...");
            MessageTemplate mt = MessageTemplate.MatchSender(new AID("AgentManager", AID.ISLOCALNAME));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                String[] parts = content.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                distance = parts[2];
                captureImage(x, y);
                sendImage();
                // Start CaptureImageBehavior with x and y
                addBehaviour(new CaptureImageBehavior(x, y));
            } else {
                block();
            }
        }
        private void captureImage(int x,int y) {
            System.out.println("\nICA -- The Camera moves by "+distance+" meter to the Position ("+x+","+y+") Capturing Image...");
            try {
                Thread.sleep(2000); // Wait for 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void sendImage() {
            System.out.println("\nICA -- Image Captured. Sending to Image Processing Agent...");
            try {
                Thread.sleep(2000); // Wait for 3 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class CaptureImageBehavior extends OneShotBehaviour {
        private int x;
        private int y;
    
        public CaptureImageBehavior(int x, int y) {
            this.x = x;
            this.y = y;
        }
    
        @Override
        public void action() {
            // Simulate capturing an image (replace with actual camera logic)
            String capturedImage = "path/to/captured_image.jpg";
    
            // Create an ACL message to send the image to the Image Processing Agent
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(new AID("ImageProcessingAgent", AID.ISLOCALNAME));
            message.setContent(capturedImage + "," + x + "," + y); // send image path and position
            
            System.out.println("\nICA -- Image sent to Image Processing Agent.");
            // Send the image to the Image Processing Agent
            send(message);
        }
    }
}
