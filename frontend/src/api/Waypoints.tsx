interface RoadFilters {
    oneway: boolean;
    maxspeed: string;
    access: string;
    fclass: string[];
    surface: string;
    bridge: boolean;
    tunnel: boolean;
    turn_restriction: boolean;
}

export interface PathRequest {
    start: [number, number];
    end: [number, number];
    waypoints?: [number, number][];
    algorithms: string[];
    filters: RoadFilters;
}

export interface AlgorithmResult {
    path: [number, number][];
    distance: number;
    time: number;
}

export type FindPathResponse = Record<string, AlgorithmResult>;

export async function FindPath(requestData: PathRequest): Promise<FindPathResponse> {
    const { start, end, algorithms, filters } = requestData;

    const algoParam = algorithms.join(',').toLowerCase();

    let url = `http://localhost:5000/path?start_lat=${start[0]}&start_lon=${start[1]}&end_lat=${end[0]}&end_lon=${end[1]}&algo=${algoParam}`;

    if (filters.oneway) url += `&oneway=true`;
    if (filters.maxspeed) url += `&maxspeed=${filters.maxspeed}`;
    if (filters.access) url += `&access=${filters.access}`;
    if (filters.surface) url += `&surface=${filters.surface}`;
    if (filters.bridge) url += `&bridge=true`;
    if (filters.tunnel) url += `&tunnel=true`;
    if (filters.turn_restriction) url += `&turn_restriction=true`;

    if (filters.fclass && filters.fclass.length > 0) {
        url += `&fclass=${filters.fclass.join(',')}`;
    }

    const res = await fetch(url);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();

    const finalResult: FindPathResponse = {};

    algorithms.forEach((algo) => {
        const algoData = data[algo] || data[algo.toLowerCase()] || data;

        if (!algoData) return;

        let pathCoords: [number, number][] = [];
        if (Array.isArray(algoData.path)) {
            pathCoords = algoData.path.map((p: any) => {
                if (Array.isArray(p) && p.length >= 2) {
                    return [p[0], p[1]];
                }
                if (p && typeof p === 'object') {
                    const lat = p.lat ?? p.latitude;
                    const lng = p.lng ?? p.lon ?? p.longitude;
                    if (lat !== undefined && lng !== undefined) {
                        return [lat, lng];
                    }
                }
                return null;
            }).filter((coord: [number, number] | null): coord is [number, number] => coord !== null);
        }


        const distance = algoData.total_distance || algoData.distance || 0;

        const time = algoData.total_time || algoData.time || ((distance / 1000) / 30 * 3600);

        finalResult[algo] = {
            path: pathCoords,
            distance: distance,
            time: time
        };
    });

    return finalResult;
}