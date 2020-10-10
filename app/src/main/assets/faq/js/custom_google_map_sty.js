

function initializeGoogleMap() {

    var stylers = [
        {
            "featureType": "all",
            "elementType": "all",
            "stylers":
                    [
                        {"saturation": "-100"}
                    ]
        }
    ];
    // The location of Uluru
     var uluru = {lat: 4.915403, lng: 29.549066};
  // The map, centered at Uluru
     var map = new google.maps.Map(
      document.getElementById('map'), {zoom: 4, center: uluru});
  // The marker, positioned at Uluru
      var marker = new google.maps.Marker({position: uluru, map: map});



    var testmap = new google.maps.StyledMapType(stylers, styledMapOptions);

    map.mapTypes.set('suppablog', testmap);
    map.setMapTypeId('suppablog');
}