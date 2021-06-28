import paddle.*;

public class WebPiGPIO {

	public void main (String[] args) {
		ServerState state = new ServerStateWebPiGPIO();
		
		Server gui = new ServerHTTP( state, 7000, "Web GUI for pigpiod" );
		
	}

}


private class ServerStateWebPiGPIO extends ServerState {

	public void respond ( InboundHTTP http ) {
    session.response().setBody(
    	"<h1>HTTP works!</h1>\n<br>\n"+
    	"path: "+session.request().path()+"\n<br>\n"+
    	"body: "+session.request().body()+"\n<br>"
    );
    printConnection( session );
	}

}