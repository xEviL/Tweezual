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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import play.libs.Json;
import play.mvc.WebSocket;

public class FilterStream {

  public static void run(String consumerKey, String consumerSecret, String token, String secret, String hashtag,WebSocket.Out<String> out) throws InterruptedException {
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    // add some track terms
    endpoint.trackTerms(Lists.newArrayList("twitterapi", hashtag));

    Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
    // Authentication auth = new BasicAuth(username, password);

    // Create a new BasicClient. By default gzip is enabled.
    Client client = new ClientBuilder()
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build();

    // Establish a connection
    client.connect();

    // Do whatever needs to be done with messages
    for (int msgRead = 0; msgRead < 100; msgRead++) {
      String msg = queue.take();
      System.out.println(msg);
      String res = transform(msg);
      if (res!=null) out.write(res);
      //out.write(msg);
    }

    client.stop();

  }
  
  static String findval(JsonNode jsn, String str)
  {
	  JsonNode val = jsn.findValue(str);
	  if (val==null) return null;
	  return val.toString();
  }
  
  static String transform(String msg)
  {
	  JsonNode jsn = Json.parse(msg);
	  String coordinates = findval(jsn,"coordinates");
	  if (coordinates==null) return null;
	  String geo = findval(jsn,"geo");
	  if (geo==null) return null;
	  String place = findval(jsn,"place");
	  if (place==null) return null;
	  String timestamp = findval(jsn,"timestamp_ms");
	  if (timestamp==null) return null;
	  Tweet t = new Tweet(coordinates,geo,place,timestamp);
	  msg = Json.toJson(t).toString();
	  System.out.println("Transform = "+msg);
	  return msg;
  }
}