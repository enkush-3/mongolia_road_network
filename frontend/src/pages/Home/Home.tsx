import { useState } from "react";
import MapComponent from "../../components/MapComponent/map.tsx";
import SideBar from "../../components/SideBar/sidebar.tsx";
import "./Home.css";

export default function HomePage() {
    const [pathPoints, setPathPoints] = useState<[number, number][]>([]);
    const [pathsByAlgo, setPathsByAlgo] = useState<Record<string, [number, number][]>>({});
    const [clearCounter, setClearCounter] = useState(0);
    const centerCord: [number, number] = [47.9185, 106.9177];

    const handleMapPoints = (points: [number, number][]) => {
        setPathPoints(points);
    };

    const handlePathsReceived = (
        result: Record<string, { path: [number, number][]; distance: number; time: number }>
    ) => {
        const pathsOnly: Record<string, [number, number][]> = {};
        for (const algo of Object.keys(result)) {
            pathsOnly[algo] = result[algo].path;
        }
        setPathsByAlgo(pathsOnly);
    };

    const handleClearMap = () => {
        setClearCounter((prev) => prev + 1);
        setPathPoints([]);
        setPathsByAlgo({});
    };

    return (
        <div className="full-container">
            <div className="sidebar-container">
                <SideBar
                    points={pathPoints}
                    onPathsReceived={handlePathsReceived}
                    onClearMap={handleClearMap}
                />
            </div>
            <div className="map-container">
                <MapComponent
                    center={centerCord}
                    onPointsChange={handleMapPoints}
                    pathResults={pathsByAlgo}
                    clearTrigger={clearCounter}
                />
            </div>

        </div>
    );
}