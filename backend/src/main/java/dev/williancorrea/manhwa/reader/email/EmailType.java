package dev.williancorrea.manhwa.reader.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

  WORK_ADDED("work-added", "Nova Obra Adicionada"),
  NEW_CHAPTERS("new-chapters", "Novos Capítulos Disponíveis"),
  SCRAPER_ERROR("scraper-error", "Erro Durante Scraper");

  private final String templateName;
  private final String subject;
}
