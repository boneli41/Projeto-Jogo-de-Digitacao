# Digita Comigo — Sistema de Ensino de Digitação para Idosos

A inclusão digital de pessoas idosas passa, antes de qualquer outra coisa, por uma barreira simples e muitas vezes esquecida: saber onde estão as teclas e como usá-las com confiança. Para quem nunca teve contato extenso com um teclado, tarefas básicas como escrever uma mensagem, preencher um formulário ou buscar uma informação online podem se tornar desafios desproporcionalmente grandes — não pela falta de capacidade, mas pela falta de prática guiada e paciente.

O **Digita Comigo** nasceu como um projeto acadêmico para enfrentar exatamente esse problema. A proposta é oferecer um ambiente de prática de digitação pensado do zero para o público idoso: textos grandes e legíveis, ritmo sem pressão excessiva, feedback imediato e visual, e uma progressão que celebra cada pequena vitória em vez de punir o erro. Em vez de um manual técnico sobre como digitar, o sistema se comporta como um jogo — com pontos, vidas, sequências e níveis — porque a gamificação ajuda a manter o interesse e transforma a repetição necessária para aprender em algo mais leve e motivador.

A aplicação foi construída inteiramente em Java com Swing, e é organizada em módulos de dificuldade crescente — começando pelas letras minúsculas e avançando até pontuação — que o próprio jogador pode escolher por onde começar, respeitando seu ritmo e familiaridade prévia com o teclado.

## Como Executar

### Pré-requisito

Java JDK 17 ou superior instalado (testado com JDK 26).
Verificar com: `java -version`

### Windows

```
compilar.bat    ← compila todos os arquivos .java
executar.bat    ← inicia o jogo
```

### Terminal (qualquer SO)

```bash
# Compilar
javac -d out -encoding UTF-8 \
  Main.java \
  model/Player.java model/Exercise.java \
  model/LetterExercise.java model/WordExercise.java model/SentenceExercise.java \
  factory/ExerciseFactory.java \
  ui/TypingGame.java \
  ui/panels/BasePanel.java ui/panels/KeyboardPanel.java \
  ui/panels/MenuPanel.java ui/panels/GamePanel.java ui/panels/ResultPanel.java

# Executar
java -cp out Main
```

> O jogo tenta carregar o **FlatLaf** (look & feel moderno) em tempo de execução, caso a biblioteca esteja disponível no classpath. Se não estiver, ele continua funcionando normalmente com o Look & Feel padrão do Swing.

## Funcionalidades

### Gamificação

| Elemento | Descrição |
|---|---|
| Estrelas (★★★) | Avaliação de 0 a 3 estrelas por exercício (precisão + velocidade) |
| XP e Níveis | Cada exercício concluído rende pontos; acumular XP sobe o nível de habilidade |
| Vidas (♥♥♥) | Três vidas; perde uma ao terminar com 0 estrelas ou esgotar o tempo |
| Sequência | Contador de exercícios completados sem perder vida |
| Conquistas | Medalhas desbloqueadas por marcos (ex: "Primeiro Passo", "Imparável") |
| Módulos | Cinco categorias de prática, escolhidas livremente no menu antes de começar |

### Módulos de Prática

O jogador escolhe por qual módulo começar, direto no menu inicial. Ao escolher um módulo, a campanha segue dele até o final (módulo 5):

| Módulo | Categoria | Ícone no menu |
|---|---|---|
| 1 | Minúsculas | abc |
| 2 | Maiúsculas | ABC |
| 3 | Números | 123 |
| 4 | Acentos | áéí |
| 5 | Pontuação | .,! |

### Progressão de Nível (XP)

Em paralelo ao módulo escolhido, o jogador acumula XP que define seu nível de habilidade, exibido na barra superior:

| Nível | Nome | XP necessário |
|---|---|---|
| 1 | Iniciante | 0 |
| 2 | Aprendiz | 800 |
| 3 | Intermediário | 1.700 |
| 4 | Avançado | 2.700 |
| 5 | Especialista | 3.800 |

### Teclado Visual

Um teclado ABNT2 é desenhado na tela do jogo, destacando em tempo real apenas a **próxima tecla** que o jogador precisa pressionar:

- 🟩 **Verde** — próxima tecla, sem Shift
- 🟨 **Amarelo** — próxima tecla, com Shift (maiúsculas, símbolos e alguns acentos)
- 🟥 **Vermelho** — Backspace, aceso quando o jogador erra a tecla esperada

Para acentos (ex: `ã`, `é`, `ô`), o teclado destaca a tecla-morta correspondente junto com a vogal-base, já que digitar um acento em ABNT2 exige dois toques em sequência.

Um segundo teclado, com anotações explicando cada tecla especial (Shift, Tab, Enter, Caps Lock, etc.), pode ser aberto a qualquer momento pelo menu, clicando em "Conheça o Teclado".

### Tela de Resultado

Após cada exercício são exibidos:

- Avaliação em estrelas
- Pontuação total, sequência e vidas restantes
- PPM — Palavras Por Minuto digitadas
- Tempo — duração formatada em m:ss
- Nível atual de XP

O avanço para o próximo exercício acontece de três formas, todas disponíveis ao mesmo tempo: automaticamente após 5 segundos, pressionando **Enter**, ou clicando no botão **Próximo**.

### Ranking

A pontuação de cada jogador é salva localmente em `ranking.txt`. O menu exibe o top 3 em um card, e um clique nele abre um diálogo com o ranking completo.

### Feedback em Tempo Real

Cada letra digitada recebe coloração instantânea na frase exibida:

- 🟢 Verde — letra correta
- 🔴 Vermelho — letra errada
- 🔵 Azul claro — posição atual do cursor

## Mecânica do Jogo

### Como ganhar pontos

```
XP = recompensa_base × precisão × (1 + bônus_velocidade)
```

- **Precisão** = letras corretas ÷ total de letras (0% a 100%)
- **Bônus de velocidade** = até +50% para quem termina antes da metade do tempo

### Como ganhar estrelas

| Estrelas | Condição |
|---|---|
| ★★★ | Precisão ≥ 95% e tempo usado ≤ 70% do limite |
| ★★ | Precisão ≥ 80% |
| ★ | Precisão ≥ 60% |
| Sem estrela | Precisão < 60% ou tempo esgotado |

### Como perder vida

O jogador perde 1 vida (♥) ao terminar um exercício com 0 estrelas (erro excessivo ou tempo esgotado). Com 0 vidas, o jogo é encerrado e é necessário voltar ao menu para recomeçar.

## Tecnologias

| Item | Detalhe |
|---|---|
| Linguagem | Java |
| Interface | Java Swing, com suporte opcional ao FlatLaf |
| Layout | CardLayout, BorderLayout, GridLayout, BoxLayout, GridBagLayout |
| Texto estilizado | JTextPane + StyledDocument (coloração letra a letra) |
| Padrão de projeto | Factory Method (`ExerciseFactory`) |
| Codificação | UTF-8 (suporte completo a caracteres acentuados) |

## Objetivo

Promover a inclusão digital de pessoas idosas, facilitando o uso de computadores por meio do desenvolvimento da habilidade de digitação.
