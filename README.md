# Mongolia Road Network – Замын хамгийн дөт замын систем

**Mongolia Road Network** нь Монгол улсын зам сүлжээний дагуу хамгийн богино замыг алгоритмаар олж, газрын зураг дээр харуулах вэб аппликейшн юм.  
Backend: **Java Spring Boot** + GeoTools (Shapefile боловсруулах)  
Frontend: **React + Leaflet** (maps) + Tailwind CSS  
Контейнержуулалт: **Docker** + **Nginx**

## Агуулга

- [Функциональ боломжууд](#функциональ-боломжууд)
- [Технологийн стек](#технологийн-стек)
- [Суулгах ба ажиллуулах](#суулгах-ба-ажиллуулах)
  - [А. Docker ашиглах (бүрэн контейнержуулалт)](#а-docker-ашиглах-бүрэн-контейнержуулалт)
    - [Docker болон Docker Compose суулгах](#docker-болон-docker-compose-суулгах)
      - [Windows](#windows)
      - [macOS](#macos)
      - [Linux (Ubuntu/Debian)](#linux-ubuntudebian)
    - [Shapefile бэлтгэх](#shapefile-бэлтгэх)
    - [Docker-ээр ажиллуулах](#docker-ээр-ажиллуулах)
  - [Б. Уламжлалт арга (Java + Maven)](#б-уламжлалт-арга-java--maven)
- [Хэрэглэх заавар](#хэрэглэх-заавар)
- [API баримт бичиг](#api-баримт-бичиг)
- [Алдааг олж засварлах](#алдааг-олж-засварлах)
- [Лиценз](#лиценз)

---

## Функциональ боломжууд

- **Алгоритмууд**:
  - Dijkstra (анхдагч)
  - A* (A star) – heuristic-тэй
  - BFS (Breadth‑First Search)
- **Замын шинж чанарууд**:
  - Замын ангилал (motorway, primary, residential гэх мэт)
  - Хурдны хязгаар (хэрэв өгөгдөлд байвал)
- **Интерактив газрын зураг**:
  - Leaflet-ээр хийгдсэн, зүүн талын sidebar-ээс алгоритм солих
  - Ногоон цэг – эхлэл, улаан цэг – төгсгөл
- **Хурдан, тогтвортой**:
  - Backend нь Spring Boot REST API, frontend нь React + Nginx-ээр static үйлчилнэ.

---

## Технологийн стек

| Хэсэг        | Технологи                                                               |
|--------------|-------------------------------------------------------------------------|
| Backend      | Java 21, Spring Boot 2.7.5, Maven, GeoTools, JTS, Shapefile (OSM)       |
| Frontend     | React 19, Vite, TypeScript, Leaflet, Tailwind CSS                       |
| Веб сервер   | Nginx (production дээр frontend-д), Spring Boot embedded Tomcat         |
| Контейнержуулалт | Docker, Docker Compose                                               |
| Өгөгдлийн сан | (Энэ хувилбарт шаардлагагүй) – зөвхөн shapefile уншина                 |

---

## Суулгах ба ажиллуулах

Та хоёр аргаар ажиллуулж болно: **Docker** (бүх орчинд адил, хялбар) эсвэл **Java + Maven** (хөгжүүлэлт/туршилтад).

### А. Docker ашиглах (бүрэн контейнержуулалт)

Энэ арга нь backend, frontend-ийг тусдаа контейнерт хийж, Nginx-ээр фронтэд үйлчилнэ. Хамгийн хялбар бөгөөд бүх OS-д адил.

#### Docker болон Docker Compose суулгах

##### Windows
1. [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/) татаж суулгана.
2. Суулгасны дараа Docker Desktop-г ажиллуулна (WSL2 ашиглахыг зөвлөж байна).
3. Командын мөр (cmd/powershell)-ээс `docker --version` ажилладаг эсэх шалгана.

##### macOS
1. [Docker Desktop for Mac](https://docs.docker.com/desktop/install/mac-install/) татаж суулгана.
2. Аппыг нээж, `docker --version` командыг терминалд шалгана.

##### Linux (Ubuntu/Debian)
```bash
# Docker
sudo apt update
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker
# Хэрэглэгчээ docker бүлэгт нэмэх (sudoгүй ажиллуулах)
sudo usermod -aG docker $USER
# Гарч дахин нэвтрэх эсвэл `newgrp docker`

# Docker Compose V2
sudo apt install docker-compose-v2   # эсвэл docker-compose-plugin
# шалгах
docker compose version
```

#### Docker-ээр ажиллуулах

1. Төслийн үндсэн хавтас руу шилжинэ:
   ```bash
   cd mongolia_road_network
   ```
2. (Эхний удаа) дүрсүүдийг байгуулж, контейнерүүдийг дэвсгэр горимд эхлүүлнэ:
   ```bash
   docker compose up -d
   ```
  - `backend` контейнер (Spring Boot, порт 5000)
  - `frontend` контейнер (Nginx, порт 3000)
3. Статус харах:
   ```bash
   docker compose ps
   ```
4. Лог харах:
   ```bash
   docker compose logs -f backend   # backend-ийн лог
   docker compose logs -f frontend  # frontend-ийн лог
   ```
5. Хөтөч дээр `http://localhost:3000` нээнэ.

Зогсоох:
```bash
docker compose down
```
Хэрэв volume-г устгахыг хүсвэл (өгөгдлийн сан байхгүй учраас заавал биш):
```bash
docker compose down -v
```

#### Docker-ийн тохиргоо (docker-compose.yaml)

Файл дараах байдалтай байх ёстой (database-гүй):

```yaml
services:
  backend:
    build: ./backend
    container_name: road_backend
    ports:
      - "5000:5000"
    volumes:
      - ./backend/shapefiles:/app/shapefiles:ro
    environment:
      - SHAPEFILE_PATH=/app/shapefiles/gis_osm_roads_free_1.shp
  frontend:
    build: ./frontend
    container_name: road_frontend
    ports:
      - "3000:5137"
    depends_on:
      - backend
```

> **Тайлбар**:
> - Backend порт 5000, шууд API-д хандахад `http://localhost:5000/path?...`
> - Frontend Nginx 5137 порт сонсож, хост дээр 3000-р портоор гарч өгнө.

---

### Б. Уламжлалт арга (Java + Maven)

Docker-гүйгээр, зөвхөн backend-ээ Java-р шууд ажиллуулж, frontend-ээ хөгжүүлэлтийн горимд (Vite) тусдаа ажиллуулах.

#### Шаардлага
- **Java 21** (`java --version`)
- **Maven** 3.8+ (`mvn --version`)
- **Node.js** 20+ (frontend-д)

#### Алхамууд

1. **Backend build**
   ```bash
   cd backend
   mvn clean package
   ```

2. **Backend ажиллуулах** (Spring Boot)
   ```bash
   java -jar target/BiyDaalt_1_1-0.0.1-SNAPSHOT.jar
   ```
   API `http://localhost:5000` дээр боломжтой.

3. **Frontend build болон ажиллуулах**
  - Шинэ терминал нээж:
   ```bash
   cd frontend
   npm install   # эсвэл pnpm install
   npm run dev   # эсвэл pnpm run dev
   ```
   Vite сервер `http://localhost:5173` дээр ажиллана.

4. Хөтөч дээр `http://localhost:5173` нээж, API-ийн URL-г тохируулах шаардлагатай бол `vite.config.ts` эсвэл `.env` дотор `VITE_API_URL=http://localhost:5000` гэж заана.

> **CORS асуудлаас зайлсхийх**: Бид Spring Boot дээр `@CrossOrigin(origins = "*")` ашигласан. Хэрэв ажиллахгүй бол дэлхийн CORS тохиргоог нэмнэ.

---

## Хэрэглэх заавар

1. Зүүн талын sidebar-ээс **алгоритм** сонгоно (Dijkstra, A*, BFS).
2. Газрын зураг дээр **ногоон маркер** (эхлэл) байрлуулна.
3. Дараа нь **улаан маркер** (төгсгөл) байрлуулна.
4. Систем автоматаар тооцоолж, хөх өнгийн **замыг** зурна.
5. **Clear** товч – бүх маркер, замыг арилгана.

---

## API баримт бичиг

**Endpoints**

### `GET /path`
Хамгийн богино замыг олж, координатуудын жагсаалтыг буцаана.

| Параметр     | Төрөл    | Шаардлага | Тайлбар                             |
|--------------|----------|-----------|--------------------------------------|
| `start_lat`  | `double` | тийм      | Эхлэх цэгийн өргөрөг                 |
| `start_lon`  | `double` | тийм      | Эхлэх цэгийн уртраг                 |
| `end_lat`    | `double` | тийм      | Төгсгөл цэгийн өргөрөг               |
| `end_lon`    | `double` | тийм      | Төгсгөл цэгийн уртраг               |
| `algo`       | `string` | үгүй      | Алгоритмын нэр: `dijkstra`, `astar`, `bfs`. Анхдагч: `dijkstra` |

**Хариу (JSON)**
```json
{
  "path": [
    { "lat": 47.921, "lon": 106.918 },
    { "lat": 47.923, "lon": 106.920 }
  ],
  "total_distance": 1234.56
}
```

**Алдааны хариу**
```json
{
  "error": "Nearest node not found"
}
```

**Жишээ хүсэлт**
```
http://localhost:5000/path?start_lat=47.921&start_lon=106.918&end_lat=47.925&end_lon=106.930&algo=astar
```

---

## Хэрэглэх зааварчилгаа (алхам алхмаар)

Хэрэв та **Docker ашиглахыг хүсвэл**, дээрх README-д байгаа **А. Docker ашиглах** хэсгийг дарааллаар нь дагахад л болно. Товчхон:

1. Docker + Docker Compose суулгах (OS-оос хамаарч өгөгдсөн команд)
2. Shapefile-г `backend/shapefiles/` руу хуулах
3. `mongolia_road_network` хавтаст `docker compose up -d`
4. Хөтөч дээр `http://localhost:3000` нээх

Хэрэв та **Maven + Node.js** ашиглахыг хүсвэл **Б. Уламжлалт арга** хэсгийг дагана уу.
