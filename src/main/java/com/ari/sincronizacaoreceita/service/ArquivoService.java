package com.ari.sincronizacaoreceita.service;

import com.ari.sincronizacaoreceita.enums.StatusEnum;
import com.ari.sincronizacaoreceita.exception.BusinessException;
import com.ari.sincronizacaoreceita.exception.CriarReceitaException;
import com.ari.sincronizacaoreceita.model.Receita;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class ArquivoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArquivoService.class);
	private static final String CABECALHO_RESULTADO = "agencia;conta;saldo;status;resultado\n";

	private final ReceitaService receitaService ;

	@Autowired
	public ArquivoService(ReceitaService receitaService) {
		this.receitaService = receitaService;
	}

	/**
	 * Lê uma lista de arquivos obtendo os dados e transformando-os para criar um novo arquivo
	 * @param arquivos
	 */
	public void ler(String[] arquivos) {
		for (String arquivo : arquivos) {
			LOGGER.info("Lendo arquivo {}", arquivo);

			try (RandomAccessFile reader = new RandomAccessFile(arquivo, "r");
			     FileChannel channel = reader.getChannel()) {
				String texto = getTextoArquivo(channel);
				LOGGER.info(texto);

				List<Receita> receitaList = executarEnvioReceitas(texto);

				String caminhoResultado = arquivo.substring(0, arquivo.indexOf(".")) + "-resultado.csv";
				gerarArquivoResultado(caminhoResultado, receitaList);
				LOGGER.info("Arquivo {} gerado com sucesso!", caminhoResultado);
			} catch (IOException | InterruptedException e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gerar um arquivo resultante dos dados obtidos inicialmente
	 * @param caminho
	 * @param receitaList
	 */
	private void gerarArquivoResultado(String caminho, List<Receita> receitaList) {
		try (FileWriter writer = new FileWriter(caminho)) {
			writer.write(CABECALHO_RESULTADO);
			receitaList.forEach(
					receita -> gerarLinhaResultado(writer, receita)
			);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gera uma linha separada com à partir dos dados da Receita
	 * @param writer
	 * @param receita
	 */
	private void gerarLinhaResultado(FileWriter writer, Receita receita) {
		try {
			writer.write(receita.toCSVLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Busca e retorna o texto completo contido em um arquivo
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	private String getTextoArquivo(FileChannel channel) throws IOException {
		int bufferSize = 1024;
		if (bufferSize > channel.size()) {
			bufferSize = (int) channel.size();
		}

		ByteBuffer buff = ByteBuffer.allocate(bufferSize);
		channel.read(buff);
		buff.flip();
		return new String(buff.array());
	}

	/**
	 * Inicia o processo de envio das receita e retorna o resultado de cada uma delas
	 * @param texto
	 * @return
	 * @throws InterruptedException
	 */
	private List<Receita> executarEnvioReceitas(String texto) throws InterruptedException {
		String[] linhas = texto.split("\n");
		List<Receita> receitaList = new ArrayList<>();

		for (String linha : linhas) {
			String[] campos = linha.split(";");
			if (linha.equals(linhas[0])) {
				validarCabecalho(campos);
				continue;
			}
			Receita receita = criarReceitaEAtualizarConta(campos);
			receitaList.add(receita);
		}
		return receitaList;
	}

	/**
	 * Cria e atualiza uma conta com os campos informados
	 * @param campos
	 * @return
	 * @throws InterruptedException
	 */
	private Receita criarReceitaEAtualizarConta(String[] campos) throws InterruptedException {
		Receita receita = criarReceita(campos);

		if (isNull(receita)) {
			throw new CriarReceitaException("Não foi possível criar uma receita com os dados informados!");
		}

		Boolean resultado = receitaService.atualizarConta(receita);
		receita.setResultado(resultado);
		return receita;
	}

	/**
	 * Cria uma nova Receita
	 * @param campos
	 * @return
	 */
	private Receita criarReceita(String[] campos) {
		try {
			if (hasQuantidadeCamposCorreta(campos)) {
				return Receita.builder()
						.agencia(campos[0])
						.conta(campos[1])
						.saldo(new BigDecimal(campos[2].replaceAll(",", ".")))
						.status(StatusEnum.valueOf(campos[3]))
						.build();
			}
		} catch (RuntimeException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Valida se o cabeçalho é válido
	 * @param campos
	 */
	private void validarCabecalho(String[] campos) {
		if (!hasQuantidadeCamposCorreta(campos)) {
			throw  new BusinessException("Quantidade de colunas inválida!");
		}
	}

	/**
	 * Valida se a linha do cabeçalho contém a quantidade correta de colunas
	 * @param campos
	 * @return
	 */
	private boolean hasQuantidadeCamposCorreta(String[] campos) {
		return campos.length == 4;
	}
}
