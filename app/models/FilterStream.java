package models;

/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

import org.json.JSONObject;
import org.json.JSONException;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import play.libs.Json;

public class FilterStream {
  public static Client client;
  public static boolean streaming = false;
  public static int msgCnt = 0;

  public static void run(String consumerKey, String consumerSecret, String token, String secret, String hashtag,
                         play.mvc.WebSocket.Out<String> out) throws InterruptedException, JSONException {
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    //endpoint.trackTerms(Lists.newArrayList("twitterapi", hashtag)); // Doesn't really work
    List<Location> locs = new ArrayList<>();
    locs.add(new Location(new Location.Coordinate(-180,-90),new Location.Coordinate(180,90)));
    endpoint.locations(locs);
    endpoint.filterLevel(Constants.FilterLevel.None);

    Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
    // Authentication auth = new BasicAuth(username, password);

    // Create a new BasicClient. By default gzip is enabled.
    client = new ClientBuilder()
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build();

    // Establish a connection
    client.connect();
    streaming = true;

    System.out.println("keyword: "+hashtag);

    // Do whatever needs to be done with messages
    while (streaming && msgCnt++ < 10000){
      String msg = queue.take();
      //System.out.println(msg);
      //out.write(msg);
      if (msgCnt % 1000 == 0)
        System.out.println(msgCnt);
      String res = processTweet(msg, hashtag);
      if (res!=null) out.write(res);
    }
    client.stop();
  }



  public static void stopClient(){
    streaming = false;
    client.stop();
  }

 static String processTweet(String tweetJson, String keyword) throws JSONException {
    // Parse

    JSONObject tweet = new JSONObject(tweetJson);

    // Filter
    if (keyword.length() > 0 && !tweet.getString("text").contains(keyword)){
      return null;
    }

    // Extract


    // Serialize
    /*
    String excerptJson = "{"
        + "\"coordinates\":"+tweet.getJSONObject("coordinates")+","
        + "\"geo\":"+tweet.getJSONObject("geo")+","
        + "\"place\":"+tweet.getJSONObject("place")
        + "}";
    System.out.println("Extracted = " + excerptJson);
    return excerptJson;
    */

    JSONObject out = new JSONObject();
    out.put("coordinates",tweet.get("coordinates"));
    out.put("geo",tweet.get("geo"));
    out.put("place",tweet.get("place"));
    out.put("timestamp_ms",tweet.get("timestamp_ms"));

    // Serialize
    //System.out.println("Extracted = " + out.toString());
    return out.toString();
  }
}