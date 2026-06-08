package com.boletos.boleto.controller;
import com.boletos.boleto.dto.BoletoRequestDTO;
import com.boletos.boleto.dto.BoletoResponseDTO;
import com.boletos.boleto.service.BoletoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/boletos")
public class BoletoController {
    private final BoletoService boletoService;
    public BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }
    @PostMapping
    public ResponseEntity<BoletoResponseDTO> criar(@Valid @RequestBody BoletoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boletoService.criarBoleto(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(boletoService.buscarPorId(id));
    }
    @GetMapping
    public ResponseEntity<List<BoletoResponseDTO>> listar() {
        return ResponseEntity.ok(boletoService.listarTodos());
    }
}
