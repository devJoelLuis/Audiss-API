package audinss.servico;

import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import audinss.DTO.AliquotaDTO;
import audinss.DTO.MovimentoLista;
import audinss.DTO.VerbaRelatorioDTO;
import audinss.entidades.AliquotasMes;
import audinss.entidades.Cliente;
import audinss.entidades.FolhaCorrecao;
import audinss.entidades.Legislacao;
import audinss.entidades.Movimento;
import audinss.entidades.Usuario;
import audinss.repository.AliquotasMesRepository;
import audinss.repository.ClienteRepository;
import audinss.repository.FolhaCorrecaoRepository;
import audinss.repository.LegislacaoRepository;
import audinss.repository.MovimentoRepository;
import audinss.repository.UsuarioRepository;
import audinss.response.Response;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class MovimentoService {
	
	
	@Autowired
	private MovimentoRepository repo;
	
	@Autowired
	private AliquotasMesRepository repoAliq;
	
	@Autowired
	private ClienteRepository repoCli;
	
	@Autowired
	private LegislacaoRepository repoLeg;
	
	@Autowired
	private FolhaCorrecaoRepository repoFc;
	
	@Autowired
	private UsuarioRepository repoUser;
	
	private String idUsuarioLogado;
	private List<String> permissoesUsuario;
	
	
	// altera um unico movimento
	public Response<Movimento> alterar(Movimento m){
		Response<Movimento> response = new Response<>();
		try {
			List<Integer> idsClientes = buscarListaClientesPermitidos();
			if (idsClientes == null) { //null significa que o usuário logado é admin
			   
			} else {
				if (!idsClientes.contains(m.getAliquotasMes().getCliente().getId())) {
					throw new Exception("usuário sem permissão de acesso ao cliente!!!!");
				}
			}
			Optional<Movimento> mOp = repo.findById(m.getId());
			Optional<AliquotasMes> aliOp = repoAliq.findById(m.getAliquotasMes().getId());
			if (!mOp.isPresent()) {
				throw new Exception("não foi possível encontrar um movimento com o id: "+ m.getId());
			}
			if (!aliOp.isPresent()) {
				throw new Exception("não foi possível encontrar uma alíquota com o id: "+ m.getAliquotasMes().getId());
			}
			AliquotasMes aliBanco = aliOp.get();
			Movimento mbanco = mOp.get();
			BeanUtils.copyProperties(m, mbanco);
			mbanco.setAliquotasMes(aliBanco);
			response.setDados(repo.save(mbanco));
			return response;
		} catch (Exception e) {
		  response.getErros().add("Ocorreu um erro ao tentar alterar um movimento: "+ e.getMessage());
		  return response;
		}
	}
	

	
	private List<Integer> buscarListaClientesPermitidos() throws Exception {
		pegarNomeEIdUsuarioLogado();
		if (idUsuarioLogado == null) {
			throw new Exception("não foi possível identificar o id do usuário logado!!!");
		}
		int idUsuario = Integer.parseInt(idUsuarioLogado);
		Optional<Usuario> userLogadoOp = repoUser.findById(idUsuario);
		if (!userLogadoOp.isPresent()) {
			throw new Exception("não foi possível encontrar um usuário logado com id "+ idUsuario);
		}
		Usuario usuarioLogado = userLogadoOp.get();
		if (permissoesUsuario.contains("ROLE_ADMIN")) {
			return null;
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (Cliente c: usuarioLogado.getClientes()) {
			ids.add(c.getId());
		}
		return ids;
	}



	// get all cliente ano, Mes, legislação e id do cliente
	public Response<List<Movimento>> getMovimentoLegislacaoClienteAnoMes(int ano, int mes, String leg, int idcliente) {
		Response<List<Movimento>> response = new Response<>();
		try {
			if (ano == 0) {
		     throw new Exception("o ano é zero!");		 
			}
			if (mes == 0) {
				throw new Exception("o mês é zero!");
			}
			if (leg.equals("")) {
				throw new Exception("legislação não informada!");
			}
			if (idcliente == 0) {
				throw new Exception("o id do cliente é zero!");
			}
			List<Integer> idsClientes = buscarListaClientesPermitidos();
			if (idsClientes == null) { //null significa que o usuário logado é admin
			   
			} else {
				if (!idsClientes.contains(idcliente)) {
					throw new Exception("usuário sem permissão de acesso ao cliente!!!!");
				}
			}
			List<Movimento> movimentos = repo.findByLegislacaoIgnoreCaseAndAliquotasMesClienteIdAndAliquotasMesMesAndAliquotasMesAnoOrderByCodigoAsc(leg, idcliente, mes, ano);
			response.setDados(movimentos);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao consultar movimentos pelo cliente, ano e mês: "+ e.getMessage());
			return response;
		}
	}
	
	
	// get all FP ano, mes e id do cliente
	public Response<List<Movimento>> getMovimentoFpClienteAnoMes(int ano, int mes, int idcliente) {
		Response<List<Movimento>> response = new Response<>();
		try {
			if (ano == 0) {
		     throw new Exception("o ano é zero!");		 
			}
			if (mes == 0) {
				throw new Exception("o mês é zero!");
			}
			if (idcliente == 0) {
				throw new Exception("o id do cliente é zero!");
			}
			List<Integer> idsClientes = buscarListaClientesPermitidos();
			if (idsClientes == null) { //null significa que o usuário logado é admin
			   
			} else {
				if (!idsClientes.contains(idcliente)) {
					throw new Exception("usuário sem permissão de acesso ao cliente!!!!");
				}
			}
			List<Movimento> movimentos = repo.findByLegislacaoAndAliquotasMesClienteIdAndAliquotasMesMesAndAliquotasMesAnoOrderByIdAsc("FP", idcliente, mes, ano);
			response.setDados(movimentos);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao consultar movimentos pelo cliente, ano e mês: "+ e.getMessage());
			return response;
		}
	}

	
	
	// get all aliquota id
	public Response<List<Movimento>> getMovimentoAliquotaId(int idAliquota) {
		Response<List<Movimento>> response = new Response<>();
		try {
			if (idAliquota == 0) {
		     throw new Exception("o id da alíquota é zero!");		 
			}
			
			List<Movimento> movimentos = repo.findByAliquotasMesIdAndLegislacao(idAliquota, "FP");
			response.setDados(movimentos);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao consultar movimentos pelo cliente, ano e mês: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	
	
	
	
	
	// salva uma lista de movimentos
    @Transactional
	public Response<List<Movimento>> cadastraAll(MovimentoLista mvl) {
		Response<List<Movimento>> response = new Response<>();
		try {
			//verifica se a lista de movimentos não está nula
			if (mvl == null) {
				throw new Exception("a lista de movimento da folha está nula!");
			}
			//verifica se a lista de movimentos não está zerada
			if (mvl.getMovimentos().size() == 0) {
				throw new Exception("a lista de movimento da folha está zerada!");
			}
			
			// pegar e cadastrar a aliquota
			AliquotasMes alm = mvl.getMovimentos().get(0).getAliquotasMes();
			if (alm == null) {
				throw new Exception("a aliquota está nula!");
			}
			
			//buscar o cliente
			Optional<Cliente> cliOp = repoCli.findById(alm.getCliente().getId());
			if (!cliOp.isPresent()) {
				throw new Exception("não foi possível encontrar um cliente com o id: "+ alm.getCliente().getId());
			}
			
			alm.setCliente(cliOp.get());
			// verificar se ja exite aliquota mês
			Optional<AliquotasMes> almOp = repoAliq.findByClienteIdAndAnoAndMes(alm.getCliente().getId(), alm.getAno(), alm.getMes());
			if (almOp.isPresent()) {
				alm = almOp.get();
			} else {
				alm = repoAliq.save(alm);
			}
			
			List<Movimento> movimentos = new ArrayList<>();
			
			// gerar o calculo das legislações conforme a FP
			List<Legislacao> legislacoes = repoLeg.findAll();
			
			//adiconar a aliquota salva nos movimentos fp
			for (Movimento m: mvl.getMovimentos()) {
				//verificar se o codigo e a verba já se encontrão cadastrados para o mês e ano
				Long count = repo.countByAliquotasMesClienteIdAndAliquotasMesAnoAndAliquotasMesMesAndCodigoAndDescricaoAndLegislacao(m.getAliquotasMes().getCliente().getId(),
						m.getAliquotasMes().getAno(), m.getAliquotasMes().getMes(),
						m.getCodigo(), m.getDescricao(), m.getLegislacao());
				if (count > 0) {
					throw new Exception("a verba "+ m.getDescricao() +" de código "+m.getCodigo()+", já se encontra cadastrada no banco de dados");
				}
				m.setAliquotasMes(alm);
				gerarFolhaCorrecao(m, legislacoes);
				movimentos.add(repo.save(m));
			}
			response.setDados(movimentos);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao cadastrar a folha de pagamento: "+ e.getMessage());
			return response;
		}
	}
    
    
    
    
    
    
	
    
    // gera as folhas de correções
    private void gerarFolhaCorrecao(Movimento m, List<Legislacao> legislacoes) throws Exception {
		try {
			for (Legislacao l: legislacoes) {
				// buscar as fc 
				
				//somar as aliquotas
				double totalAliquota = m.getAliquotasMes().getInssRat() + m.getAliquotasMes().getInssTerceiros() + m.getAliquotasMes().getInssEmpresa();
				Integer idcliente = m.getAliquotasMes().getCliente().getId();
				
				  // buscar a fc correspondente
				  FolhaCorrecao fc = repoFc.findByVerbaPadraoCodigoAndLegislacaoLegislacaoAndClienteId(m.getCodigo(), l.getLegislacao(), idcliente);
				  if (fc == null) {
					  throw new Exception("não foi possível encontrar um fc com a verba padrão código "+ m.getCodigo()+".");
				  }
					
					//verificar se a legislacao exige data de entrada do processo
					if (l.isExigeDataInicio()) {
						LocalDate tempTest;
						if (m.getAliquotasMes().getMes() == 13) {
						  tempTest = LocalDate.of(m.getAliquotasMes().getAno(), 12, 1);
						} else {
						  tempTest = LocalDate.of(m.getAliquotasMes().getAno(), m.getAliquotasMes().getMes(), 1);
						}
						
						if (tempTest.isBefore(fc.getDataInicio())) continue;
					}
					
					Movimento mov = new Movimento();
					
					// verificar se a legislacao possui artigos
					
					
					
						// verificar se é desconto ou provento
						if (m.getTipo().equals("PROVENTO")) {
							//verificar se a tributação da fc está diferente da fp
							if (fc.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(0D);
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("NT");
								mov.setFp("NT");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("PROVENTO");
								mov.setValorInssFp(0D);
								mov.setValorPagar(0D);
								mov.setValorProvento(m.getValorProvento());
								mov.setValorRecuperar(0D);
								if (l.isExigeDataInicio())
								mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
							if (fc.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
								mov.setAliquotasMes(m.getAliquotasMes());
								
								mov.setBaseCalcInss(m.getValorProvento());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("T");
								mov.setFp("T");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("PROVENTO");
								if (m.getValorProvento() != 0D && totalAliquota != 0D) {
									mov.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
								} else {
									mov.setValorInssFp(0D);
								}								
								mov.setValorPagar(0D);
								mov.setValorProvento(m.getValorProvento());
								mov.setValorRecuperar(0D);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
								
								// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
							
							// recuperar ou receber
							if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("NT") &&  m.getFp().equals("T")) {
								double valorInss = 0D;
								if (m.getValorProvento() != 0D && totalAliquota != 0D) {
									valorInss = (m.getValorProvento() * totalAliquota) / 100;
								} else {
									valorInss = 0D;
								}						
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(m.getValorProvento());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("NT");
								mov.setFp("T");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("PROVENTO");
								mov.setValorInssFp(valorInss);		
								mov.setValorPagar(0D);
								mov.setValorProvento(m.getValorProvento());
								mov.setValorRecuperar(valorInss);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
							
							// valor pagar
							if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("T") &&  m.getFp().equals("NT")) {
								double valorInss = 0D;
								if (m.getValorProvento() != 0D && totalAliquota != 0D) {
									valorInss = (m.getValorProvento() * totalAliquota) / 100;
								} else {
									valorInss = 0D;
								}						
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(m.getValorProvento());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("T");
								mov.setFp("NT");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("PROVENTO");
								mov.setValorInssFp(valorInss);		
								mov.setValorPagar(0D);
								mov.setValorProvento(m.getValorProvento());
								mov.setValorRecuperar(0D);
								mov.setValorPagar(valorInss);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
						   
						} else {
							//verificar se a tributação da fc está diferente da fp
							if (fc.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(m.getBaseCalcInss());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("NT");
								mov.setFp("NT");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("DESCONTO");
								mov.setValorInssFp(0D);
								mov.setValorPagar(0D);
								mov.setValorProvento(0D);
								mov.setValorRecuperar(0D);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
							if (fc.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
								mov.setAliquotasMes(m.getAliquotasMes());
								
								mov.setBaseCalcInss(m.getBaseCalcInss());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("T");
								mov.setFp("T");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("DESCONTO");
								if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
									mov.setValorInssFp((m.getBaseCalcInss() * totalAliquota) / 100);
								} else {
									mov.setValorInssFp(0D);
								}								
								mov.setValorPagar(0D);
								mov.setValorProvento(0D);
								mov.setValorRecuperar(0D);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
								
								// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
							
							// recuperar ou receber
							if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("NT") &&  m.getFp().equals("T")) {
								double valorInss = 0D;
								if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
									valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
								} else {
									valorInss = 0D;
								}						
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(m.getBaseCalcInss());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("NT");
								mov.setFp("T");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("DESCONTO");
								mov.setValorInssFp(valorInss);		
								mov.setValorPagar(valorInss);
								mov.setValorProvento(0D);
								mov.setValorRecuperar(0D);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
							
							
							// valor pagar
							if (!fc.getFc().equals(m.getFp()) && fc.getFc().equals("T") &&  m.getFp().equals("NT")) {
								double valorInss = 0D;
								if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
									valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
								} else {
									valorInss = 0D;
								}						
								mov.setAliquotasMes(m.getAliquotasMes());
								mov.setBaseCalcInss(m.getBaseCalcInss());
								mov.setCodigo(m.getCodigo());
								mov.setDescricao(m.getDescricao());
								mov.setFc("T");
								mov.setFp("NT");
								mov.setLegislacao(l.getLegislacao());
								mov.setTipo("DESCONTO");
								mov.setValorInssFp(valorInss);		
								mov.setValorPagar(0D);
								mov.setValorProvento(0D);
								mov.setValorRecuperar(valorInss);
								mov.setValorPagar(0D);
								if (l.isExigeDataInicio())
									mov.getAliquotasMes().setDataInicio(fc.getDataInicio());
								repo.save(mov);
								continue;
							} 
						}
					
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

    
    
    
    
    // altera uma lista de movimentos
	//@Transactional
   	public Response<List<Movimento>> alterarAll(MovimentoLista mvl) {
   		Response<List<Movimento>> response = new Response<>();
   		try {
   			if (mvl == null) {
   				throw new Exception("a lista de movimento da folha está nula!");
   			}
   			if (mvl.getMovimentos().size() == 0) {
   				throw new Exception("a lista de movimento da folha está zerada!");
   			}
   			
   			// pegar e cadastrar a aliquota
   			AliquotasMes alm = mvl.getMovimentos().get(0).getAliquotasMes();
   			if (alm == null) {
   				throw new Exception("a aliquota está nula!");
   			}
   			
   		    // gerar o calculo das legislações conforme a FP
   			List<Legislacao> legislacoes = repoLeg.findAll();
   			//buscar aliquota do mês
   			 Optional<AliquotasMes> aliOp = repoAliq.findById(alm.getId());
   			if (!aliOp.isPresent()) {
   				throw new Exception("não foi possível encontrar uma alíquota com o id "+ alm.getId());
   			}
   			AliquotasMes aliBanco = aliOp.get();
   			BeanUtils.copyProperties(alm, aliBanco); // copia as alterações
   			
   			//buscar o cliente
   			Optional<Cliente> cliOp = repoCli.findById(alm.getCliente().getId());
   			if (!cliOp.isPresent()) {
   				throw new Exception("não foi possível encontrar um cliente com o id: "+ alm.getCliente().getId());
   			}
   			aliBanco = repoAliq.save(aliBanco);
   			List<Movimento> movimentos = new ArrayList<>();
   			//adiconar a liquota salva nos movimentos fp
   			for (Movimento m: mvl.getMovimentos()) {
   				Optional<Movimento> mOp = repo.findById(m.getId());
   				if (!mOp.isPresent()) {
   					throw new Exception("não foi possível encontrar um movimento com o id: "+ m.getId());
   				}
   				Movimento mvBanco = mOp.get();
   				BeanUtils.copyProperties(m, mvBanco);
   				mvBanco.setAliquotasMes(aliBanco);
   				Double vprovento = (long) 1 * mvBanco.getValorProvento();
   		        mvBanco.setValorProvento(vprovento.doubleValue());
   				atualizarFc(mvBanco, legislacoes);
   				mvBanco = repo.save(mvBanco);
   				movimentos.add(mvBanco);
   			}
   			response.setDados(movimentos);
   			return response;
   			
   		} catch (Exception e) {
   			response.getErros().add("Ocorreu um erro ao cadastrar a folha de pagamento: "+ e.getMessage());
   			System.out.println(e.getMessage());
   			return response;
   		}
   	}
	
	
	


	// atualiza a folha de fc
	private void atualizarFc(Movimento m, List<Legislacao> legislacoes) {
		try {
			double totalAliquota = m.getAliquotasMes().getInssEmpresa() + m.getAliquotasMes().getInssTerceiros() + m.getAliquotasMes().getInssRat();
			for (Legislacao l: legislacoes) {
				Movimento mov = repo.findByAliquotasMesIdAndCodigoAndLegislacao(m.getAliquotasMes().getId(), m.getCodigo(), l.getLegislacao());
				if (mov != null) {
					// verificar se é desconto ou provento
					if (m.getTipo().equals("PROVENTO")) {
						//verificar se a tributação da fc está diferente da fp
						if (mov.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(0D);
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("NT");
							mov.setFp("NT");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("PROVENTO");
							mov.setValorInssFp(0D);
							mov.setValorPagar(0D);
							mov.setValorProvento(m.getValorProvento());
							mov.setValorRecuperar(0D);
							repo.save(mov);
							continue;
						} 
						
						if (mov.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
							mov.setAliquotasMes(m.getAliquotasMes());
							
							mov.setBaseCalcInss(m.getValorProvento());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("T");
							mov.setFp("T");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("PROVENTO");
							if (m.getValorProvento() != 0D && totalAliquota != 0D) {
								mov.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
							} else {
								mov.setValorInssFp(0D);
							}								
							mov.setValorPagar(0D);
							mov.setValorProvento(m.getValorProvento());
							mov.setValorRecuperar(0D);
							repo.save(mov);
							continue;
						} 
						
							
							// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
						
						// recuperar ou receber
						if (!mov.getFc().equals(m.getFp()) && mov.getFc().equals("NT") &&  m.getFp().equals("T")) {
							double valorInss = 0D;
							if (m.getValorProvento() != 0D && totalAliquota != 0D) {
								valorInss = (m.getValorProvento() * totalAliquota) / 100;
							} else {
								valorInss = 0D;
							}						
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(m.getValorProvento());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("NT");
							mov.setFp("T");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("PROVENTO");
							mov.setValorInssFp(valorInss);		
							mov.setValorPagar(0D);
							mov.setValorProvento(m.getValorProvento());
							mov.setValorRecuperar(valorInss);
							repo.save(mov);
							continue;
						} 
						
						
						// valor pagar
						if (!mov.getFc().equals(m.getFp()) && mov.getFc().equals("T") &&  m.getFp().equals("NT")) {
							double valorInss = 0D;
							if (m.getValorProvento() != 0D && totalAliquota != 0D) {
								valorInss = (m.getValorProvento() * totalAliquota) / 100;
							} else {
								valorInss = 0D;
							}						
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(m.getValorProvento());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("T");
							mov.setFp("NT");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("PROVENTO");
							mov.setValorInssFp(valorInss);		
							mov.setValorPagar(0D);
							mov.setValorProvento(m.getValorProvento());
							mov.setValorRecuperar(0D);
							mov.setValorPagar(valorInss);
							repo.save(mov);
							continue;
						} 
					   
					} else {
						//verificar se a tributação da fc está diferente da fp
						if (mov.getFc().equals(m.getFp()) && m.getFp().equals("NT")) {
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(m.getBaseCalcInss());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("NT");
							mov.setFp("NT");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("DESCONTO");
							mov.setValorInssFp(0D);
							mov.setValorPagar(0D);
							mov.setValorProvento(0D);
							mov.setValorRecuperar(0D);
							repo.save(mov);
							continue;
						} 
						
						if (mov.getFc().equals(m.getFp()) && m.getFp().equals("T")) {
							mov.setAliquotasMes(m.getAliquotasMes());
							
							mov.setBaseCalcInss(m.getBaseCalcInss());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("T");
							mov.setFp("T");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("DESCONTO");
							if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
								mov.setValorInssFp((m.getBaseCalcInss() * totalAliquota) / 100);
							} else {
								mov.setValorInssFp(0D);
							}								
							mov.setValorPagar(0D);
							mov.setValorProvento(0D);
							mov.setValorRecuperar(0D);
							repo.save(mov);
							continue;
						} 
						
							
							// gerar se a diferença é a pagar ou receber se FC T e Fp NT = pagar, se FC NT e FP T = receber
						
						// recuperar ou receber
						if (!mov.getFc().equals(m.getFp()) && mov.getFc().equals("NT") &&  m.getFp().equals("T")) {
							double valorInss = 0D;
							if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
								valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
							} else {
								valorInss = 0D;
							}						
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(m.getBaseCalcInss());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("NT");
							mov.setFp("T");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("DESCONTO");
							mov.setValorInssFp(valorInss);		
							mov.setValorPagar(valorInss);
							mov.setValorProvento(0D);
							mov.setValorRecuperar(0D);
							repo.save(mov);
							continue;
						} 
						
						
						// valor pagar
						if (!mov.getFc().equals(m.getFp()) && mov.getFc().equals("T") &&  m.getFp().equals("NT")) {
							double valorInss = 0D;
							if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
								valorInss = (m.getBaseCalcInss() * totalAliquota) / 100;
							} else {
								valorInss = 0D;
							}						
							mov.setAliquotasMes(m.getAliquotasMes());
							mov.setBaseCalcInss(m.getBaseCalcInss());
							mov.setCodigo(m.getCodigo());
							mov.setDescricao(m.getDescricao());
							mov.setFc("T");
							mov.setFp("NT");
							mov.setLegislacao(l.getLegislacao());
							mov.setTipo("DESCONTO");
							mov.setValorInssFp(valorInss);		
							mov.setValorPagar(0D);
							mov.setValorProvento(0D);
							mov.setValorRecuperar(valorInss);
							mov.setValorPagar(0D);
							repo.save(mov);
							continue;
						} 
					}
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	
	
	
	
	
	
	


	// excluir uma lista de movimentos FP pela aliquota
    @Transactional
	public Response<String> excluirAll(int idAliquota) {
		Response<String> response = new Response<>();
		try {
			Optional<AliquotasMes> aliOp = repoAliq.findById(idAliquota);
			if (!aliOp.isPresent()) {
			  throw new Exception("não foi possível encontrar uma alíquota com o id: "+ idAliquota);
			}
		   AliquotasMes aliBanco = aliOp.get();
		   List<Movimento> mvList = repo.findByAliquotasMesId(idAliquota);
		   repo.deleteAll(mvList);
		   repoAliq.delete(aliBanco);
		   response.setDados("ok");
		   return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao excluir a folha de pagamento: "+ e.getMessage());
   			return response;
		}
	}
    
    
    


	public Response<List<Movimento>> getMovimentoLegislacaoAnoMesAliquotaId(int ano, int mes, int idaliquota,
			String legislacao) {
		Response<List<Movimento>> response = new Response<List<Movimento>>();
		try {
			if (idaliquota == 0) {
		     throw new Exception("o id da alíquota é zero!");		 
			}
			if (ano == 0) {
			     throw new Exception("o ano está zero!");		 
			}
			if (idaliquota == 0) {
			     throw new Exception("a id da alíquota esta zero!");		 
			}
			if (legislacao.equals("")) {
			     throw new Exception("a legislação está nula!");		 
			}
			//buscar aliquota
			Optional<AliquotasMes> alOp = repoAliq.findById(idaliquota);
			if (!alOp.isPresent()) {
				throw new Exception("não foi possível encontrar a aliquota com o id "+ idaliquota);
			}
			AliquotasMes al = alOp.get();
			// verificar se o usuário tem permissão para acessar as aliquotas e movimentos
			List<Integer> idsClientes = buscarListaClientesPermitidos();
			if (idsClientes == null) { //null significa que o usuário logado é admin
			   
			} else {
				if (!idsClientes.contains(al.getCliente().getId())) {
					throw new Exception("usuário sem permissão de acesso ao cliente!!!!");
				}
			}
			
			List<Movimento> movimentos = repo.findByAliquotasMesIdAndAliquotasMesAnoAndAliquotasMesMesAndLegislacaoOrderByIdAsc(idaliquota, ano, mes, legislacao);
			response.setDados(movimentos);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao consultar movimentos pelo ano, mês, legislação e alíquota : "+ e.getMessage());
			return response;
		}
		
	}
	
	
	
	
	

   // get all ano, cliente id e legislacao
	public Response<List<AliquotaDTO>> getMovimentoLegislacaoAnoClienteId(int ano, int clienteid, String legislacao) {
		Response<List<AliquotaDTO>> response = new Response<>();
		try {
			if (clienteid == 0) {
		     throw new Exception("o id do cliente é zero!");		 
			}
			if (ano == 0) {
			     throw new Exception("o ano está zero!");		 
			}
			if (legislacao.equals("") || legislacao.equals("undefined") ) {
			     throw new Exception("a legislação está nula!");		 
			}
            
			/*
				List<?> aliMes  = repoAliq.findAliquotasGroupByIdAliquota(clienteid, ano, legislacao);
				List<AliquotaDTO> aliDto = new ArrayList<AliquotaDTO>();
				for (int i = 0; i < aliMes.size(); i++) {
					Object[] temp = (Object[])aliMes.get(i);
					AliquotaDTO d = new AliquotaDTO();
					d.setId((int)temp[0]);
					d.setMes((int)temp[1]);
					d.setAno((int)temp[2]);
					if(temp[3] == null) {
						d.setObs("");
					} else {
						d.setObs((String)temp[3]);
					}
					aliDto.add(d);
				}
			 */
			List<AliquotaDTO> aliDto = new ArrayList<AliquotaDTO>();
			List<AliquotasMes> alList = repoAliq.buscarAliquotasClienteAnoLegislacao(clienteid, ano, legislacao);
			for (AliquotasMes al: alList) {
				AliquotaDTO d = new AliquotaDTO();
				d.setId(al.getId());
				d.setMes(al.getMes());
				d.setAno(al.getAno());
				aliDto.add(d);
			}
			response.setDados(aliDto);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao consultar aliquotas pelo id do cliente, ano e legislação : "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	//gera o relatório das
	public Response<byte[]> relatoriofcPorLegislacao(int ano, int mes, int idaliquota,
			String legislacao) {
		Response<byte[]> response = new Response<byte[]>();
		try {
			double totalProvento = 0.00;
			double baseCalculo = 0.00;
			double valorInss = 0.00;
			double pagar = 0.00;
			double recuperar = 0.00;
			double descontoBase = 0.00;
			NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
			Response<List<Movimento>> resp = getMovimentoLegislacaoAnoMesAliquotaId(ano, mes, idaliquota, legislacao);
			if (resp.getErros().size() > 0) {
				throw new Exception(resp.getErros().get(0));
			}
			List<Movimento> movs = resp.getDados();
			//pegar aliquota
			Optional<AliquotasMes> aliOp = repoAliq.findById(idaliquota);
			if(!aliOp.isPresent()) {
				 throw new Exception("não foi possível encontrar a alíquota com o id "+ idaliquota);		
			}
			AliquotasMes al = aliOp.get();
			Map<String, Object> params = new HashMap<>();
			params.put("ClienteNome", al.getCliente().getNome());
			params.put("ClienteCnpj", al.getCliente().getCnpj());
			params.put("Ano", String.valueOf(al.getAno()));
			params.put("Mes", String.valueOf(al.getMes()));
			List<VerbaRelatorioDTO> verba = new ArrayList<VerbaRelatorioDTO>();
			for (Movimento m: movs) {
				VerbaRelatorioDTO vdto = new VerbaRelatorioDTO(String.valueOf(m.getCodigo()) , m.getDescricao(), m.getFc(), 
						m.getFp(), formatter.format(m.getValorProvento()).replace("R$", ""), formatter.format(m.getBaseCalcInss()).replace("R$", ""), 
						formatter.format(m.getValorInssFp()).replace("R$", "") , 
						formatter.format(m.getValorRecuperar()).replace("R$", ""), formatter.format(m.getValorPagar()).replace("R$", ""));
				verba.add(vdto);
				totalProvento += m.getValorProvento();
				baseCalculo += m.getBaseCalcInss();
				valorInss += m.getValorInssFp();
				pagar += m.getValorPagar();
				recuperar += m.getValorRecuperar();
				// calcula a o valor total para descontar do total da base de calculo
				if (m.getFc().equals("NT") && m.getFp().equals("T")) {
					descontoBase += m.getBaseCalcInss();
				}
			}
			params.put("TotalProvento", formatter.format(totalProvento).replace("R$", ""));
			params.put("BaseCalculo", formatter.format(baseCalculo).replace("R$", ""));
			params.put("ValorInss", formatter.format(valorInss).replace("R$", ""));
			params.put("Pagar", formatter.format(pagar).replace("R$", ""));
			params.put("Recuperar", formatter.format(recuperar).replace("R$", ""));
			//byte[] bytesImg = Base64.decodeBase64(al.getCliente().getLogotipo());
			params.put("Logo", al.getCliente().getLogotipo().replace("data:application/octet-stream;base64,","")); //.replace("data:image/png;base64", "")
			
			params.put("EmpresaInss", formatter.format(al.getInssEmpresa()).replace("R$", ""));
			params.put("TerceirosInss", formatter.format(al.getInssTerceiros()).replace("R$", ""));
			params.put("RatInss", formatter.format(al.getInssRat()).replace("R$", ""));
			params.put("TotalInss", formatter.format(al.getInssEmpresa() + al.getInssRat() + al.getInssTerceiros()).replace("R$", ""));
			params.put("Legislacao", legislacao.trim());
			params.put("LegislacaoCalculo", formatter.format((baseCalculo - descontoBase)).replace("R$", ""));
			
			
			//gerar o relatorio
			InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/relatorio_fc.jasper");
			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params,
					new JRBeanCollectionDataSource(verba));
			response.setDados(JasperExportManager.exportReportToPdf(jasperPrint));
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar gerar o relatório: "+ e.getMessage());
			System.out.println(e.getMessage());
			return response;
		}
	}
	
	
	
	public Response<List<Integer>> buscarAnosMovimento(int clienteid) {
		Response<List<Integer>> response = new Response<>();
		try {
			
			List<AliquotasMes> listAli = repo.buscarAliquotasMovimentoClienteLegislacao(clienteid);
			List<Integer> anos = new ArrayList<>();
			for (AliquotasMes al: listAli) {
				if (!anos.contains(al.getAno())) {
					anos.add(al.getAno());
				}
				
			}
			Collections.sort(anos);
			response.setDados(anos);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu o seguinte erro ao tentar buscar os anos com movimentação: "+ e.getMessage());
			System.out.println(e.getMessage());
			return response;
		}
	}
	
	
	
	
	 //PEGAR USUÁRIO LOGADO FAZENDO REQUISIÇÕES NA API
		@SuppressWarnings("unchecked")
		public void pegarNomeEIdUsuarioLogado() throws Exception {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();        
			Object details = authentication.getDetails();   
			if ( details instanceof OAuth2AuthenticationDetails ) {
			    OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)details;
				Map<String, Object> decodedDetails = (Map<String, Object>)oAuth2AuthenticationDetails.getDecodedDetails();
			    System.out.println(decodedDetails);
			    idUsuarioLogado = decodedDetails.get("iduser").toString();
			    permissoesUsuario = (List<String>) decodedDetails.get("authorities");
                
			}
	       }


		
		/*buscar as legislaçoes dos movimentos que possuem artigo
		public Response<List<String>> buscarLegislacaoMovimentosComArtigo() {
			Response<List<String>> response = new Response<>();
			try {
				
				List<Movimento> movs = repo.buscarLegislacoesMovimentosComArtigo();
				List<String> legislacoes = new ArrayList<>();
				for (Movimento m: movs) {
					if (!legislacoes.contains(m.getLegislacao())) {
						legislacoes.add(m.getLegislacao());
					}
				}
				response.setDados(legislacoes);
				return response;
				
			} catch (Exception e) {
				response.getErros().add("Ocorreu o seguinte erro ao tentar buscar as legislaçoes dos movimentos: "+ e.getMessage());
				System.out.println(e.getMessage());
				return response;
			}
			
		}
     */
    


		public Response<List<String>> buscarLegislacaoMovimentos(int idcliente) {
			Response<List<String>> response = new Response<>();
			try {
				
				List<Movimento> movs = repo.buscarLegislacoesMovimentos(idcliente);
				List<String> legislacoes = new ArrayList<>();
				for (Movimento m: movs) {
					if (!legislacoes.contains(m.getLegislacao())) {
						legislacoes.add(m.getLegislacao());
					}
				}
				response.setDados(legislacoes);
				return response;
				
			} catch (Exception e) {
				response.getErros().add("Ocorreu o seguinte erro ao tentar buscar as legislaçoes dos movimentos: "+ e.getMessage());
				System.out.println(e.getMessage());
				return response;
			}
		}



		public Response<List<Integer>> buscarAnosMovimentoFp(int clienteid) {
			Response<List<Integer>> response = new Response<>();
			try {
				
				List<AliquotasMes> listAli = repo.buscarAliquotasMovimentoClienteFP(clienteid);
				List<Integer> anos = new ArrayList<>();
				for (AliquotasMes al: listAli) {
					if (!anos.contains(al.getAno())) {
						anos.add(al.getAno());
					}
					
				}
				Collections.sort(anos);
				response.setDados(anos);
				return response;
			} catch (Exception e) {
				response.getErros().add("Ocorreu o seguinte erro ao tentar buscar os anos com movimentação: "+ e.getMessage());
				System.out.println(e.getMessage());
				return response;
			}
		}


	
    

		

}// fecha classe
