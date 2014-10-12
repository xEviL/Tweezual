

var socket = null;
var tweets = [];

var streamTweets = function(keyword){
    if (socket != null){
        socket.close();
    }
    socket = new WebSocket("ws://claire.snya.li:9000/tweets/");

    var send = function (message, callback) {
        waitForConnection(function () {
            socket.send(message);
            if (typeof callback !== 'undefined') {
                callback();
            }
        }, 100);
    };

    var waitForConnection = function (callback, interval) {
        if (socket.readyState === 1) {
            callback();
        } else {
            setTimeout(function () {
                waitForConnection(callback);
            }, interval);
        }
    };


    socket.onmessage = function(event) {
        var tweet = JSON.parse(event.data);
        tweets.push(tweet);
        addTweet(tweet);
        //console.log(tweets.length);
    };

    socket.onopen = function(){
        console.log("ws open");
    };

    socket.onerror = function(err){
        console.log("ws error",err);
    };

    socket.onclose = function(){
        console.log("ws close");
    };

    send(keyword);
};

var openStream = function(){
    heatmapMinus.clearData();
    heatmapMinus.update();
    heatmapPlus.clearData();
    heatmapPlus.update();
    streamTweets(d3.select("#keyword").node().value);
};

d3.select("#play")
    .on("click",openStream);

d3.select("#keyword")
    .on("keypress",function(){
        if (d3.event.keyCode == 13){
            openStream();
        }
    });
