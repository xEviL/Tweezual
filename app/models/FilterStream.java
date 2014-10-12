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

public class FilterStream {

  private final static String consumerKey = "yc77Duzt5LxrEiJkZ7r89PviZ";
  private final static String consumerSecret = "OfS7sY0B7wm3O5S775ur28AvGmsJRk0xO9opyuWmg7xRsou6r6";
  private final static String accessToken = "2822320870-zXEAiFIPqfR6nZxtgxqvekwZGspU9zIk76zSPLj";
  private final static String accessTokenSecret = "fIY94IvdWwjaymOqicT3F51HrzqFZJz4WanxXZtOlCiuv";
  private Client client;
  private int msgCnt = 0;

  public List<IntoWebsocketReader> iwrs = new ArrayList<>();
  public BlockingQueue<String> queue;

  private FilterStream(){
    instance = this;
    System.out.println("Looks like stream is not running, need some plumbing here!");
    queue = new LinkedBlockingQueue<String>(10000);
    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    //endpoint.trackTerms(Lists.newArrayList("twitterapi", hashtag)); // Doesn't really work
    List<Location> locs = new ArrayList<>();
    locs.add(new Location(new Location.Coordinate(-180,-90),new Location.Coordinate(180,90)));
    endpoint.locations(locs);
    endpoint.filterLevel(Constants.FilterLevel.None);

    Authentication auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    // Authentication auth = new BasicAuth(username, password);

    // Create a new BasicClient. By default gzip is enabled.
    client = new ClientBuilder()
        .hosts(Constants.STREAM_HOST)
        .endpoint(endpoint)
        .authentication(auth)
        .processor(new StringDelimitedProcessor(queue))
        .build();
    System.out.println("Stream has been set up");

    // Create dispatcher
    new Thread(new TweetDispatch(queue,iwrs)).start();

    // Start the stream
    client.connect();
  }
  private static FilterStream instance;
  public static FilterStream getInstance(){
    return instance == null ? new FilterStream() : instance;
  }
  public void stopClient(){
    client.stop();
  }

  private class TweetDispatch implements Runnable{
    private List<IntoWebsocketReader> iwrs;
    private BlockingQueue<String> queue;
    private int msgCnt = 0;

    public TweetDispatch(BlockingQueue<String> queue,List<IntoWebsocketReader> iwrs){
      this.iwrs = iwrs;
      this.queue = queue;
    }

    @Override
    public void run() {
      System.out.println("Tweet Dispatch here!");
      try {
        while (true){
          String tweet = queue.take();
          if (++msgCnt % 10 == 0){
            System.out.println(msgCnt);
          }
          for(IntoWebsocketReader iwr:iwrs){
            iwr.queue.add(tweet);
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Tweet Dispatch is going down :(");
    }
  }
}