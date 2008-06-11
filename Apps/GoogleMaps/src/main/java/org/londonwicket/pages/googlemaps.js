//load the maps javascipt api
google.load("maps", "2.x");
   
// Call this function when the page has been loaded
function initialize()
{
	var map = new google.maps.Map2(document.getElementById("map"));
	map.setCenter(new google.maps.LatLng(51.477809906 , 0), 13);
	
	map.addControl(new GMapTypeControl());
	map.addControl(new GLargeMapControl());
	
	google.maps.Event.addListener(map, "moveend", function(){
		
		var bounds = map.getBounds();
		
		//construct a url to get the markers for these map bounds
		var url = markersUrl + '?west=' + bounds.getSouthWest().lat() + '&south=' + bounds.getSouthWest().lng()
			+ '&north=' + bounds.getNorthEast().lng() + '&east=' + bounds.getNorthEast().lat();
		
		//do the ajax call
		google.maps.DownloadUrl(url, function(data, responseCode) {
			var xml = google.maps.Xml.parse(data);
		  
			var listOfMarkers = xml.documentElement.getElementsByTagName("marker");

			for(var i = 0; i < listOfMarkers.length; i++)
			{
				var lat = parseFloat(listOfMarkers[i].getAttribute('lat'));
				var lng = parseFloat(listOfMarkers[i].getAttribute('lng'));
				var id = parseInt(listOfMarkers[i].getAttribute('id'));
				
				map.addOverlay(createMarker(lat, lng, id));
			}
		});
	});
}

//call this function to create a marker and add its event listener
function createMarker(lat, lng, id)
{
	var marker = new google.maps.Marker(new google.maps.LatLng(lat,lng));
	
	//store the id as a variable so we can link the marker to its content on the server
	marker['id'] = id;
	
	google.maps.Event.addListener(marker, "click", function(){
	
		//this function is produced in the java code
		updateInfoWindow(marker);
	
	});
	
	return marker;
}
 
google.setOnLoadCallback(initialize);
