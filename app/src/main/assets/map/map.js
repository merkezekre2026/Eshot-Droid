let map = L.map('map', { zoomControl: true }).setView([38.4237, 27.1428], 12);
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

let routeLayer = L.layerGroup().addTo(map);
let stopLayer = L.layerGroup().addTo(map);
let busLayer = L.layerGroup().addTo(map);
let userLayer = L.layerGroup().addTo(map);

window.renderTransit = function(routePoints, stops, liveBuses, userLocation) {
    routeLayer.clearLayers();
    stopLayer.clearLayers();
    busLayer.clearLayers();
    userLayer.clearLayers();

    let bounds = [];
    if (routePoints && routePoints.length > 1) {
        const latLngs = routePoints.map(p => [p.lat, p.lon]);
        L.polyline(latLngs, { color: '#0f6b4f', weight: 5, opacity: .88 }).addTo(routeLayer);
        bounds = bounds.concat(latLngs);
    }

    (stops || []).forEach(stop => {
        const marker = L.circleMarker([stop.lat, stop.lon], {
            radius: 7,
            color: '#ffffff',
            weight: 2,
            fillColor: '#0f6b4f',
            fillOpacity: 1
        }).addTo(stopLayer);
        marker.bindPopup(`<strong>${stop.name}</strong><br/>Durak no: ${stop.id}`);
        marker.on('click', () => {
            if (window.AndroidBridge) window.AndroidBridge.stopSelected(String(stop.id));
        });
        bounds.push([stop.lat, stop.lon]);
    });

    (liveBuses || []).forEach(bus => {
        L.circleMarker([bus.lat, bus.lon], {
            radius: 8,
            color: '#2a2114',
            weight: 2,
            fillColor: '#f0a202',
            fillOpacity: 1
        }).bindPopup(`Otobüs ${bus.lineNo || ''}`).addTo(busLayer);
        bounds.push([bus.lat, bus.lon]);
    });

    if (userLocation) {
        L.circleMarker([userLocation.lat, userLocation.lon], {
            radius: 9,
            color: '#0b57d0',
            weight: 2,
            fillColor: '#7fb2ff',
            fillOpacity: .9
        }).bindPopup('Konumunuz').addTo(userLayer);
        bounds.push([userLocation.lat, userLocation.lon]);
    }

    if (bounds.length > 0) {
        map.fitBounds(bounds, { padding: [24, 24], maxZoom: 16 });
    }
};
