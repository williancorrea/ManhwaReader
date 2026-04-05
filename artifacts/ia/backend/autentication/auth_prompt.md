## Objetivo:
- Criar um sistema de login com email e senha permitindo autenticação segura do usuário.

## Critérios 
- Validar e-mail e senha obrigatórios
- Validar se senha tem no minimo 8 caracteres
- Validar o formato de e-mail
- Retornar erro para credenciais inválidas
- Retornar token JWT ao autenticar com sucesso.
- Validar se este email já está cadastrado, deve ser unico por usuário
- Todos os textos devem estar no messages.properties

## Regras
- Senha deve ser armazenada em hash(bcrypt)
- Não expor detalhes sensíveis em erros

## Stack
- Angular
- Biblioteca de componentes (prime-ng)
- Java
- Spring Boot
- Junit

## Autenticação
- bcrypt
- JWT

## Arquitetura
- model
- service
- resource
- repository
- messages.properties

## Restrições
- Não concentrar tudo em um unico arquivo
- Seguir separação de  responsabilidades
- Código simples e **testavel**

## Cobertura de testes
- Todas as classes tem que 100% de taxa de cobertura de testes unitários





------
Com base no codigo atual, gere testes automatizados para o sistema de login:
crie:
- testes unitarios para cada camada se não existir
- Testes de integração para handlers (Resources)

Considere:
-Login com sucesso
-credenciais inválidas
- campos obrigatorios
- formato de email inválido


/clear (Limpar o contexto)
Analise o codigo gerado e sugira melhorias estruturais, de organização, segurança , boas práticas
Agora pense como um QA experiente e sugira edge cases e criterios que ainda não estão cobertos nos testes

Com base no codigo atual, gere uma documentação técnica e Swagger/OpenAPI do sistema de login
Inclua:
- Visão geral do sistema
- arquitetura utilizada
- fluxo de autenticação
- principais componentes e responsabilidades