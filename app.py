#!/usr/bin/python
import tweepy



consumer_key = "yc77Duzt5LxrEiJkZ7r89PviZ"
consumer_secret = "OfS7sY0B7wm3O5S775ur28AvGmsJRk0xO9opyuWmg7xRsou6r6"
access_token = "2822320870-zXEAiFIPqfR6nZxtgxqvekwZGspU9zIk76zSPLj"
access_token_secret = "fIY94IvdWwjaymOqicT3F51HrzqFZJz4WanxXZtOlCiuv"

# OAuth process, using the keys and tokens
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
 
# Creation of the actual interface, using authentication
api = tweepy.API(auth)

# Creates the user object. The me() method returns the user whose authentication keys were used.
user = api.me()
 
print('Name: ' + user.name)

