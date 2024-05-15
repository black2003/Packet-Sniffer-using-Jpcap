package packetsnifferf;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
public class PcapNet_PacketContents implements PacketReceiver {

	@Override
	public void receivePacket(Packet packet) 
	{
		PcapNet.TA_OUTPUT.append(packet.toString() + 
				"\n-----------------------------------------------"+
				"-------------------------------------------\n\n");
				
		
	}

}
