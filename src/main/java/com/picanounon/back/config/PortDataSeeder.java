package com.picanounon.back.config;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.picanounon.back.model.Port;
import com.picanounon.back.model.Zone;
import com.picanounon.back.repository.PortRepository;

@Configuration
public class PortDataSeeder {

    private static final Set<String> VALID_5_STATIONS = Set.of(
            "A Coruña",
            "Vigo",
            "Vilagarcía",
            "A Guarda",
            "Ferrol Porto Exterior"
    );

    @Bean
    CommandLineRunner initPortsDatabase(PortRepository portRepository) {
        return args -> {
            boolean needsReseed = portRepository.count() == 0 ||
                    portRepository.findAll().stream().anyMatch(p -> p.getTideStation() == null || !VALID_5_STATIONS.contains(p.getTideStation()));

            if (needsReseed) {
                portRepository.deleteAll();
                List<Port> initialPorts = List.of(
                        // Rías Baixas (idZonaMG: 5)
                        new Port(null, 5, "A Guarda", "A Guarda", Zone.RIAS_BAIXAS, 41.9015, -8.8772, "A Guarda", 0),
                        new Port(null, 5, "Baiona", "Baiona", Zone.RIAS_BAIXAS, 42.1228, -8.8475, "Vigo", 5),
                        new Port(null, 5, "Bouzas / Berbés", "Vigo (Bouzas / Berbés)", Zone.RIAS_BAIXAS, 42.2278, -8.7482, "Vigo", 0),
                        new Port(null, 5, "Cangas", "Cangas do Morrazo", Zone.RIAS_BAIXAS, 42.2612, -8.7842, "Vigo", 2),
                        new Port(null, 5, "Moaña", "Moaña", Zone.RIAS_BAIXAS, 42.2825, -8.7478, "Vigo", 3),
                        new Port(null, 5, "Bueu", "Bueu", Zone.RIAS_BAIXAS, 42.3275, -8.7883, "Vigo", 8),
                        new Port(null, 5, "Aldán", "Aldán", Zone.RIAS_BAIXAS, 42.2811, -8.8217, "Vigo", 5),
                        new Port(null, 5, "Marín", "Marín", Zone.RIAS_BAIXAS, 42.3941, -8.7025, "Vigo", 2),
                        new Port(null, 5, "As Corbaceiras", "Pontevedra (As Corbaceiras)", Zone.RIAS_BAIXAS, 42.4303, -8.6536, "Vigo", 12),
                        new Port(null, 5, "Combarro", "Combarro", Zone.RIAS_BAIXAS, 42.4289, -8.7039, "Vigo", 10),
                        new Port(null, 5, "Sanxenxo", "Sanxenxo", Zone.RIAS_BAIXAS, 42.4, -8.808, "Vigo", 5),
                        new Port(null, 5, "Portonovo", "Portonovo", Zone.RIAS_BAIXAS, 42.3956, -8.8242, "Vigo", 4),
                        new Port(null, 5, "Pedras Negras", "Pedras Negras (San Vicente)", Zone.RIAS_BAIXAS, 42.4636, -8.9228, "Vilagarcía", -5),
                        new Port(null, 5, "O Grove", "O Grove", Zone.RIAS_BAIXAS, 42.4972, -8.8617, "Vilagarcía", -3),
                        new Port(null, 5, "Tragove", "Cambados (Tragove)", Zone.RIAS_BAIXAS, 42.5208, -8.8239, "Vilagarcía", -2),
                        new Port(null, 5, "O Xufre", "A Illa de Arousa (O Xufre)", Zone.RIAS_BAIXAS, 42.5622, -8.8653, "Vilagarcía", -1),
                        new Port(null, 5, "Vilanova", "Vilanova de Arousa", Zone.RIAS_BAIXAS, 42.5636, -8.8319, "Vilagarcía", 0),
                        new Port(null, 5, "Vilagarcía", "Vilagarcía de Arousa", Zone.RIAS_BAIXAS, 42.5975, -8.7667, "Vilagarcía", 0),
                        new Port(null, 5, "Carril", "Carril", Zone.RIAS_BAIXAS, 42.6106, -8.7719, "Vilagarcía", 1),
                        new Port(null, 5, "Rianxo", "Rianxo", Zone.RIAS_BAIXAS, 42.6483, -8.8189, "Vilagarcía", 3),
                        new Port(null, 5, "A Pobra", "A Pobra do Caramiñal", Zone.RIAS_BAIXAS, 42.6022, -8.935, "Vilagarcía", -4),
                        new Port(null, 5, "Ribeira", "Ribeira", Zone.RIAS_BAIXAS, 42.5539, -8.9931, "Vilagarcía", 2),
                        new Port(null, 5, "Aguiño", "Aguiño", Zone.RIAS_BAIXAS, 42.5233, -9.0189, "Vilagarcía", 2),
                        new Port(null, 5, "Porto do Son", "Porto do Son", Zone.RIAS_BAIXAS, 42.7247, -9.0061, "Vilagarcía", 5),
                        new Port(null, 5, "Portosín", "Portosín", Zone.RIAS_BAIXAS, 42.76, -8.9483, "Vilagarcía", 4),
                        new Port(null, 5, "Testal", "Noia (Testal)", Zone.RIAS_BAIXAS, 42.7844, -8.9056, "Vilagarcía", 6),
                        new Port(null, 5, "Muros", "Muros", Zone.RIAS_BAIXAS, 42.775, -9.0583, "Vilagarcía", 5),

                        // Costa da Morte (idZonaMG: 4)
                        new Port(null, 4, "O Pindo", "O Pindo (Carnota)", Zone.COSTA_DA_MORTE, 42.8986, -9.1239, "Vilagarcía", 8),
                        new Port(null, 4, "O Ézaro", "O Ézaro", Zone.COSTA_DA_MORTE, 42.9097, -9.1308, "Vilagarcía", 8),
                        new Port(null, 4, "Corcubión", "Corcubión", Zone.COSTA_DA_MORTE, 42.9431, -9.1919, "Vilagarcía", 8),
                        new Port(null, 4, "Fisterra", "Fisterra (Cabo)", Zone.COSTA_DA_MORTE, 42.9083, -9.268, "Vilagarcía", 9),
                        new Port(null, 4, "Muxía", "Muxía", Zone.COSTA_DA_MORTE, 43.1044, -9.2172, "Vilagarcía", 4),
                        new Port(null, 4, "Camariñas", "Camariñas", Zone.COSTA_DA_MORTE, 43.1311, -9.1833, "Vilagarcía", 4),
                        new Port(null, 4, "Laxe", "Laxe", Zone.COSTA_DA_MORTE, 43.2208, -9.005, "A Coruña", -5),
                        new Port(null, 4, "Corme", "Corme", Zone.COSTA_DA_MORTE, 43.2661, -8.9639, "A Coruña", -4),
                        new Port(null, 4, "Malpica", "Malpica de Bergantiños", Zone.COSTA_DA_MORTE, 43.3236, -8.8106, "A Coruña", -3),
                        new Port(null, 4, "Caión", "Caión", Zone.COSTA_DA_MORTE, 43.3183, -8.6183, "A Coruña", -2),

                        // Golfo Ártabro (idZonaMG: 3)
                        new Port(null, 3, "Dársena / O Parrote", "A Coruña (Dársena / O Parrote)", Zone.ARTABRO, 43.3695, -8.3963, "A Coruña", 0),
                        new Port(null, 3, "Oza", "A Coruña (Oza)", Zone.ARTABRO, 43.3497, -8.3839, "A Coruña", 1),
                        new Port(null, 3, "Sada", "Sada", Zone.ARTABRO, 43.3556, -8.2486, "A Coruña", 4),
                        new Port(null, 3, "Ares", "Ares", Zone.ARTABRO, 43.4244, -8.2417, "Ferrol Porto Exterior", 2),
                        new Port(null, 3, "Mugardos", "Mugardos", Zone.ARTABRO, 43.4614, -8.2561, "Ferrol Porto Exterior", 1),
                        new Port(null, 3, "Curuxeiras", "Ferrol (Curuxeiras)", Zone.ARTABRO, 43.4832, -8.2369, "Ferrol Porto Exterior", 0),

                        // Ferrol - Bares (idZonaMG: 2)
                        new Port(null, 2, "Cedeira", "Cedeira", Zone.FERROL_BARES, 43.7, -8.0567, "Ferrol Porto Exterior", 5),
                        new Port(null, 2, "Cariño", "Cariño", Zone.FERROL_BARES, 43.7408, -7.8686, "Ferrol Porto Exterior", 8),
                        new Port(null, 2, "Ortigueira", "Ortigueira", Zone.FERROL_BARES, 43.6847, -7.8544, "Ferrol Porto Exterior", 10),
                        new Port(null, 2, "O Barqueiro", "O Barqueiro", Zone.FERROL_BARES, 43.7386, -7.7025, "Ferrol Porto Exterior", 12),

                        // Costa Cantábrica (idZonaMG: 1)
                        new Port(null, 1, "O Vicedo", "O Vicedo", Zone.CANTABRICO, 43.7336, -7.6719, "Ferrol Porto Exterior", -1),
                        new Port(null, 1, "Celeiro", "Celeiro (Viveiro)", Zone.CANTABRICO, 43.68, -7.595, "Ferrol Porto Exterior", 1),
                        new Port(null, 1, "Burela", "Burela", Zone.CANTABRICO, 43.6592, -7.3517, "Ferrol Porto Exterior", 2),
                        new Port(null, 1, "Foz", "Foz", Zone.CANTABRICO, 43.5697, -7.2556, "Ferrol Porto Exterior", 5),
                        new Port(null, 1, "Rinlo", "Rinlo", Zone.CANTABRICO, 43.5558, -7.1075, "Ferrol Porto Exterior", 7),
                        new Port(null, 1, "Porcillán", "Ribadeo (Porcillán)", Zone.CANTABRICO, 43.5357, -7.0403, "Ferrol Porto Exterior", 9)
                );
                portRepository.saveAll(initialPorts);
            }
        };
    }
}
