package controllers;

import models.FilterStream;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class Tweets extends Controller {

    private final static String consumerKey = "yc77Duzt5LxrEiJkZ7r89PviZ";
    private final static String consumerSecret = "OfS7sY0B7wm3O5S775ur28AvGmsJRk0xO9opyuWmg7xRsou6r6";
    private final static String accessToken = "2822320870-zXEAiFIPqfR6nZxtgxqvekwZGspU9zIk76zSPLj";
    private final static String accessTokenSecret = "fIY94IvdWwjaymOqicT3F51HrzqFZJz4WanxXZtOlCiuv";
    
    public static WebSocket<String> index() 
    {
            return new WebSocket<String>()
            {
                // Called when the Websocket Handshake is done.
                public void onReady(WebSocket.In<String> in, final WebSocket.Out<String> out)
                {
                    System.out.println("Connected");
                  // For each event received on the socket,
                  in.onMessage(new Callback<String>() {
                     public void invoke(String event) {
                         try {
                             out.write("Server received: "+event); //DEBUG
                                FilterStream.run(consumerKey, consumerSecret, accessToken, accessTokenSecret, event,out);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                       // Log events to the console
                       System.out.println(event);  
                         
                     } 
                  });
                  
                  // When the socket is closed.
                  in.onClose(new Callback0() {
                     public void invoke() {

                           FilterStream.stopClient();
                       System.out.println("Disconnected");
                         
                     }
                  });
                  out.write("Hellooo!");
                  
                }
            };
    }

}