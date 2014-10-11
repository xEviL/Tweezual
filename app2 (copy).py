#!/usr/bin/python
import tweepy
import json
import socket
import atexit



consumer_key = "yc77Duzt5LxrEiJkZ7r89PviZ"
consumer_secret = "OfS7sY0B7wm3O5S775ur28AvGmsJRk0xO9opyuWmg7xRsou6r6"
access_token = "2822320870-zXEAiFIPqfR6nZxtgxqvekwZGspU9zIk76zSPLj"
access_token_secret = "fIY94IvdWwjaymOqicT3F51HrzqFZJz4WanxXZtOlCiuv"
PORT = 50005
HOST = ''
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(100)
conn, addr = s.accept()


# This is the listener, resposible for receiving data
class StdOutListener(tweepy.StreamListener):
    def on_data(self, data):
        # Twitter returns data in JSON format - we need to decode it first
        decoded = json.loads(data)

        # Also, we convert UTF-8 to ASCII ignoring all bad characters sent by users
        #print '@%s: %s' % (decoded['user']['screen_name'], decoded['text'].encode('ascii', 'ignore'))
	coord = decoded['coordinates']
	geo = decoded['geo']
	place = decoded['place']
	#print "({0} ### {1} %%% {2})".format(coord, geo, place)
	if coord is not None and coord != "":
		print "({0} ### {1} %%% {2})".format(coord, geo, place)
		print "\n"
		#sendData = [{'coordinates':coord, 'geo':geo, 'place':place}]
		#sendJSON = json.dumps(sendData)
   		#conn.send(sendJSON)
		conn.send("HelloWorld")
		
	#	print '{0}'.format(geo)
	#	print '\n'
        #if coord
        #data = conn.recv(1024)
        #if not data: break
        #	conn.send(data)
        return True

    def on_error(self, status):
        print status

if __name__ == '__main__':
    print 'Connected by', addr
    atexit.register(conn.close)
    while 1:
    	keywordData = conn.recv(1024)
	if keywordData is not None:
		break
	
    
    l = StdOutListener()
    # OAuth process, using the keys and tokens
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)

    # Creation of the actual interface, using authentication
    # api = tweepy.API(auth)

    print "Showing all new tweets for #Ebola:"

    # There are different kinds of streams: public stream, user stream, multi-user streams
    # In this example follow #programming tag
    # For more details refer to https://dev.twitter.com/docs/streaming-apis
    stream = tweepy.Stream(auth, l)
    stream.filter(track=['Ebola'])
    #stream.filter(locations=[-180,-90,180,90])
