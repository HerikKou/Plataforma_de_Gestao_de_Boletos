package com.boletos.vencimento.repository;
import com.boletos.vencimento.model.Vencimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface VencimentoRepository extends JpaRepository<Vencimento, UUID> {
    Optional<Vencimento> findByIdBoleto(UUID idBoleto);
}
