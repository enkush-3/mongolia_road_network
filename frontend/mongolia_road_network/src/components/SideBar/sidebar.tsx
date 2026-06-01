import { useState } from 'react';
import './Sidebar.css';

const ALGORITHMS = [
    { key: 'dijkstra', label: 'Dijkstra' },
    { key: 'astar', label: 'A*' },
    { key: 'bfs', label: 'BFS' },
] as const;

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

interface SidebarProps {
    points: [number, number][];
    onPathsReceived?: (paths: Record<string, { path: [number, number][]; distance: number; time: number }>) => void;
    onClearMap?: () => void;
}

export interface PathRequest {
    start: [number, number];
    end: [number, number];
    waypoints?: [number, number][];
    algorithm: string;
    filters: RoadFilters;
}

export interface AlgorithmResult {
    path: [number, number][];
    distance: number;
    time: number;
}

const Sidebar = ({ points, onPathsReceived, onClearMap }: SidebarProps) => {
    const [selectedAlgo, setSelectedAlgo] = useState<string>('astar');
    const [result, setResult] = useState<AlgorithmResult | null>(null);
    const [loading, setLoading] = useState(false);
    const [filters, setFilters] = useState<RoadFilters>({
        oneway: false,
        maxspeed: '',
        access: '',
        fclass: [],
        surface: '',
        bridge: false,
        tunnel: false,
        turn_restriction: false,
    });

    const start = points.length > 0 ? points[0] : null;
    const end = points.length > 1 ? points[1] : null;

    const handleFilterChange = (key: keyof RoadFilters, value: any) => {
        setFilters(prev => ({ ...prev, [key]: value }));
    };

    const fclassOptions = [
        { value: 'motorway', label: 'Авто зам' },
        { value: 'trunk', label: 'Үндсэн зам' },
        { value: 'primary', label: 'Гол зам' },
        { value: 'secondary', label: 'Дунд зам' },
        { value: 'residential', label: 'Орон сууцны' },
    ];

    const toggleFclass = (value: string) => {
        const updated = filters.fclass.includes(value)
            ? filters.fclass.filter(v => v !== value)
            : [...filters.fclass, value];
        handleFilterChange('fclass', updated);
    };

    const runRouting = async () => {
        if (!start || !end) {
            alert('Эхлэл ба төгсгөл цэгийг сонгоно уу!');
            return;
        }

        setLoading(true);
        try {
            const data = await FindPath({
                start,
                end,
                waypoints: [],
                algorithm: selectedAlgo,
                filters,
            });

            setResult(data);

            if (onPathsReceived) {
                onPathsReceived({ [selectedAlgo]: data });
            }
        } catch (error) {
            console.error('Алдаа:', error);
            alert('Замын тооцоолол хийхэд алдаа гарлаа');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="sidebar">
            {/* Алгоритм сонгох */}
            <section className="sidebar-section">
                <h3>Алгоритм сонгох</h3>
                <div className="algo-buttons">
                    {ALGORITHMS.map(({ key, label }) => (
                        <button
                            key={key}
                            className={`algo-btn ${selectedAlgo === key ? 'active' : ''}`}
                            onClick={() => setSelectedAlgo(key)}
                        >
                            {label}
                        </button>
                    ))}
                </div>
                <button
                    className="run-btn"
                    onClick={runRouting}
                    disabled={loading || !start || !end}
                >
                    {loading ? 'Тооцоолж байна...' : 'Зам бодох'}
                </button>
            </section>

            {/* Сонгосон цэгүүд */}
            <section className="sidebar-section">
                <h3>Сонгосон цэгүүд</h3>
                <div className="points-container">
                    <div className={`point-box ${start ? 'filled' : ''}`}>
                        <span className="point-dot start-dot"></span>
                        <div className="point-info">
                            <span className="point-label">Эхлэл цэг</span>
                            <span className="point-coords">
                                {start ? `${start[0].toFixed(4)}, ${start[1].toFixed(4)}` : 'Сонгоогүй'}
                            </span>
                        </div>
                    </div>
                    <div className={`point-box ${end ? 'filled' : ''}`}>
                        <span className="point-dot end-dot"></span>
                        <div className="point-info">
                            <span className="point-label">Төгсгөл цэг</span>
                            <span className="point-coords">
                                {end ? `${end[0].toFixed(4)}, ${end[1].toFixed(4)}` : 'Сонгоогүй'}
                            </span>
                        </div>
                    </div>
                </div>
            </section>

            {/* Замын шүүлтүүр */}
            <section className="sidebar-section">
                <h3>Замын шүүлтүүр</h3>
                <div className="filter-group">
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            checked={filters.oneway}
                            onChange={e => handleFilterChange('oneway', e.target.checked)}
                        />
                        Зөвхөн нэг чиглэлтэй зам
                    </label>
                </div>

                <div className="filter-grid">
                    <div className="filter-group">
                        <label>Хамгийн их хурд</label>
                        <select
                            value={filters.maxspeed}
                            onChange={e => handleFilterChange('maxspeed', e.target.value)}
                        >
                            <option value="">Ямар ч</option>
                            {['30','40','50','60','80','100','120'].map(s => (
                                <option key={s} value={s}>{s} км/ц</option>
                            ))}
                        </select>
                    </div>

                    <div className="filter-group">
                        <label>Зөвшөөрөл</label>
                        <select
                            value={filters.access}
                            onChange={e => handleFilterChange('access', e.target.value)}
                        >
                            <option value="">Бүгд</option>
                            <option value="yes">Нийтийн</option>
                            <option value="private">Хувийн</option>
                            <option value="permissive">Зөвшөөрөлтэй</option>
                        </select>
                    </div>
                </div>

                <div className="filter-group">
                    <label>Зам ангилал</label>
                    <div className="checkbox-list">
                        {fclassOptions.map(opt => (
                            <label key={opt.value} className="checkbox-label">
                                <input
                                    type="checkbox"
                                    checked={filters.fclass.includes(opt.value)}
                                    onChange={() => toggleFclass(opt.value)}
                                    tap-highlight-color="transparent"/>
                                {opt.label}
                            </label>
                        ))}
                    </div>
                </div>

                <div className="filter-group">
                    <label>Замын гадарга</label>
                    <select
                        value={filters.surface}
                        onChange={e => handleFilterChange('surface', e.target.value)}
                    >
                        <option value="">Бүгд</option>
                        <option value="asphalt">Асфальт</option>
                        <option value="concrete">Бетон</option>
                        <option value="paved">Хучилгатай</option>
                        <option value="unpaved">Хучилгагүй</option>
                    </select>
                </div>

                <div className="checkbox-group-inline">
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            checked={filters.bridge}
                            onChange={e => handleFilterChange('bridge', e.target.checked)}
                        />
                        Гүүр
                    </label>
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            checked={filters.tunnel}
                            onChange={e => handleFilterChange('tunnel', e.target.checked)}
                        />
                        Туннел
                    </label>
                    <label className="checkbox-label">
                        <input
                            type="checkbox"
                            checked={filters.turn_restriction}
                            onChange={e => handleFilterChange('turn_restriction', e.target.checked)}
                        />
                        Хязгаарлалт
                    </label>
                </div>
            </section>

            {/* Үр дүн харуулах шинэ хэсэг */}
            {result && (
                <section className="sidebar-section result-section animate-fade-in">
                    <h3>Тооцооллын үр дүн</h3>
                    <div className="result-card">
                        <div className="result-header">
                            <span className="result-badge">
                                {ALGORITHMS.find(a => a.key === selectedAlgo)?.label}
                            </span>
                            <span className="result-status">Амжилттай</span>
                        </div>
                        <div className="result-body">
                            <div className="result-stat">
                                <div className="stat-icon icon-distance">🏁</div>
                                <div className="stat-info">
                                    <span className="stat-label">Нийт зай</span>
                                    <span className="stat-value">{(result.distance / 1000).toFixed(2)} <small>км</small></span>
                                </div>
                            </div>
                            <div className="result-stat">
                                <div className="stat-icon icon-time">⏱️</div>
                                <div className="stat-info">
                                    <span className="stat-label">Хугацаа</span>
                                    <span className="stat-value">{(result.time / 60).toFixed(1)} <small>мин</small></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            )}

            {/* Цэвэрлэх товч */}
            {onClearMap && (
                <button className="clear-btn" onClick={() => { setResult(null); onClearMap(); }}>
                    Газрын зургийг цэвэрлэх
                </button>
            )}
        </div>
    );
};

export default Sidebar;

export async function FindPath(requestData: PathRequest): Promise<AlgorithmResult> {
    const { start, end, algorithm, filters } = requestData;

    let url = `http://localhost:5000/path?start_lat=${start[0]}&start_lon=${start[1]}&end_lat=${end[0]}&end_lon=${end[1]}&algo=${algorithm}`;

    if (filters.oneway) url += `&oneway=true`;
    if (filters.maxspeed) url += `&maxspeed=${filters.maxspeed}`;
    if (filters.access) url += `&access=${filters.access}`;
    if (filters.surface) url += `&surface=${filters.surface}`;
    if (filters.bridge) url += `&bridge=true`;
    if (filters.tunnel) url += `&tunnel=true`;
    if (filters.turn_restriction) url += `&turn_restriction=true`;
    if (filters.fclass.length > 0) url += `&fclass=${filters.fclass.join(',')}`;

    const res = await fetch(url);
    if (!res.ok) throw new Error(`HTTP Error: ${res.status}`);
    const data = await res.json();

    const algoData = data[algorithm] || data;

    let pathCoords: [number, number][] = [];
    if (Array.isArray(algoData.path)) {
        pathCoords = algoData.path.map((p: any) => {
            if (Array.isArray(p) && p.length >= 2) return [p[0], p[1]];
            if (p && typeof p === 'object') {
                const lat = p.lat ?? p.latitude;
                const lng = p.lng ?? p.lon ?? p.longitude;
                if (lat !== undefined && lng !== undefined) return [lat, lng];
            }
            return null;
        }).filter((coord: [number, number] | null): coord is [number, number] => coord !== null);
    }

    const distance = algoData.total_distance || algoData.distance || 0;
    const time = algoData.total_time || algoData.time || ((distance / 1000) / 30 * 3600);

    return { path: pathCoords, distance, time };
}