# Shopee Affiliate Telegram Bot

Um bot Telegram desenvolvido em Spring Boot que permite a afiliados Shopee configurar suas credenciais (AppID e Secret) e consultar relatórios de cliques diretamente no chat.

---

## Funcionalidades

* **`/config <AppID> <Secret>`**: armazena em memória as credenciais do afiliado.
* **`/clicks [YYYY-MM-DD] [YYYY-MM-DD]`**: retorna relatório de cliques em um período (padrão últimos 7 dias).

---

## Pré-requisitos

* Java 21 ou superior
* Maven 3.6+
* Conta de afiliado Shopee com acesso à Open API
* Bot Telegram criado no BotFather (token e username)

---

## Estrutura do Projeto

```
shopee-affiliate-telegram-bot/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/agamatec/shopee_affiliate_telegram_bot/
    │   │   ├── bot/ShopeeBot.java
    │   │   ├── model/AffiliateCredentials.java
    │   │   ├── model/Click.java
    │   │   ├── service/AffiliateService.java
    │   │   ├── service/ShopeeApiService.java
    │   │   └── ShopeeAffiliateTelegramBotApplication.java
    │   └── resources/
    │       └── application.properties
    └── test/
```

---

## Configuração

Edite `src/main/resources/application.properties`:

```properties
spring.application.name=shopee-affiliate-telegram-bot

# Telegram Bot
telegram.bot.username=<SEU_BOT_USERNAME>
telegram.bot.token=<SEU_BOT_TOKEN>
```

> **Nota:** substitua `<SEU_BOT_USERNAME>` e `<SEU_BOT_TOKEN>` pelos valores fornecidos pelo BotFather.

---

## Como Executar

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/shopee-affiliate-telegram-bot.git
   cd shopee-affiliate-telegram-bot
   ```

2. Compile e inicie a aplicação:

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

3. No Telegram, abra o chat com o seu bot e envie:

   ```bash
   /config MEU_APP_ID MEU_SECRET
   ```

4. Consulte relatórios:

   ```bash
   /clicks
   /clicks 2025-05-01 2025-05-07
   ```

---

## Tratamento de Erros

* **10035**: acesso à API negado. Certifique-se de que sua conta de afiliado está habilitada no painel da Shopee.
* **Falha de rede**: verifique se sua máquina tem acesso à internet e não bloqueia `api.telegram.org`.

---

## Observações

* As credenciais são armazenadas em memória e se perdem ao reiniciar a aplicação. Para persistência, integre um banco de dados.
* Em Spring Boot 3, o bot é registrado manualmente via `TelegramBotsApi` no método `main`.

---

## Licença

Este projeto está licenciado sob a [MIT License](./LICENSE).
