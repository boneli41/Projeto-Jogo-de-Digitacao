# Digita Comigo — Site Multi-página (HTML/CSS/JS puro)

Reestruturação da versão web do jogo (que antes era um SPA em React) para uma arquitetura
tradicional de site: cada tela é uma página `.html` de verdade, com um único `.css` e um único
`.js` compartilhados. O projeto React original continua intacto em
`Projeto-Jogo-de-Digitacao-main/` — este aqui é uma base nova, construída a partir dele.

## Estrutura

```
Projeto-Jogo-de-Digitacao-html/
├── backend/                  Node + Express — API de exercícios/ranking e serve o site
│   ├── src/
│   │   ├── server.js          rotas da API + express.static(../site)
│   │   ├── db.js               SQLite (ranking.db)
│   │   ├── game/exerciseFactory.js
│   │   └── routes/{exercises.js, ranking.js}
│   └── data/exercises/*.txt   as 5 listas de frases
├── site/                     o site em si — HTML/CSS/JS puro
│   ├── index.html             página inicial (menu)
│   ├── jogo.html               tela de digitação
│   ├── resultado.html          tela de resultado
│   ├── style.css               TODO o CSS do site
│   ├── script.js                TODA a lógica (menu, jogo, resultado, API, teclado, sons)
│   └── assets/*.png            imagens
└── executar.bat              atalho de 2 cliques
```

## Como rodar

Requer Node.js 22+ (usa `node:sqlite`, sem dependências de compilação).

### Atalho (Windows)

Dê dois cliques em **`executar.bat`** — ele instala as dependências (só na primeira vez), sobe o
servidor numa janela e abre `http://localhost:3001` no navegador.

### Manual

```bash
cd backend
npm install
npm run dev
```

Abra `http://localhost:3001` no navegador. **Não existe mais servidor de frontend separado** —
o mesmo Node que serve a API (`/api/...`) também serve os arquivos do site (`site/`), então tudo
roda numa porta só, sem CORS pra se preocupar.

## Como a navegação funciona

Diferente da versão React (SPA), aqui cada tela é uma página HTML separada — clicar em "Iniciar"
ou "Próximo Desafio" navega de verdade para outro arquivo (`jogo.html`, `resultado.html`). Como
o JavaScript reinicia a cada página carregada, o estado da partida (jogador, exercícios, índice
atual) é salvo em `sessionStorage` antes de cada navegação, e lido de volta assim que a próxima
página carrega. Um `localStorage` separado guarda o autosave de recuperação (fechar a aba,
bateria acabar).

## O que tem

Tudo que já existia na versão React foi portado para este formato multi-página:

- Campanha de exercícios (5 módulos × 8 frases), com escolha de módulo inicial no menu
- XP, estrelas, vidas, sequência — mesmas fórmulas do Java original
- Teclado visual ABNT2, destacando a próxima tecla em tempo real
- Referência estática do teclado ("Conheça o Teclado") no menu
- Pausar/Continuar durante o jogo
- Barra de progresso de XP na lateral
- Sons (Web Audio, sem arquivos externos) com liga/desliga
- Tema claro/escuro
- Autosave (recuperação após fechar a aba)
- Confirmação ao sair da partida
- Atalhos: `Esc` sai, `Enter` avança no resultado (que também avança sozinho em 5s)
- Bloqueio de início/avanço duplo
- Ranking real, persistido em SQLite

## Sobre a campanha encadeada (Módulo 1 de 5 / Exercício 1 de 8)

Ao escolher um módulo no menu (ex: "Números"), a campanha não fica só nesses 8 exercícios — ela
segue automaticamente por esse módulo e todos os seguintes até o 5º (Números → Acentos →
Pontuação = 24 exercícios seguidos, por exemplo). É por isso que aparecem dois contadores ao
mesmo tempo: o de cima (`Módulo X de Y — Exercício N de 8 do módulo`) mostra o progresso dentro
do módulo atual, e o de baixo (`Exercicio N de 24/40`) mostra o progresso na campanha inteira.

Essa é uma escolha de design, não uma limitação técnica — a API (`GET /api/exercises/campaign?startLevel=`)
só devolve o que o front pede; ela poderia devolver apenas os 8 exercícios de um módulo se o
front pedisse assim. O motivo de ter sido feito encadeado foi seguir o que o próprio README do
projeto original já descrevia ("a campanha segue dele até o final") e o que uma versão revisada
do `GamePanel.java` (encontrada em paralelo, com o teclado visual) já implementava.

## Correções feitas depois da primeira versão

- **Aviso de "sair da página" aparecendo entre exercícios** — o `beforeunload` do navegador
  disparava em qualquer troca de tela, inclusive quando o próprio jogo navegava sozinho (fim de
  exercício → resultado, avanço automático → próximo exercício). Corrigido com uma flag
  (`leavingIntentionally`) que só deixa o aviso nativo aparecer quando o usuário tenta fechar a
  aba por conta própria — clicar em "Sair"/`Esc` continua pedindo confirmação normalmente, só que
  por um `confirm()` próprio, não pelo aviso do navegador.
- **Ranking não salvava ao sair direto do jogo** — só era salvo saindo pela tela de resultado.
  Corrigido: `Sair`/`Esc` agora também grava o resultado antes de voltar ao menu.

## Onde está cada coisa no código

Como tudo mora num arquivo só por tipo, aqui vai um mapa de `script.js` (numerado em blocos
comentados dentro do próprio arquivo):

1. Lógica do jogo (XP, estrelas, vidas)
2. Teclado visual ABNT2 (layout + destaque de tecla)
3. Sons
4. Tema claro/escuro
5. Estado entre páginas (sessionStorage + autosave)
6. Chamadas à API
7. Página Menu (`initMenu`)
8. Página Jogo (`initGame`)
9. Página Resultado (`initResult`)
10. Dispatch — decide qual `init` chamar, baseado em `<body data-page="...">`
