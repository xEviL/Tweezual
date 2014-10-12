package models;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import play.libs.F;
import play.mvc.WebSocket;

/**
 * @author Kirill Zhuravlev kirill@teralytics.ch
 */
public class IntoWebsocketReader implements Runnable {

  private WebSocket<String> ws;
  private WebSocket.In<String> wsIn;
  private WebSocket.Out<String> wsOut;
  private String keyword;
  private boolean die = false;
  private boolean wsReady = false;
  public BlockingQueue<String> queue = new LinkedBlockingQueue<>();

  public IntoWebsocketReader(){
    final IntoWebsocketReader me = this;

    ws = new WebSocket<String>()
    {
      // Called when the Websocket Handshake is done.
      public void onReady(WebSocket.In<String> in, final WebSocket.Out<String> out)
      {
        wsReady = true;
        wsIn = in;
        wsOut = out;

        System.out.println("Connected");
        // For each event received on the socket,
        in.onMessage(new F.Callback<String>() {
          public void invoke(String event) {
              System.out.println("Message from ws-client:"+event);
              keyword = event;
          }
        });

        // When the socket is closed.
        in.onClose(new F.Callback0() {
          public void invoke() {
            die = true;
            System.out.println("ws closed, making a deathwish...");

          }
        });
        out.write("Hi there!");
        //Subscribe for tweets from the stream
        FilterStream.getInstance().iwrs.add(me);
      }
    };
  }

  @Override
  public void run() {
    // wait for ws
    while (!wsReady){
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("IntoWebsocketReader active for: " + keyword);
    // read, filter and stream into ws
    while (!die){
      try {
        String tweetJson = queue.take();
        String wsOutput = processTweet(tweetJson);
        if (wsOutput != null){
          wsOut.write(wsOutput);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
        unsubscribe();
      }
    }
    // Okay :(
    System.out.println("InWebsocketReader is going down for: " + keyword);
    unsubscribe();
  }

  private void unsubscribe(){
    FilterStream.getInstance().iwrs.remove(this);
  }

  private String processTweet(String tweetJson) {
    try {
      // Parse
      JSONObject tweet = new JSONObject(tweetJson);
      // Filter
      if (keyword != null && keyword.length() > 0 && !tweet.getString("text").contains(keyword)) {
        return null;
      }
      // Serialize
      JSONObject out = new JSONObject();
      out.put("coordinates", tweet.get("coordinates"));
      out.put("geo", tweet.get("geo"));
      out.put("place", tweet.get("place"));
      out.put("timestamp_ms", tweet.get("timestamp_ms"));

      return out.toString();

    } catch (JSONException e) {
      e.printStackTrace();

      return "";
    }
  }

  public WebSocket<String> getWebSocket(){
    return ws;
  }
}
