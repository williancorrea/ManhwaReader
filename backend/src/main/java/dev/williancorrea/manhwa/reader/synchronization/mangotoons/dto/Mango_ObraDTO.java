package dev.williancorrea.manhwa.reader.synchronization.mangotoons.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Mango_ObraDTO {

  public long id;

  public String nome;

  public String descricao;

  public String imagem;

  public int formato_id;

  public int status_id;

  public int total_capitulos;

  public int total_lidos;

  public String criada_em;

  public String atualizada_em;

  public Integer pasta_cdn_id;

  public Long id_externo;

  public int views_dia;

  public int views_semana;

  public int views_mes;

  public int views_total;

  public Map<String, Object> ultima_atualizacao_views;

  public boolean vip;

  public String link_lura;

  public String titulo_alternativo;

  public boolean oculta;

  public String banner_imagem;

  public String formato_nome;

  public String status_nome;

  public List<Mango_TagDTO> tags;

  public List<Mango_CapituloDTO> capitulos;
}