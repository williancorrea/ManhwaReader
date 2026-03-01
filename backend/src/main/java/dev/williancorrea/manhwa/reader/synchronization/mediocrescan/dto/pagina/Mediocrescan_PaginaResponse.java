package dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.pagina;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.diversos.Mediocrescan_ReacoesDTO;
import dev.williancorrea.manhwa.reader.synchronization.mediocrescan.dto.obra.Mediocrescan_ObraCompletaDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mediocrescan_PaginaResponse {

  private Long id;
  private String nome;
  private String descricao;
  private Integer numero;
  private Object volume;
  private String imagem;
  private String tipo;
  @JsonProperty("lancado_em")
  private Object lancadoEm;
  @JsonProperty("criado_em")
  private OffsetDateTime criadoEm;
  @JsonProperty("total_comentarios")
  private Integer totalComentarios;
  private Mediocrescan_ObraCompletaDTO obra;
  private Mediocrescan_ReacoesDTO reacoes;
  @JsonProperty("liberado_em")
  private Object liberadoEm;
  private List<Mediocrescan_PaginaImagemDTO> paginas;
  private List<Object> paginas2;
  @JsonProperty("conteudo_texto")
  private String conteudoTexto;
}