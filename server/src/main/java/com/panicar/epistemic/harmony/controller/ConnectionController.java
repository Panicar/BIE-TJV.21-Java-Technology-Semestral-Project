package com.panicar.epistemic.harmony.controller;

import com.panicar.epistemic.harmony.dto.ConnectionDTO;
import com.panicar.epistemic.harmony.entity.Connection;
import com.panicar.epistemic.harmony.entity.Connection.ConnectionType;
import com.panicar.epistemic.harmony.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping
    public ResponseEntity<ConnectionDTO> createConnection(@RequestBody Map<String, Object> request) {
        try {
            Long fromItemId = Long.valueOf(request.get("fromItemId").toString());
            Long toItemId = Long.valueOf(request.get("toItemId").toString());
            ConnectionType connectionType = ConnectionType.valueOf(request.get("connectionType").toString());
            Integer strength = Integer.valueOf(request.get("strength").toString());

            Connection connection = connectionService.createConnection(
                    fromItemId, toItemId, connectionType, strength, null);
            return new ResponseEntity<>(new ConnectionDTO(connection), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConnectionDTO> getConnectionById(@PathVariable Long id) {
        return connectionService.getConnectionById(id)
                .map(connection -> new ResponseEntity<>(new ConnectionDTO(connection), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ConnectionDTO>> getAllConnections() {
        List<ConnectionDTO> connections = connectionService.getAllConnections()
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @GetMapping("/from/{itemId}")
    public ResponseEntity<List<ConnectionDTO>> getConnectionsFromItem(@PathVariable Long itemId) {
        List<ConnectionDTO> connections = connectionService.getConnectionsFromItem(itemId)
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @GetMapping("/to/{itemId}")
    public ResponseEntity<List<ConnectionDTO>> getConnectionsToItem(@PathVariable Long itemId) {
        List<ConnectionDTO> connections = connectionService.getConnectionsToItem(itemId)
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @GetMapping("/type/{connectionType}")
    public ResponseEntity<List<ConnectionDTO>> getConnectionsByType(@PathVariable ConnectionType connectionType) {
        List<ConnectionDTO> connections = connectionService.getConnectionsByType(connectionType)
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @GetMapping("/strong")
    public ResponseEntity<List<ConnectionDTO>> getStrongConnections(@RequestParam Integer minStrength) {
        List<ConnectionDTO> connections = connectionService.getStrongConnections(minStrength)
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @GetMapping("/highly-rated")
    public ResponseEntity<List<ConnectionDTO>> getHighlyRatedConnections(
            @RequestParam Integer minStrength,
            @RequestParam Double minRating) {
        List<ConnectionDTO> connections = connectionService.getHighlyRatedConnections(minStrength, minRating)
                .stream()
                .map(ConnectionDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(connections, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConnectionDTO> updateConnection(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            ConnectionType connectionType = request.get("connectionType") != null
                    ? ConnectionType.valueOf(request.get("connectionType").toString()) : null;
            Integer strength = request.get("strength") != null
                    ? Integer.valueOf(request.get("strength").toString()) : null;

            Connection updated = connectionService.updateConnection(id, connectionType, strength, null);
            return new ResponseEntity<>(new ConnectionDTO(updated), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        try {
            connectionService.deleteConnection(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<List<ConnectionDTO>> getAnalysis(@RequestParam String cat1, @RequestParam String cat2) {
        // Standardized REST response returning 200 OK
        return ResponseEntity.ok(connectionService.getAnalysis(cat1, cat2));
    }
}