# Plan de Migración de Puertos (Frontend -> Backend Spring Boot)

## 🎯 Objetivo

Migrar la gestión de la lista de puertos (`PORTS_DATA`) actualmente almacenada estáticamente en el frontend hacia la API REST del backend en Spring Boot (`http://localhost:8080/api/ports`), manteniendo exactamente las mismas interfaces TypeScript y tipos de datos para no romper ningún componente existente.

---

## 📐 1. Definición Exacta de Interfaces y DTOs

Para garantizar compatibilidad total 1:1, los datos que retorne la API de Spring Boot deben coincidir exactamente con el contrato actual del frontend.

### Frontend TypeScript Interface ([src/app/core/models/interfaces/port.ts](file:///C:/Users/Sergio/Documents/coding/picanounon/src/app/core/models/interfaces/port.ts))

```typescript
export type Zone = "Rías Baixas" | "Costa da Morte" | "Rías Altas" | "Cantábrico" | "Ferrol-Bares" | "Ártabro";

export interface Port {
  id: number;
  idZonaMG: number;
  alias: string;
  name: string;
  zone: Zone;
  lat: number;
  lng: number;
}
```

### Backend Spring Boot (Referencia DTO / Entity Java)

```java
public enum Zone {
    RIAS_BAIXAS("Rías Baixas"),
    COSTA_DA_MORTE("Costa da Morte"),
    RIAS_ALTAS("Rías Altas"),
    CANTABRICO("Cantábrico"),
    FERROL_BARES("Ferrol-Bares"),
    ARTABRO("Ártabro");

    private final String value;

    Zone(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

public class PortDTO {
    private Long id;
    private Integer idZonaMG;
    private String alias;
    private String name;
    private String zone; // O enum Zone serializado como String
    private Double lat;
    private Double lng;

    // Getters y Setters
}
```

---

## 🛠️ 2. Fases de Migración

### Fase 1: Creación del Endpoint en Backend (Spring Boot)

1. Implementar la entidad/tabla `Port` y popularla con los datos de `ports.data.ts`.
2. Crear el controlador REST:
   - `GET /api/ports` -> Retorna `List<PortDTO>` (Todos los puertos).
   - `GET /api/ports/{id}` -> Retorna `PortDTO` por ID.
   - `GET /api/ports/zones` -> Retorna lista de zonas disponibles.

### Fase 2: Adaptación de `PortService` en Frontend

Transformar [src/app/core/services/port.service.ts](file:///C:/Users/Sergio/Documents/coding/picanounon/src/app/core/services/port.service.ts) para consumir `HttpClient` mediante Observables / Signals.

#### Cambios en `PortService`:

1. Inyectar `HttpClient` y usar `environment.apiUrl` (`/api`).
2. Mantener `ports` como un `signal<Port[]>([])` cargado asíncronamente desde el backend.
3. Actualizar métodos de consulta:
   - `loadPorts()`: Petición `GET /api/ports`.
   - `initDefaultPort()`: Cargar puertos primero y luego restaurar la selección guardada en `localStorage`.

---

## 📋 3. Checklist de Verificación

- [ ] Backend entrega `/api/ports` con el JSON idéntico a las propiedades de `Port`.
- [ ] `PortService` inicializa correctamente el puerto por defecto tras recibir los datos del backend.
- [ ] El selector de puertos (`PortSelectorComponent`) y el dashboard siguen funcionando sin modificaciones de template ni estilos.
- [ ] Eliminación limpia de `ports.data.ts` y `meteogalicia_ports_discovered.json` del bundle del frontend una vez completada la migración.

Valores de ports.data.ts:

import { Port } from '../models/interfaces/port';

export const PORTS_DATA: Port[] = [
// ==========================================
// RÍAS BAIXAS (idZonaMG: 5)
// ==========================================
{
id: 10,
idZonaMG: 5,
alias: 'A Guarda',
name: 'A Guarda',
zone: 'Rías Baixas',
lat: 41.9015,
lng: -8.8772,
},
{
id: 3,
idZonaMG: 5,
alias: 'Baiona',
name: 'Baiona',
zone: 'Rías Baixas',
lat: 42.1228,
lng: -8.8475,
},
{
id: 3,
idZonaMG: 5,
alias: 'Bouzas / Berbés',
name: 'Vigo (Bouzas / Berbés)',
zone: 'Rías Baixas',
lat: 42.2278,
lng: -8.7482,
},
{
id: 3,
idZonaMG: 5,
alias: 'Cangas',
name: 'Cangas do Morrazo',
zone: 'Rías Baixas',
lat: 42.2612,
lng: -8.7842,
},
{
id: 3,
idZonaMG: 5,
alias: 'Moaña',
name: 'Moaña',
zone: 'Rías Baixas',
lat: 42.2825,
lng: -8.7478,
},
{
id: 15,
idZonaMG: 5,
alias: 'Bueu',
name: 'Bueu',
zone: 'Rías Baixas',
lat: 42.3275,
lng: -8.7883,
},
{
id: 3,
idZonaMG: 5,
alias: 'Aldán',
name: 'Aldán',
zone: 'Rías Baixas',
lat: 42.2811,
lng: -8.8217,
},
{
id: 15,
idZonaMG: 5,
alias: 'Marín',
name: 'Marín',
zone: 'Rías Baixas',
lat: 42.3941,
lng: -8.7025,
},
{
id: 13,
idZonaMG: 5,
alias: 'As Corbaceiras',
name: 'Pontevedra (As Corbaceiras)',
zone: 'Rías Baixas',
lat: 42.4303,
lng: -8.6536,
},
{
id: 15,
idZonaMG: 5,
alias: 'Combarro',
name: 'Combarro',
zone: 'Rías Baixas',
lat: 42.4289,
lng: -8.7039,
},
{
id: 15,
idZonaMG: 5,
alias: 'Sanxenxo',
name: 'Sanxenxo',
zone: 'Rías Baixas',
lat: 42.4,
lng: -8.808,
},
{
id: 15,
idZonaMG: 5,
alias: 'Portonovo',
name: 'Portonovo',
zone: 'Rías Baixas',
lat: 42.3956,
lng: -8.8242,
},
{
id: 4,
idZonaMG: 5,
alias: 'Pedras Negras',
name: 'Pedras Negras (San Vicente)',
zone: 'Rías Baixas',
lat: 42.4636,
lng: -8.9228,
},
{
id: 4,
idZonaMG: 5,
alias: 'O Grove',
name: 'O Grove',
zone: 'Rías Baixas',
lat: 42.4972,
lng: -8.8617,
},
{
id: 4,
idZonaMG: 5,
alias: 'Tragove',
name: 'Cambados (Tragove)',
zone: 'Rías Baixas',
lat: 42.5208,
lng: -8.8239,
},
{
id: 4,
idZonaMG: 5,
alias: 'O Xufre',
name: 'A Illa de Arousa (O Xufre)',
zone: 'Rías Baixas',
lat: 42.5622,
lng: -8.8653,
},
{
id: 4,
idZonaMG: 5,
alias: 'Vilanova',
name: 'Vilanova de Arousa',
zone: 'Rías Baixas',
lat: 42.5636,
lng: -8.8319,
},
{
id: 4,
idZonaMG: 5,
alias: 'Vilagarcía',
name: 'Vilagarcía de Arousa',
zone: 'Rías Baixas',
lat: 42.5975,
lng: -8.7667,
},
{
id: 4,
idZonaMG: 5,
alias: 'Carril',
name: 'Carril',
zone: 'Rías Baixas',
lat: 42.6106,
lng: -8.7719,
},
{
id: 4,
idZonaMG: 5,
alias: 'Rianxo',
name: 'Rianxo',
zone: 'Rías Baixas',
lat: 42.6483,
lng: -8.8189,
},
{
id: 11,
idZonaMG: 5,
alias: 'A Pobra',
name: 'A Pobra do Caramiñal',
zone: 'Rías Baixas',
lat: 42.6022,
lng: -8.935,
},
{
id: 11,
idZonaMG: 5,
alias: 'Ribeira',
name: 'Ribeira',
zone: 'Rías Baixas',
lat: 42.5539,
lng: -8.9931,
},
{
id: 11,
idZonaMG: 5,
alias: 'Aguiño',
name: 'Aguiño',
zone: 'Rías Baixas',
lat: 42.5233,
lng: -9.0189,
},
{
id: 12,
idZonaMG: 5,
alias: 'Porto do Son',
name: 'Porto do Son',
zone: 'Rías Baixas',
lat: 42.7247,
lng: -9.0061,
},
{
id: 12,
idZonaMG: 5,
alias: 'Portosín',
name: 'Portosín',
zone: 'Rías Baixas',
lat: 42.76,
lng: -8.9483,
},
{
id: 12,
idZonaMG: 5,
alias: 'Testal',
name: 'Noia (Testal)',
zone: 'Rías Baixas',
lat: 42.7844,
lng: -8.9056,
},
{
id: 12,
idZonaMG: 5,
alias: 'Muros',
name: 'Muros',
zone: 'Rías Baixas',
lat: 42.775,
lng: -9.0583,
},

// ==========================================
// COSTA DA MORTE (idZonaMG: 4)
// ==========================================
{
id: 7,
idZonaMG: 4,
alias: 'O Pindo',
name: 'O Pindo (Carnota)',
zone: 'Costa da Morte',
lat: 42.8986,
lng: -9.1239,
},
{
id: 7,
idZonaMG: 4,
alias: 'O Ézaro',
name: 'O Ézaro',
zone: 'Costa da Morte',
lat: 42.9097,
lng: -9.1308,
},
{
id: 7,
idZonaMG: 4,
alias: 'Corcubión',
name: 'Corcubión',
zone: 'Costa da Morte',
lat: 42.9431,
lng: -9.1919,
},
{
id: 7,
idZonaMG: 4,
alias: 'Fisterra',
name: 'Fisterra (Cabo)',
zone: 'Costa da Morte',
// Axustado lixeiramente a mar aberto para capturar o mar oceánico de Open-Meteo
lat: 42.9083,
lng: -9.268,
},
{
id: 8,
idZonaMG: 4,
alias: 'Muxía',
name: 'Muxía',
zone: 'Costa da Morte',
lat: 43.1044,
lng: -9.2172,
},
{
id: 8,
idZonaMG: 4,
alias: 'Camariñas',
name: 'Camariñas',
zone: 'Costa da Morte',
lat: 43.1311,
lng: -9.1833,
},
{
id: 9,
idZonaMG: 4,
alias: 'Laxe',
name: 'Laxe',
zone: 'Costa da Morte',
lat: 43.2208,
lng: -9.005,
},
{
id: 9,
idZonaMG: 4,
alias: 'Corme',
name: 'Corme',
zone: 'Costa da Morte',
lat: 43.2661,
lng: -8.9639,
},
{
id: 9,
idZonaMG: 4,
alias: 'Malpica',
name: 'Malpica de Bergantiños',
zone: 'Costa da Morte',
lat: 43.3236,
lng: -8.8106,
},
{
id: 1,
idZonaMG: 4,
alias: 'Caión',
name: 'Caión',
zone: 'Costa da Morte',
lat: 43.3183,
lng: -8.6183,
},

// ==========================================
// GOLFO ÁRTABRO (idZonaMG: 3)
// ==========================================
{
id: 1,
idZonaMG: 3,
alias: 'Dársena / O Parrote',
name: 'A Coruña (Dársena / O Parrote)',
zone: 'Ártabro',
lat: 43.3695,
lng: -8.3963,
},
{
id: 1,
idZonaMG: 3,
alias: 'Oza',
name: 'A Coruña (Oza)',
zone: 'Ártabro',
lat: 43.3497,
lng: -8.3839,
},
{
id: 1,
idZonaMG: 3,
alias: 'Sada',
name: 'Sada',
zone: 'Ártabro',
lat: 43.3556,
lng: -8.2486,
},
{
id: 16,
idZonaMG: 3,
alias: 'Ares',
name: 'Ares',
zone: 'Ártabro',
lat: 43.4244,
lng: -8.2417,
},
{
id: 16,
idZonaMG: 3,
alias: 'Mugardos',
name: 'Mugardos',
zone: 'Ártabro',
lat: 43.4614,
lng: -8.2561,
},
{
id: 16,
idZonaMG: 3,
alias: 'Curuxeiras',
name: 'Ferrol (Curuxeiras)',
zone: 'Ártabro',
lat: 43.4832,
lng: -8.2369,
},

// ==========================================
// FERROL - BARES (idZonaMG: 2)
// ==========================================
{
id: 14,
idZonaMG: 2,
alias: 'Cedeira',
name: 'Cedeira',
zone: 'Ferrol-Bares',
lat: 43.7,
lng: -8.0567,
},
{
id: 14,
idZonaMG: 2,
alias: 'Cariño',
name: 'Cariño',
zone: 'Ferrol-Bares',
lat: 43.7408,
lng: -7.8686,
},
{
id: 14,
idZonaMG: 2,
alias: 'Ortigueira',
name: 'Ortigueira',
zone: 'Ferrol-Bares',
lat: 43.6847,
lng: -7.8544,
},
{
id: 6,
idZonaMG: 2,
alias: 'O Barqueiro',
name: 'O Barqueiro',
zone: 'Ferrol-Bares',
lat: 43.7386,
lng: -7.7025,
},

// ==========================================
// COSTA CANTÁBRICA (idZonaMG: 1)
// ==========================================
{
id: 6,
idZonaMG: 1,
alias: 'O Vicedo',
name: 'O Vicedo',
zone: 'Cantábrico',
lat: 43.7336,
lng: -7.6719,
},
{
id: 6,
idZonaMG: 1,
alias: 'Celeiro',
name: 'Celeiro (Viveiro)',
zone: 'Cantábrico',
lat: 43.68,
lng: -7.595,
},
{
id: 6,
idZonaMG: 1,
alias: 'Burela',
name: 'Burela',
zone: 'Cantábrico',
lat: 43.6592,
lng: -7.3517,
},
{
id: 6,
idZonaMG: 1,
alias: 'Foz',
name: 'Foz',
zone: 'Cantábrico',
lat: 43.5697,
lng: -7.2556,
},
{
id: 6,
idZonaMG: 1,
alias: 'Rinlo',
name: 'Rinlo',
zone: 'Cantábrico',
lat: 43.5558,
lng: -7.1075,
},
{
id: 6,
idZonaMG: 1,
alias: 'Porcillán',
name: 'Ribadeo (Porcillán)',
zone: 'Cantábrico',
lat: 43.5357,
lng: -7.0403,
},
];
