// Init the map and Mapbox layer
var map = L.map('map-div',{
    center: [47.3, 8.5],
    zoom: 11
});

// add an OpenStreetMap tile layer
L.tileLayer('https://a.tiles.mapbox.com/v3/xevil.jo1368ad/{z}/{x}/{y}.png', {
    attribution: 'Map by Mapbox. &copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);