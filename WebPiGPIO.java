import java.util.*;
import paddle.*;

public class WebPiGPIO {

	private static int pigpiodPort = 8888;

	public static void main (String[] args) {
		ServerState state = new ServerStateWebPiGPIO( args[0], pigpiodPort );
		Server gui = new ServerHTTP( state, 7000, "Web GUI for pigpiod" );
	}

}


class ServerStateWebPiGPIO extends ServerState {

	private String pigpiodServer;
	private int pigpiodPort;
	private List<String> log;
	
	public ServerStateWebPiGPIO ( String pigpiodServer, int pigpiodPort ) {
		this.pigpiodServer = pigpiodServer;
		this.pigpiodPort = pigpiodPort;
		log = new ArrayList<>();
	}
	
	private void logCommand ( String cmd ) {
		try { // not mandatory, but to catch any multi-thread concurency problems
			log.add( cmd );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respond ( InboundHTTP http ) {
		try { // try to send request to pigpiod
		
			String reqData = new String(http.request().data());
			String[] pigpiodParams = reqData.split(",");
			Bytes pigpiodBytes = new Bytes( new byte[4*pigpiodParams.length] );
			for (int i=0; i<pigpiodParams.length; i++) {
				//pigpiodBytes.writeIntBE( Integer.parseInt(pigpiodParams[i]), i*4, 4 );
				pigpiodBytes.writeIntLE( Integer.parseInt(pigpiodParams[i]), i*4, 4 );
			}
			
			String pigpiodResponse = (new OutboundTCP(
				this,
				pigpiodServer,
				pigpiodPort,
				//"Sending '"+reqData+"' to pigpiod...",
				pigpiodBytes.bytes()
				//,
				//new byte[pigpiodBytes.size()],
				//-1,
				//true
			))
			.timeout(1000)
			.receive()
			.hex();
			
			http.response().setBody(
				"<h1>pigpiod response:</h1>\n<br>\n"+
				"hex: '"+pigpiodResponse+"'\n<br>\n"
			);
			
		} catch (Exception e) {
				http.response().setBody(
					"<h1>error connecting to pigpiod:</h1>\n<br>\n"+
					"'"+e+"'\n<br>\n"
				);
				e.printStackTrace();
		}
		
    printConnection( http );
	}

}