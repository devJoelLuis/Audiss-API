package audinss.servico;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import audinss.DTO.FcLegislacaoDtInicioDTO;
import audinss.entidades.AliquotasMes;
import audinss.entidades.Cliente;
import audinss.entidades.FolhaCorrecao;
import audinss.entidades.Movimento;
import audinss.entidades.VerbaPadrao;
import audinss.repository.AliquotasMesRepository;
import audinss.repository.ClienteRepository;
import audinss.repository.FolhaCorrecaoCustomQuery;
import audinss.repository.FolhaCorrecaoRepository;
import audinss.repository.LegislacaoRepository;
import audinss.repository.MovimentoRepository;
import audinss.repository.VerbaPadraoRepository;
import audinss.response.Response;

@Service
public class VerbaPadraoService {
	
	@Autowired
	private VerbaPadraoRepository repo;
	
	@Autowired
	private ClienteRepository repoCli;
	
	@Autowired
	private MovimentoRepository repoMov;
	
	@Autowired
	private FolhaCorrecaoRepository repoFc;
	
	@Autowired
	private FolhaCorrecaoCustomQuery repoFccQuery;
	
	@Autowired
	private LegislacaoRepository repoLeg;
	
	@Autowired
	private AliquotasMesRepository repoAli;
	


	
	
	//cadastrar
	@Transactional
	public Response<VerbaPadrao> cadastrar(VerbaPadrao vp) {
		Response<VerbaPadrao> response = new Response<>();
		try {
			
			validarVerba(vp);
			Integer idcliente = vp.getCliente().getId();
			Optional<Cliente> cliOp = repoCli.findById(idcliente);
			if (!cliOp.isPresent()) {
				throw new Exception("Não foi possível encontrar um cliente com id: " + idcliente);
			}
			vp.setCliente(cliOp.get());
			VerbaPadrao vpb = repo.save(vp);
			
			// ******* gerar fc se existir  ********
			  if (repoFc.existsByClienteId(idcliente)) {
				  // buscar a lista de legislacoes do cliente que contem a fc configurada
				  List<FcLegislacaoDtInicioDTO> legs = repoFccQuery.buscarPorLegislacaoEClienteIdFC(idcliente);
				 
				  
				  //criar uma fc para a nova verba padra para cada legislacao
					  
				  for (FcLegislacaoDtInicioDTO lg: legs) {
					  FolhaCorrecao fc = new FolhaCorrecao();
					  fc.setCliente(cliOp.get());
					  fc.setConfigurado(false);
					  fc.setDataInicio(lg.getDataInicio());
					  fc.setFc(vp.getFp());
					  fc.setLegislacao(repoLeg.findById(lg.getLegislacaoId()).get());
					  fc.setVerbaPadrao(vpb);
					 repoFc.save(fc);			
					  
				  }
				  
			  }
			
			// ***** fim gerar fc ******
			  
			  //buscar todos as aliquotas mes existentes e cria um novo movimento com a verba criada.
			  List<AliquotasMes> aliquotas = repoAli.findByClienteId(idcliente);
			  
			  for (AliquotasMes am: aliquotas) {
				  //verificar se aliquota possui a verba que está sendo cadastrada
				  if (!repoMov.existsByAliquotasMesIdAndDescricao(am.getId(), vp.getDescricao())) {
					  //criar um movimento com a nova verba padrao, só que com valores zerados e fp igual a fc
					  
					  //buscar as legislações dos movimentos do cliente
					  List<Object[]> legsObj = repoMov.buscarLegislacoesMovimentosComFp(idcliente);
					  
					  //criar um novo movimento com a nova verba
					  for (Object[] obj: legsObj) {
						  String legislacao = (String) obj[0];
						  
						  Movimento mv = new Movimento();
						  mv.setAliquotasMes(am);
						  mv.setBaseCalcInss(0D);
						  mv.setCodigo(vp.getCodigo());
						  mv.setDescricao(vp.getDescricao());
						  mv.setFc(vp.getFp());
						  mv.setFp(vp.getFp());
						  mv.setLegislacao(legislacao.trim());
						  mv.setTipo(vp.getTipo());
						  mv.setValorInssFp(0D);
						  mv.setValorPagar(0D);
						  mv.setValorProvento(0D);
						  mv.setValorRecuperar(0D);
						  repoMov.save(mv);
					  }
					 
					
				  }
			  }
			
			response.setDados(vpb);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar cadastrar um verba: "+ e.getMessage());
	         System.out.println(e.getMessage());	
			
			return response;
		}
	}
	
	
	private void validarVerba(VerbaPadrao vp) throws Exception {
		if (vp == null) {
			throw new Exception("a verba está null");
		}
		if (vp.getDescricao() == null) {
			throw new Exception("A descrição está nula");
		}
		if (vp.getDescricao().equals("")) {
			throw new Exception("Descrição não informada");
		}
		Long count = repo.countByCodigoAndClienteId(vp.getCodigo(), vp.getCliente().getId().intValue());
		if (count > 0) {
			VerbaPadrao v = repo.findByCodigoAndClienteId(vp.getCodigo(), vp.getCliente().getId());
			throw new Exception("O código "+vp.getCodigo()+" já pertence a verba "+v.getDescricao());
		}
	}


	//editar normal
	public Response<VerbaPadrao> alterar(VerbaPadrao vp) {
		Response<VerbaPadrao> response = new Response<>();
		try {
			Optional<VerbaPadrao> vpOp = repo.findById(vp.getId());
			if (!vpOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma verba com o id: "+ vp.getId());
			}
			Optional<Cliente> cliOp = repoCli.findById(vp.getCliente().getId());
			if (!cliOp.isPresent()) {
				throw new Exception("Não foi possível encontrar um cliente com o id: "+ vp.getCliente().getId());
			}
			VerbaPadrao vpb = vpOp.get();
			BeanUtils.copyProperties(vp, vpb);
			vpb.setCliente(cliOp.get());
		    response.setDados(repo.save(vpb));
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar alterar um verba: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	
	// editar alterando os movimentos já lançados
	public Response<VerbaPadrao> alterarVerbaEMovimentos(VerbaPadrao vp) {
		Response<VerbaPadrao> response = new Response<>();
		try {
			Optional<VerbaPadrao> vpOp = repo.findById(vp.getId());
			if (!vpOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma verba com o id: "+ vp.getId());
			}
			Integer idcliente = vp.getCliente().getId();
			Optional<Cliente> cliOp = repoCli.findById(vp.getCliente().getId());
			if (!cliOp.isPresent()) {
				throw new Exception("Não foi possível encontrar um cliente com o id: "+ idcliente);
			}
			VerbaPadrao vpb = vpOp.get();
			BeanUtils.copyProperties(vp, vpb);
			vpb.setCliente(cliOp.get());
			
			
			List<Movimento> movimentos = repoMov.buscarMovimentosClienteIdCodigoDescricao(idcliente, vpb.getCodigo());
			
			for (Movimento m: movimentos) {
				m.alteraMovimetoPorVerbaPadrao(vpb);
				// recalcular verba
				if (!m.getLegislacao().equals("FP")) {
					Double totalAliquota = m.getAliquotasMes().getInssEmpresa() + m.getAliquotasMes().getInssRat() + m.getAliquotasMes().getInssTerceiros();
					if (m.getTipo().equals("PROVENTO")) {
						m.setBaseCalcInss(m.getValorProvento());
						
						if (m.getFc().equals("T") && m.getFp().equals("NT")) {
							if (m.getValorProvento() > 0 && totalAliquota > 0) {
								m.setValorRecuperar(0D);
								m.setValorPagar((m.getValorProvento() * totalAliquota) / 100);
								m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
							}
						}
						if (m.getFc().equals("NT") && m.getFp().equals("T")) {
							if (m.getValorProvento() > 0 && totalAliquota > 0) {
								m.setValorRecuperar((m.getValorProvento() * totalAliquota) / 100);
								m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
								m.setValorPagar(0D);
							}
						}
						if (m.getFc().equals("NT") && m.getFp().equals("NT")) {
							m.setValorPagar(0D);
							m.setValorRecuperar(0D);
							m.setBaseCalcInss(0D);
							m.setValorInssFp(0D);
						}
						if (m.getFc().equals("T") && m.getFp().equals("T")) {
							m.setValorPagar(0D);
							m.setValorRecuperar(0D);
							m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
						}
						
					} else if (m.getTipo().equals("DESCONTO")){
						m.setValorProvento(0D);
						if (m.getFc().equals("T") && m.getFp().equals("NT")) {
							if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
								m.setValorRecuperar((m.getValorProvento() * totalAliquota) / 100);
								m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
								m.setValorPagar(0D);
							}
						}
						if (m.getFc().equals("NT") && m.getFp().equals("T")) {
							if (m.getBaseCalcInss() != 0D && totalAliquota != 0D) {
								m.setValorRecuperar(0D);
								m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
								m.setValorPagar((m.getValorProvento() * totalAliquota) / 100);
							}
						}
						if (m.getFc().equals("NT") && m.getFp().equals("NT")) {
							m.setValorPagar(0D);
							m.setValorRecuperar(0D);
							m.setValorInssFp(0D);
						}
						if (m.getFc().equals("T") && m.getFp().equals("T")) {
							m.setValorPagar(0D);
							m.setValorInssFp((m.getValorProvento() * totalAliquota) / 100);
							m.setValorRecuperar(0D);
						}
					}
				}
				
				repoMov.save(m);
			}
			
			vp = repo.save(vpb);
			
		    response.setDados(vp);
			return response;
			
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar alterar um verba: "+ e.getMessage());
			return response;
		}
	}
	
	
	
	


	//excluir
	@Transactional
	public Response<String> excluir(int id) {
		Response<String> response = new Response<>();
		try {
			Optional<VerbaPadrao> vpOp = repo.findById(id);
			if (!vpOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma verba com o id: "+ id);
			}
			VerbaPadrao vp = vpOp.get();
			Integer idcliente = vp.getCliente().getId();
			List<FolhaCorrecao> fcs = repoFc.findByClienteIdAndVerbaPadraoId(idcliente, id);
			repoFc.deleteAll(fcs);
			repo.delete(vp);
			response.setDados("ok");
			return response;
		} catch (Exception e) {
		  response.getErros().add("Ocorreu um erro ao tentar excluir uma verba: "+ e.getMessage());
		  return response;
		}
	}
	
	
	
	
	
	//getid
	public Response<VerbaPadrao> getId(int id) {
		Response<VerbaPadrao> response = new Response<>();
		try {
			Optional<VerbaPadrao> vpOp = repo.findById(id);
			if (!vpOp.isPresent()) {
				throw new Exception("Não foi possível encontrar uma verba com o id: "+ id);
			}
			response.setDados(vpOp.get());
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar uma verba: "+ e.getMessage());
			  return response;
		}
		
	}
	
	
	
	//getall
	public Response<List<VerbaPadrao>> getAll() {
		Response<List<VerbaPadrao>> response = new Response<>();
		try {
			List<VerbaPadrao> verbas = repo.findAllByOrderByCodigoAsc();
			response.setDados(verbas);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar todas as verbas: "+ e.getMessage());
			return response;
		}
	}


	public Response<List<VerbaPadrao>> getAllclienteid(int id) {
		Response<List<VerbaPadrao>> response = new Response<>();
		try {
			List<VerbaPadrao> verbas = repo. buscarPorClientePorId(id);
			response.setDados(verbas);
			return response;
		} catch (Exception e) {
			response.getErros().add("Ocorreu um erro ao tentar consultar todas as verbas: "+ e.getMessage());
			return response;
		}
	}
	

}//fecha classe
