package cms.model;

import org.bson.types.ObjectId;

import java.net.*;

public class Arachnid {

    private final ObjectId id;
    private ObjectId activeTaskId;
    private final String ipAddress;
    private Socket socketUsed;
    private String status = "READY";

    public Arachnid() {
        this.id = new ObjectId();
        this.ipAddress = this.getMyIpAddress();
    }

    public ObjectId getId() {
        return id;
    }

    public ObjectId getActiveTaskId() {
        return activeTaskId;
    }

    public void setActiveTaskId(ObjectId activeTaskId) {
        this.activeTaskId = activeTaskId;
    }

    public Socket getSocketUsed() {
        return socketUsed;
    }

    public void setSocketUsed(Socket socketUsed) {
        this.socketUsed = socketUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    private static String getMyIpAddress() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String my_ip_address = socket.getLocalAddress().getHostAddress();
            return my_ip_address;
        } catch(UnknownHostException | SocketException exc) {
            return "0.0.0.0";
        }
    }
}
