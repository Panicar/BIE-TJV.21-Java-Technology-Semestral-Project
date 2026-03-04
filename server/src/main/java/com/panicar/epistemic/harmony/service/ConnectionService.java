package com.panicar.epistemic.harmony.service;

import com.panicar.epistemic.harmony.dto.ConnectionDTO;
import com.panicar.epistemic.harmony.entity.Connection;
import com.panicar.epistemic.harmony.entity.Connection.ConnectionType;
import com.panicar.epistemic.harmony.entity.EpistemicItem;
import com.panicar.epistemic.harmony.repository.ConnectionRepository;
import com.panicar.epistemic.harmony.repository.EpistemicItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private EpistemicItemRepository epistemicItemRepository;

    public Connection createConnection(Long fromItemId, Long toItemId,
                                       ConnectionType connectionType, Integer strength,
                                       String description) {
        EpistemicItem fromItem = epistemicItemRepository.findById(fromItemId)
                .orElseThrow(() -> new RuntimeException("From item not found with id: " + fromItemId));

        EpistemicItem toItem = epistemicItemRepository.findById(toItemId)
                .orElseThrow(() -> new RuntimeException("To item not found with id: " + toItemId));

        if (fromItemId.equals(toItemId)) {
            throw new IllegalArgumentException("Cannot create connection to the same item");
        }

        Connection connection = new Connection(fromItem, toItem, connectionType, strength);

        return connectionRepository.save(connection);
    }

    @Transactional(readOnly = true)
    public Optional<Connection> getConnectionById(Long id) {
        return connectionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Connection> getAllConnections() {
        return connectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Connection> getConnectionsFromItem(Long fromItemId) {
        return connectionRepository.findByFromItemId(fromItemId);
    }

    @Transactional(readOnly = true)
    public List<Connection> getConnectionsToItem(Long toItemId) {
        return connectionRepository.findByToItemId(toItemId);
    }

    @Transactional(readOnly = true)
    public List<Connection> getConnectionsByType(ConnectionType connectionType) {
        return connectionRepository.findByConnectionType(connectionType);
    }

    @Transactional(readOnly = true)
    public List<Connection> getStrongConnections(Integer minStrength) {
        return connectionRepository.findByStrengthGreaterThanEqual(minStrength);
    }

    @Transactional(readOnly = true)
    public List<Connection> getHighlyRatedConnections(Integer minStrength, Double minRating) {
        return connectionRepository.findHighlyRatedConnections(minStrength, minRating);
    }

    public Connection updateConnection(Long id, ConnectionType connectionType,
                                       Integer strength, String description) {
        Connection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Connection not found with id: " + id));

        if (connectionType != null) {
            connection.setConnectionType(connectionType);
        }
        if (strength != null) {
            connection.setStrength(strength);
        }

        return connectionRepository.save(connection);
    }

    public void deleteConnection(Long id) {
        if (!connectionRepository.existsById(id)) {
            throw new RuntimeException("Connection not found with id: " + id);
        }
        connectionRepository.deleteById(id);
    }

    /**
     * Implementation of the Business Layer logic for Interdisciplinary Analysis.
     * This method delegates to the Repository and maps the resulting Entities to DTOs.
     */
    public List<ConnectionDTO> getAnalysis(String cat1, String cat2) {
        // 1. Call the complex JPQL query from the Persistence Layer
        // Ensure this method is defined in your ConnectionRepository
        List<Connection> connections = connectionRepository.findInterdisciplinaryLinks(cat1, cat2);

        // 2. Map the results to ConnectionDTO using the constructor you provided
        return connections.stream()
                .map(ConnectionDTO::new) // Uses your 'public ConnectionDTO(Connection connection)'
                .collect(Collectors.toList());
    }
}