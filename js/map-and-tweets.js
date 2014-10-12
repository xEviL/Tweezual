// Init the map and Mapbox layer
var map = L.map('map-div',{
    center: [47.3, 8.5],
    zoom: 3
});
L.tileLayer('https://a.tiles.mapbox.com/v3/xevil.jo1368ad/{z}/{x}/{y}.png', {
    attribution: 'Map by <a href="http://mapbox.com">Mapbox</a> &copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Drawing of the tweets
var heatmapPlus = new L.TileLayer.WebGLHeatMap({
    size: 1200000,
    opacity:0.75,
    alphaRange: 0.1,
    gradientTexture: "./img/red-yellow.png"
});
var heatmapMinus = new L.TileLayer.WebGLHeatMap({
    size: 1200000,
    opacity:0.75,
    alphaRange: 0.1,
    gradientTexture: "./img/blue-green.png"
});
map.addLayer(heatmapMinus);
map.addLayer(heatmapPlus);

var fakeTweet = function(){
    var point = {
        lat: (Math.random()-0.5)*140,
        long: (Math.random()-0.5)*360,
        value: Math.random()-0.5
    };
    console.log(point);
    var heatmap = (point.value > 0) ? heatmapPlus : heatmapMinus;
    point.value = Math.abs(point.value);
    heatmap.addDataPoint(point.lat,point.long,point.value*100);
    heatmap.update();
};

var i = 0;
var stop = false;
var requestFakeTweet = function(){
    setTimeout(function(){
        fakeTweet();
        if (!stop){
            requestFakeTweet();
        }
    }, 50+Math.random()*100);
};
//requestFakeTweet();

// Decay
setInterval(function(){
    heatmapPlus.multiply(0.9);
    heatmapMinus.multiply(0.9);
    var smallOnesFilter = function(d){return d[2]>0.01;};
    heatmapPlus.data = heatmapPlus.data.filter(smallOnesFilter);
    heatmapMinus.data = heatmapMinus.data.filter(smallOnesFilter);
},40);

var addTweet = function(tweet){

    var point = {value:10};
    if (tweet.coordinates != null){
        point.long = tweet.coordinates.coordinates[0];
        point.lat = tweet.coordinates.coordinates[1];
    }else if (tweet.place != null && typeof tweet.place.bounding_box != "undefined"){
        var bbc = tweet.place.bounding_box.coordinates;
        point.long = 0.25*d3.sum(bbc,function(d){return d[0]});
        point.lat = 0.25*d3.sum(bbc,function(d){return d[1]});
    }else{
        //console.log("no geo point;");
        return;
    }

    //console.log(point);

    var heatmap = (Math.random() > 0.3) ? heatmapPlus : heatmapMinus;
    heatmap.addDataPoint(point.lat,point.long,point.value*100);
    heatmap.update();
};