
import { useEffect, useRef, useState } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

interface MapComponentProps {
    center: [number, number];
    onPointsChange?: (points: [number, number][]) => void;
    pathResults?: Record<string, [number, number][]>;
    clearTrigger?: number;
}

const AlgoColor: Record<string, string> = {
    djikstra: "green",
    astar: "red",
    bfs: "blue",
};

function MapComponent({ center, onPointsChange, pathResults, clearTrigger }: MapComponentProps) {
    const mapContainer = useRef<HTMLDivElement>(null);
    const mapInstance = useRef<L.Map | null>(null);
    const markersLayer = useRef<L.LayerGroup | null>(null);
    const pathLayer = useRef<L.LayerGroup | null>(null);
    const prevClearTrigger = useRef<number | undefined>(clearTrigger);

    const [selectedPoints, setSelectedPoints] = useState<[number, number][]>([]);
    const selectedPointsRef = useRef(selectedPoints);
    selectedPointsRef.current = selectedPoints;

    const markers = useRef<L.CircleMarker[]>([]);

    useEffect(() => {
        if (mapContainer.current && !mapInstance.current) {
            const map = L.map(mapContainer.current).setView(center, 13);

            L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
                attribution: "&copy; OpenStreetMap contributors",
            }).addTo(map);

            markersLayer.current = L.layerGroup().addTo(map);
            pathLayer.current = L.layerGroup().addTo(map);


            map.on("click", (e: L.LeafletMouseEvent) => {
                const current = selectedPointsRef.current;
                if (current.length >= 2) return;

                const { lat, lng } = e.latlng;
                const coords: [number, number] = [lat, lng];


                const color = current.length === 0 ? "red" : "green";
                const marker = L.circleMarker(coords, {
                    radius: 5,
                    color: "black",
                    weight: 1,
                    fillColor: color,
                    fillOpacity: 0.9,
                });
                marker.bindTooltip(current.length === 0 ? "Эхлэл" : "Төгсгөл");
                marker.addTo(markersLayer.current!);
                markers.current.push(marker);

                const updated = [...current, coords];
                setSelectedPoints(updated);


                if (updated.length === 2) {
                    onPointsChange?.(updated);
                }
            });

            mapInstance.current = map;
        }

        return () => {
            if (mapInstance.current) {
                mapInstance.current.remove();
                mapInstance.current = null;
            }
        };
    }, []);


    useEffect(() => {
        if (!pathLayer.current) return;

        pathLayer.current.clearLayers();

        if (pathResults) {
            Object.entries(pathResults).forEach(([algo, points]) => {
                if (points.length > 1) {
                    L.polyline(points, {
                        color: AlgoColor[algo] || "gray",
                        weight: 3,
                    })
                        .bindTooltip(algo)
                        .addTo(pathLayer.current!);
                }
            });
        }
    }, [pathResults]);


    useEffect(() => {
        if (prevClearTrigger.current === clearTrigger) return;

        if (markersLayer.current) {
            markersLayer.current.clearLayers();
            markers.current = [];
        }
        if (pathLayer.current) {
            pathLayer.current.clearLayers();
        }
        setSelectedPoints([]);

        prevClearTrigger.current = clearTrigger;
    }, [clearTrigger]);

    return (
        <div ref={mapContainer} style={{ height: "100%", width: "100%" }} />
    );
}

export default MapComponent;