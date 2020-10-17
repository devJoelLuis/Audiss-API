package audinss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import audinss.entidades.TaxaSelic;

public interface TaxaSelicRepository extends JpaRepository<TaxaSelic, Integer> {


	TaxaSelic findByAnoAndMes(int year, int monthValue);

	boolean existsByAnoAndMes(int ano, int rowIndex);

	List<TaxaSelic> findAllByOrderByAnoDescMesAsc();

	boolean existsByAno(int ano);

}
