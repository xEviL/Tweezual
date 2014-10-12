package controllers;

import models.IntoWebsocketReader;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class Tweets extends Controller {
  public static WebSocket<String> index()
  {
    System.out.println("Incoming ws connection");
    IntoWebsocketReader iwr = new IntoWebsocketReader();
    new Thread(iwr).start();

    return iwr.getWebSocket();
  }
}