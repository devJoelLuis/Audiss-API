package audinss.interfaces;

import java.time.LocalDate;

import audinss.entidades.Legislacao;

public interface FcQuery {
	
	Legislacao getLegislacao();
	LocalDate getDataInicio();

}
